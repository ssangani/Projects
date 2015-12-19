#!/usr/bin/python2.7
#
# Interface for the assignement
#

import psycopg2

DATABASE_NAME = 'dds_assgn1'


def getopenconnection(user='postgres', password='admin', dbname='dds_assgn1'):
    return psycopg2.connect("dbname='" + dbname + "' user='" + user + "' host='localhost' password='" + password + "'")


def loadratings(ratingstablename, ratingsfilepath, openconnection):
    print 'inside load...'
    cur = openconnection.cursor()
    createTableQuery = """CREATE TABLE IF NOT EXISTS %s ( 
    	id SERIAL NOT NULL, 
    	UserId INT NULL, 
    	tmp1 CHAR(45) NULL, 
    	MovieId INT NULL, 
    	tmp2 CHAR(45) NULL, 
    	Rating REAL NULL, 
    	tmp3 CHAR(45) NULL, 
    	ts CHAR(45), 
    	PRIMARY KEY(id))
    	"""	% (ratingstablename)
    print 'creating table...'
    cur.execute(createTableQuery)
    print 'loading data...'
    loadDataQuery = """COPY %s (UserId, tmp1, MovieId, tmp2, Rating, tmp3, ts)
    	FROM \'%s\' 
    	WITH (FORMAT CSV, DELIMITER \':\')
    	""" %(ratingstablename, ratingsfilepath)
    cur.execute(loadDataQuery)
    print 'data loaded...'
    cleanUpQuery = """
    	ALTER TABLE %s DROP COLUMN tmp1;
    	ALTER TABLE %s DROP COLUMN tmp2;
    	ALTER TABLE %s DROP COLUMN tmp3;
    	ALTER TABLE %s DROP COLUMN ts;
    	""" % (ratingstablename, ratingstablename, ratingstablename, ratingstablename)
    print 'cleaning up...'
    cur.execute(cleanUpQuery)
    cur.close()


def rangepartition(ratingstablename, numberofpartitions, openconnection):
    print 'inside range partition...'
    if (numberofpartitions <= 10):
        cur = openconnection.cursor()
        countQuery = 'SELECT COUNT(*) FROM %s ' % (ratingstablename)
        cur.execute(countQuery)
        totalTuples = cur.fetchone()[0]
        print 'number of partitions %d' % (numberofpartitions)
        print 'total tuples %d' % (totalTuples)
        tupleLimit = totalTuples / numberofpartitions
        currRange = 0.0
        minRange = []
        maxRange = []
        print 'calculating range for partitions'
        for i in range(0, numberofpartitions):
            minRange.append(-1)
            maxRange.append(-1)

        for i in range(0, numberofpartitions):
            minRange[i] = currRange
            tupleCount = 0
            while (tupleCount < tupleLimit):
                #print 'tuple count - %d' % (tupleCount)
                query = 'SELECT COUNT (*) FROM %s WHERE Rating = %f ' % (ratingstablename, currRange)
                cur.execute(query)
                count = cur.fetchone()[0]
                #print 'tuples with rating %f = %d' %(currRange, count)
                tupleCount += count
                if ((tupleCount > tupleLimit) & (maxRange[i] != -1)):
                    break
                else:
                    maxRange[i] = currRange
                    currRange += 0.5

        maxRange[numberofpartitions - 1] = 5.0

        print 'creating master table...'
        masterTableName = 'range' + ratingstablename
        createMasterTableQuery = """CREATE TABLE %s(
            LIKE %s
            including defaults
            including constraints
            including indexes
        );
        """ % (masterTableName, ratingstablename)
        cur.execute(createMasterTableQuery)
        print 'creating child tables...'
        for i in range(0, numberofpartitions):
            createChildTableQuery = """CREATE TABLE %s%d (
                CONSTRAINT %s%d_pkey PRIMARY KEY (id),
                CONSTRAINT rating_check CHECK (Rating >= %f and Rating <= %f)
                )
                INHERITS(%s)
            """ % (masterTableName, i+1, masterTableName, i+1, minRange[i], maxRange[i], masterTableName)
            cur.execute(createChildTableQuery)
            #print 'range %d = %f to %f' % (i, minRange[i], maxRange[i])

        print 'creating trigger functions...'
        functionQuery = """CREATE OR REPLACE FUNCTION master_to_child_range_insert_trigger() 
            RETURNS TRIGGER AS $BODY$ 
            BEGIN 
        """
        for i in range(0, numberofpartitions):
            if (i == 0):
                cond = """IF (NEW.Rating >= %f AND NEW.Rating <= %f) 
                    THEN INSERT INTO %s%d VALUES (NEW.*);
                    """ % (minRange[i], maxRange[i], masterTableName, i+1)
            else:
                cond = """ELSIF (NEW.Rating >= %f AND NEW.Rating <= %f)
                    THEN INSERT INTO %s%d VALUES (NEW.*);
                    """ % (minRange[i], maxRange[i], masterTableName, i+1)
            functionQuery = functionQuery + ' \n' + cond

        functionTail = """
            ELSE
            RAISE EXCEPTION 'selection field out of range!';
            END IF;
            RETURN NULL;
            END;
            $BODY$
            LANGUAGE plpgsql;
            """
        functionQuery += functionTail
        cur.execute(functionQuery)

        triggerQuery = """CREATE TRIGGER master_range_trigger
            BEFORE INSERT ON %s
            FOR EACH ROW EXECUTE PROCEDURE master_to_child_range_insert_trigger();
            """ % (masterTableName)
        cur.execute(triggerQuery)

        print 'inserting data entries...'
        dataQuery = """INSERT INTO %s
            SELECT * FROM %s
            """ % (masterTableName, ratingstablename)
        cur.execute(dataQuery)
        print 'range partioning finished...'
        
        cur.close()
    else:
        print 'Maximum 10 Ranged partitions allowed'

def roundrobinpartition(ratingstablename, numberofpartitions, openconnection):
    print 'inside round robin partition...'
    masterTableName = 'roundrobin' + ratingstablename
    cur = openconnection.cursor()
    print 'creating master table...'
    createMasterTableQuery = """CREATE TABLE IF NOT EXISTS %s ( 
            id SERIAL NOT NULL, 
            UserId INT NULL,  
            MovieId INT NULL, 
            Rating REAL NULL, 
            PRIMARY KEY(id))
            """ % (masterTableName)
    cur.execute(createMasterTableQuery)

    print 'creating child tables...'
    for i in range(0, numberofpartitions):
        createChildTableQuery = """CREATE TABLE %s%d (
            CONSTRAINT %s%d_pkey PRIMARY KEY (id),
            CONSTRAINT rating_check CHECK ((id %% %d) = %d)
            )
            INHERITS(%s)
        """ % (masterTableName, i, masterTableName, i, numberofpartitions, i, masterTableName)
        cur.execute(createChildTableQuery)

        resetQuery = 'ALTER SEQUENCE %s_id_seq RESTART WITH 1' % (masterTableName)

    print 'creating trigger functions...'
    functionQuery = """CREATE OR REPLACE FUNCTION master_to_child_round_robin_insert_trigger()
        RETURNS TRIGGER AS $BODY$
        DECLARE 
        lastId INT := (SELECT COALESCE(MAX(id), 0) FROM %s);
        tableNumber INT DEFAULT 0;
        BEGIN
        IF (NEW.id != NULL) THEN
        tableNumber = (NEW.id %% %d);
        ELSE
        tableNumber = (((lastId %% %d) + 1) %% %d);
        END IF;
    """ % (masterTableName, numberofpartitions, numberofpartitions, numberofpartitions)
    for i in range(0, numberofpartitions):
        if (i == 0):
            cond = """IF (tableNumber = %d) THEN
                INSERT INTO %s%d VALUES (NEW.*);
                """ % (i, masterTableName, i)
        else:
            cond = """ELSIF (tableNumber = %d) THEN
                INSERT INTO %s%d VALUES (NEW.*);
                """ % (i,masterTableName, i)
        functionQuery = functionQuery + ' \n' + cond

    functionTail = """
        ELSE
        RAISE EXCEPTION 'selection field out of range!';
        END IF;
        RETURN NULL;
        END;
        $BODY$
        LANGUAGE plpgsql;
        """
    functionQuery += functionTail
    cur.execute(functionQuery)

    triggerQuery = """CREATE TRIGGER master_round_robin_trigger
        BEFORE INSERT ON %s
        FOR EACH ROW EXECUTE PROCEDURE master_to_child_round_robin_insert_trigger();
        """ % (masterTableName)
    cur.execute(triggerQuery)

    print 'inserting data entries...'
    dataQuery = """INSERT INTO %s
        SELECT * FROM %s
        """ % (masterTableName, ratingstablename)
    cur.execute(dataQuery)
    print 'round robin partioning finished...'
    
    cur.close()


def roundrobininsert(ratingstablename, userid, itemid, rating, openconnection):
    masterTableName = 'roundrobin' + ratingstablename
    cur = openconnection.cursor()
    insertQuery = 'INSERT INTO %s (UserId, MovieId, Rating) VALUES (%d, %d, %f);' % (masterTableName, userid, itemid, rating)
    cur.execute(insertQuery)
    cur.close()


def rangeinsert(ratingstablename, userid, itemid, rating, openconnection):
    masterTableName = 'range' + ratingstablename
    cur = openconnection.cursor()
    insertQuery = 'INSERT INTO %s (UserId, MovieId, Rating) VALUES (%d, %d, %f);' % (masterTableName, userid, itemid, rating)
    cur.execute(insertQuery)
    cur.close()


def create_db(dbname):
    """
    We create a DB by connecting to the default user and database of Postgres
    The function first checks if an existing database exists for a given name, else creates it.
    :return:None
    """
    # Connect to the default database
    con = getopenconnection(dbname='postgres')
    con.set_isolation_level(psycopg2.extensions.ISOLATION_LEVEL_AUTOCOMMIT)
    cur = con.cursor()

    # Check if an existing database with the same name exists
    cur.execute('SELECT COUNT(*) FROM pg_catalog.pg_database WHERE datname=\'%s\'' % (dbname,))
    count = cur.fetchone()[0]
    if count == 0:
        cur.execute('CREATE DATABASE %s' % (dbname,))  # Create the database
    else:
        print 'A database named {0} already exists'.format(dbname)

    # Clean up
    cur.close()
    con.close()


# Middleware
def before_db_creation_middleware():
    # Use it if you want to
    pass


def after_db_creation_middleware(databasename):
    # Use it if you want to
    pass


def before_test_script_starts_middleware(openconnection, databasename):
    # Use it if you want to
    pass


def after_test_script_ends_middleware(openconnection, databasename):
    cur = openconnection.cursor()
    clearSchema = 'DROP SCHEMA PUBLIC CASCADE;CREATE SCHEMA PUBLIC;'
    cur.execute(clearSchema)
    #cleanupQuery = 'DROP DATABASE IF EXISTS %s' % (databasename)
    #cur.execute(cleanupQuery)
    cur.close()
    openconnection.close()

def delete_partitions(ratingstablename, openconnection):
    cur = openconnection.cursor()
    rangePartitionDatabaseName = 'range' + ratingstablename
    roundRobinPartitionDatabaseName = 'roundrobin' + ratingstablename
    print 'checking for partitions...'

    cur.execute('SELECT COUNT(*) FROM pg_catalog.pg_database WHERE datname IN (\'%s\',\'%s\')' % (rangePartitionDatabaseName, roundRobinPartitionDatabaseName))
    count = cur.fetchone()[0]
    if count != 0:
        print 'deleting partitions...'
        getChildTables = """SELECT c.relname AS child, p.relname AS parent
            FROM
            pg_inherits JOIN pg_class AS c ON (inhrelid=c.oid)
            JOIN pg_class as p ON (inhparent=p.oid)
            WHERE p.relname IN(\'%s\', \'%s\');
            """ % (rangePartitionDatabaseName, roundRobinPartitionDatabaseName)
        cur.execute(getChildTables)
        
        deletePartitions = '';
        childList = cur.fetchall()
        childListLength = len(childList)
        for i in  range(0, childListLength):
            dropChild = """
                ALTER TABLE %s NO INHERIT %s;
                DROP TABLE %s;
                """ % (childList[i][0], childList[i][1], childList[i][0])
            deletePartitions += dropChild
        deletePartitions += 'DROP TABLE %s;' % (rangePartitionDatabaseName)
        deletePartitions += 'DROP TABLE %s;' % (roundRobinPartitionDatabaseName)
        cur.execute(deletePartitions)
        
        cur.close()
        print 'partitions deleted...'
    else:
        print 'No partitions exist'

def delete_table(ratingstablename, openconnection):
    cur = openconnection.cursor()
    cur.execute('SELECT COUNT(*) FROM pg_catalog.pg_class WHERE relname =\'%s\'' % (ratingstablename))
    count = cur.fetchone()[0]
    if count != 0:
        cur.execute('DROP TABLE %s CASCADE' % (ratingstablename))
        cur.close()
    else:
        print 'Table doesn\'t exist'

if __name__ == '__main__':
    try:

        # Use this function to do any set up before creating the DB, if any
        before_db_creation_middleware()

        create_db(DATABASE_NAME)

        # Use this function to do any set up after creating the DB, if any
        after_db_creation_middleware(DATABASE_NAME)

        with getopenconnection() as con:
            con.set_isolation_level(psycopg2.extensions.ISOLATION_LEVEL_AUTOCOMMIT)
            # Use this function to do any set up before I starting calling your functions to test, if you want to
            before_test_script_starts_middleware(con, DATABASE_NAME)

            # Here is where I will start calling your functions to test them. For example,
            #loadratings('ratings', 'E:\\College\\512\\Assn1\\test_data.dat', con)
            #rangepartition('ratings', 3, con)
            #roundrobinpartition('ratings', 3, con)
            delete_partitions('ratings', con)
            delete_table('ratings', con)
            # ###################################################################################
            # Anything in this area will not be executed as I will call your functions directly
            # so please add whatever code you want to add in main, in the middleware functions provided "only"
            # ###################################################################################

            # Use this function to do any set up after I finish testing, if you want to
            #after_test_script_ends_middleware(con, DATABASE_NAME)

    except Exception as detail:
        print "OOPS! This is the error ==> ", detail
