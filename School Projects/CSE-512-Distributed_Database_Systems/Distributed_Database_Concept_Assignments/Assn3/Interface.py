#!/usr/bin/python3.4
#
# Interface for the assignement
#

import psycopg2
import threading
from queue import Queue
import time
import sys

DATABASE_NAME = 'dds_assgn1'


def getopenconnection(user='postgres', password='1234', dbname='dds_assgn1'):
    return psycopg2.connect("dbname='" + dbname + "' user='" + user + "' host='localhost' password='" + password + "'")


def loadratings(ratingstablename, ratingsfilepath, openconnection):
	print("inside load...")
	print("creating table %", (ratingstablename))
	cur = openconnection.cursor()
	createTableQuery = """CREATE TABLE IF NOT EXISTS "{}" (
		"UserId" bigint,
		"tmp1" character varying,
		"MovieId" bigint,
		"tmp2" character varying,
		"Rating" double precision,
		"tmp3" character varying,
		"ts" character varying)
		""".format(ratingstablename)
	print("creating table...")
	cur.execute(createTableQuery)
	print("loading data...")
	filePath= str(ratingsfilepath)
	loadDataQuery = """COPY "{}" ("UserId", "tmp1", "MovieId", "tmp2", "Rating", "tmp3", "ts")
		FROM {}
		WITH (FORMAT CSV, DELIMITER \':\')
		""".format(ratingstablename, "'"+filePath+"'")
	cur.execute(loadDataQuery)
	print("data loaded...")
	cleanUpQuery = """ALTER TABLE "{}" DROP COLUMN "tmp1";
		ALTER TABLE "{}" DROP COLUMN "tmp2";
		ALTER TABLE "{}" DROP COLUMN "tmp3";
		ALTER TABLE "{}" DROP COLUMN "ts";
		""".format(ratingstablename, ratingstablename, ratingstablename, ratingstablename)
	print("cleaning up...")
	cur.execute(cleanUpQuery)
	cur.close()

def loadUserData(userTableName, userDataFilePath, openConnection):
	print("inside user data load...")
	print("creating table %", (userTableName))
	cur = openConnection.cursor()
	createTableQuery = """CREATE TABLE IF NOT EXISTS "{}" (
		"UserId" bigint,
		"tmp1" character varying,
		"Name" character varying)
		""".format(userTableName)
	print("creating table...")
	cur.execute(createTableQuery)
	print("loading data...")
	filePath= str(userDataFilePath)
	loadDataQuery = """COPY "{}" ("UserId", "tmp1", "Name")
		FROM {}
		WITH (FORMAT CSV, DELIMITER \':\')
		""".format(userTableName, "'"+filePath+"'")
	cur.execute(loadDataQuery)
	print("data loaded...")
	cleanUpQuery = 'ALTER TABLE "{}" DROP COLUMN "tmp1"'.format(userTableName)
	print("cleaning up...")
	cur.execute(cleanUpQuery)
	cur.close()

def deletetable(tablename, openconnection):
	print('deleting table...')
	cur = openconnection.cursor()
	deleteTableQuery = 'DROP TABLE IF EXISTS %s CASCADE' % (tablename)
	cur.execute(deleteTableQuery)
	print('table deleted...')
	cur.close()


def deletePartitions(tablename, openconnection):
	print('deleting partitions...')
	cur = openconnection.cursor()
	getTableNamesFromMetaData = 'SELECT partitionName FROM partitions_meta_data'
	deleteTableQuery = 'DROP TABLE IF EXISTS %s CASCADE'
	cur.execute(getTableNamesFromMetaData)
	row = cur.fetchone()
	while row is not None:
		cur.execute(deleteTableQuery, (row[0]))
	cur.execute(deleteTableQuery, (partitions_meta_data))
	print('partitions deleted...')
	cur.close()

def clearAllSchema(openconnection):
	print('clearing all schema')
	clearSchemaQuery = 'drop schema public cascade;create schema public;'
	cur = openconnection.cursor()
	cur.execute(clearSchemaQuery)
	cur.close()

def rangepartition(ratingstablename, partitioncolname, numberofpartitions, openconnection):
	print("inside range partition...")
	#deletePartitions(ratingstablename, openconnection)
	cur = openconnection.cursor()
	getRangeMinQuery = 'SELECT MIN("{}") FROM "{}"'.format(partitioncolname, ratingstablename)
	getRangeMaxQuery = 'SELECT MAX("{}") FROM "{}"'.format(partitioncolname, ratingstablename)
	partitioncolidx = 0
	cur.execute(getRangeMinQuery)
	minRange = cur.fetchone()[0]
	print('minRange: ' + str(minRange))
	cur.execute(getRangeMaxQuery)
	maxRange = cur.fetchone()[0]
	print('maxRange: ' + str(maxRange))
	if ((ratingstablename == 'Ratings') & (partitioncolname == 'Rating')):
		minRange = 0.0
		maxRange = 5.0
	rangeStep = (float(maxRange) - float(minRange))/float(numberofpartitions)
	if (rangeStep < minRange):
		minRange = 0.0
	print('range step: ' + str(rangeStep))
	print('creating meta data')
	createMetaDataQuery = """
		CREATE TABLE IF NOT EXISTS partitions_meta_data
		(
		"tableName" character varying, 
		"partitionName" character varying, 
		"partitionType" character varying, 
		"rangeStart" double precision, 
		"rangeEnd" double precision, 
		"current" boolean
		)"""
	cur.execute(createMetaDataQuery)
	#create partition based on original schema
	createPartitionQuery = """
		CREATE TABLE IF NOT EXISTS "{}"
		( 
	"""
	getTableFieldsQuery = """
		SELECT column_name, data_type
		FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = '{}'
	"""
	cur.execute(getTableFieldsQuery.format(ratingstablename))
	colInfo = cur.fetchall()
	for i in range(0, len(colInfo)):
		if(partitioncolname == colInfo[i][0]):
			partitioncolidx = i
		if (i == (len(colInfo) - 1)):
			queryTerm = '"{}" {})'.format(colInfo[i][0], colInfo[i][1])
		else:
			queryTerm = '"{}" {}, '.format(colInfo[i][0], colInfo[i][1])
		createPartitionQuery = createPartitionQuery + queryTerm
	
	#createPartitionQuery = """
	#	CREATE TABLE IF NOT EXISTS "{}"
	#	(
	#	"UserId" bigint, 
	#	"MovieId" bigint, 
	#	"Rating" double precision
	#	)"""
	insertPartitionMetaDataQuery = """
		INSERT INTO partitions_meta_data 
		("tableName", "partitionName", "partitionType", "rangeStart", "rangeEnd", "current") 
		VALUES (%s, %s, %s, %s, %s, %s)
		"""
	N = int(numberofpartitions)
	for i in range(1, N+1):
		partitionName = ratingstablename+str(i)
		cur.execute(createPartitionQuery.format(partitionName))
		if (i == 1):
			cur.execute(insertPartitionMetaDataQuery, (ratingstablename, partitionName, 'range', float(minRange), float(rangeStep * i), bool(0)))
		elif (i == N):
			cur.execute(insertPartitionMetaDataQuery, (ratingstablename, partitionName, 'range', float(rangeStep * (i-1)), float(maxRange), bool(0)))
		else:
			cur.execute(insertPartitionMetaDataQuery, (ratingstablename, partitionName, 'range', float(rangeStep * (i-1)), float(rangeStep * i), bool(0)))
	print('meta data loaded')
	print('moving data to partitions')
	getPartitionName = """SELECT "partitionName" 
		FROM partitions_meta_data 
		WHERE "rangeStart" < %s 
		AND "rangeEnd" >= %s 
		AND "tableName" = %s 
		LIMIT 1
	"""
	getPartitionNameInclusive = """SELECT "partitionName" 
		FROM partitions_meta_data 
		WHERE "rangeStart" <= %s 
		AND "rangeEnd" >= %s 
		AND "tableName" = %s 
		LIMIT 1
	"""
	#create insert query for partition based on schema
	insertPartitionDataQuery = """
		INSERT INTO "{}"
		("""
	for i in range(0, len(colInfo)):
		if (i == (len(colInfo) - 1)):
			queryTerm = '"{}") '.format(colInfo[i][0], colInfo[i][1])
		else:
			queryTerm = '"{}", '.format(colInfo[i][0], colInfo[i][1])
		insertPartitionDataQuery = insertPartitionDataQuery + queryTerm
	insertPartitionDataQuery = insertPartitionDataQuery + ' VALUES ('
	for i in range(0, len(colInfo)):
		if (i == (len(colInfo) - 1)):
			insertPartitionDataQuery = insertPartitionDataQuery + '%s) '
		else:
			insertPartitionDataQuery = insertPartitionDataQuery + '%s, '
	
	#insertPartitionDataQuery = """
	#	INSERT INTO "{}"
	#	("UserId", "MovieId", "Rating") 
	#	VALUES (%s, %s, %s)
	#"""
	cur.execute('SELECT * FROM "{}"'.format(ratingstablename))
	rows = cur.fetchall()
	for i in range(0, len(rows)):
		row = rows[i]
		#print(row)
		if (int(row[partitioncolidx]) == minRange):
			cur.execute(getPartitionNameInclusive, (row[partitioncolidx], row[partitioncolidx], ratingstablename))
		else:
			cur.execute(getPartitionName, (row[partitioncolidx], row[partitioncolidx], ratingstablename))
		partitionName = cur.fetchone()[0]
		args = []
		for j in range(0, len(row)):
			args.append(row[j])
			#print(args)
		cur.execute(insertPartitionDataQuery.format(partitionName), tuple(args))
		#cur.execute(insertPartitionDataQuery.format(partitionName), (row[0], row[1], row[2]))
	cur.close()

def InsertSelectFromTable(insertTableName, selectTableName, sortingColumnName, insertIndex, openConnection):
	cur = openConnection.cursor()
	#
	print('fetching sorted data...')
	print('insert output table name:'+insertTableName+', select table:'+selectTableName)
	print('insert idx:'+str(insertIndex))
	selectSortedQuery = 'SELECT * FROM "{}" ORDER BY "{}" ASC'.format(selectTableName, sortingColumnName)
	cur.execute(selectSortedQuery)
	rows = cur.fetchall()
	#create insert query for table schema
	insertQuery = """
		INSERT INTO "{}"
		("""
	getTableFieldsQuery = """
		SELECT column_name, data_type
		FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = '{}'
	"""
	cur.execute(getTableFieldsQuery.format(insertTableName))
	colInfo = cur.fetchall()
	for i in range(0, len(colInfo)):
		if (i == (len(colInfo) - 1)):
			queryTerm = '"{}") '.format(colInfo[i][0], colInfo[i][1])
		else:
			queryTerm = '"{}", '.format(colInfo[i][0], colInfo[i][1])
		insertQuery = insertQuery + queryTerm
	insertQuery = insertQuery + ' VALUES ('
	for i in range(0, len(colInfo)):
		if (i == (len(colInfo) - 1)):
			insertQuery = insertQuery + '%s) '
		else:
			insertQuery = insertQuery + '%s, '
	#insertQuery = """
	#	INSERT INTO "{}" 
	#	("UserId", "MovieId", "Rating", "TupleOrder") 
	#	VALUES (%s, %s, %s, %s) 
	#"""
	insertQuery = insertQuery.format(insertTableName)
	print('insert query:'+insertQuery)
	print('inserting sorted data...')
	for i in range(0, len(rows)):
		row = rows[i]
		#print(row)
		args = []
		for j in range(0, len(row)):
			args.append(row[j])
		args.append(int(insertIndex + i))
		#print(args)
		cur.execute(insertQuery, tuple(args))
	cur.close()

def ParallelSort(TableName, SortingColumnName, OutputTableName, openConnection):
	print('output table name:'+OutputTableName)
	cur = openConnection.cursor()
	numberOfThreads = 5
	rangepartition(TableName, SortingColumnName, numberOfThreads, openConnection)
	print('creating sorted output table...')
	#create output based on original schema
	createSortedOutputTableQuery = """
		CREATE TABLE IF NOT EXISTS "{}"
		( 
	"""
	getTableFieldsQuery = """
		SELECT column_name, data_type
		FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = '{}'
	"""
	cur.execute(getTableFieldsQuery.format(TableName))
	colInfo = cur.fetchall()
	for i in range(0, len(colInfo)):
		queryTerm = '"{}" {}, '.format(colInfo[i][0], colInfo[i][1])
		createSortedOutputTableQuery = createSortedOutputTableQuery + queryTerm
	createSortedOutputTableQuery = createSortedOutputTableQuery + ' "TupleOrder" bigint)'
	
	#createSortedOutputTableQuery = """
	#	CREATE TABLE IF NOT EXISTS "{}"
	#	(
	#	"UserId" bigint, 
	#	"MovieId" bigint, 
	#	"Rating" double precision,
	#	"TupleOrder" bigint
	#	)"""
	cur.execute(createSortedOutputTableQuery.format(OutputTableName))
	selectPartitions = """SELECT "partitionName" 
		FROM "partitions_meta_data"
		WHERE "tableName" = '{}'
		AND "partitionType" = '{}'
	"""
	cur.execute(selectPartitions.format(TableName, 'range'))
	partitions = cur.fetchall()
	idx = 1
	for i in range(0, len(partitions)):
		#print(partitions[i][0])
		getInsertCount = 'SELECT COUNT(*) FROM "{}"'.format(partitions[i][0])
		cur.execute(getInsertCount)
		count = cur.fetchone()[0]
		#print('idx: ' + str(idx))
		#print('count: ' + str(count))
		t = threading.Thread(target=InsertSelectFromTable, args=(OutputTableName, partitions[i][0], SortingColumnName, idx, openConnection))
		t.daemon = True  # thread dies when main thread (only non-daemon thread) exits.		
		t.start()
		t.join()
		idx = idx + int(count)
	cur.close()

def mergeInsertFunc(insertTableName, selectTable1Name, selectTable2Name, table1JoinColumn, join1Idx, table2JoinColumn, join2Idx, input1Idx, input2Idx, insertQuery, openConnection):
	cur = openConnection.cursor()
	#
	print('fetching sorted data...')
	#print('insert output table name:'+insertTableName+', select table:'+selectTableName)
	#print('insert idx:'+str(insertIndex))
	selectSortedQuery = 'SELECT * FROM "{}" ORDER BY "{}" ASC'
	cur.execute(selectSortedQuery.format(selectTable1Name, table1JoinColumn))
	joinData1 = cur.fetchall()
	cur.execute(selectSortedQuery.format(selectTable2Name, table2JoinColumn))
	joinData2 = cur.fetchall()
	#create insert query for table schema
	print('inserting sorted data...')
	i = 0
	j = 0
	size1 = len(joinData1)
	size2 = len(joinData2)
	print('size1:'+str(size1))
	print('size2:'+str(size2))
	while i < size2:
		while j < size1:
			print('i:'+str(i)+', j:'+str(j))
			#print(joinData2[i])
			#print(joinData1[j])
			if(joinData1[j][join1Idx] == joinData2[i][join2Idx]):
				print('insert')
				vars = []
				for k in range(0, len(input1Idx)):
					vars.append(joinData1[j][input1Idx[k]])
				for k in range(0, len(input2Idx)):
					vars.append(joinData2[i][input2Idx[k]])
				cur.execute(insertQuery.format(insertTableName), tuple(vars))
				j = j + 1
			else:
				break
		i = i + 1
	cur.close()


def ParallelJoin(InputTable1, InputTable2, Table1JoinColumn, Table2JoinColumn, OutputTable, openConnection):
	print('inside join...')
	cur = openConnection.cursor()
	getUniqueRowCount = 'SELECT COUNT(DISTINCT "{}") FROM "{}"'.format(Table2JoinColumn, InputTable2)
	getTotalRowCount = 'SELECT COUNT("{}") FROM "{}"'.format(Table2JoinColumn, InputTable2)
	cur.execute(getUniqueRowCount)
	uniqueRowCount = cur.fetchone()[0]
	#uniqueRowCount = len(cur.fetchall())
	print(type(uniqueRowCount))
	cur.execute(getTotalRowCount)
	#totalRowCount = len(cur.fetchall())
	totalRowCount = cur.fetchone()[0]
	print(type(totalRowCount))
	if(int(totalRowCount) > int(uniqueRowCount)):
		print('!!!WARNING: Join key is duplicated in table 2!!!')
		sys.exit("Join key is duplicated in table 2...")
	#
	#
	join1Idx = 0
	join2Idx = 0
	numberOfThreads = 5
	outputColName = []
	input1Idx = []
	input2Idx = []
	#sortedTable1Name = 'Sorted'+InputTable1
	#sortedTable2Name = 'Sorted'+InputTable2
	#ParallelSort(InputTable1, Table1JoinColumn, sortedTable1Name, con)
	#ParallelSort(InputTable2, Table2JoinColumn, sortedTable2Name, con)
	print('creating output join table...')
	createOutputTableQuery = """
		CREATE TABLE IF NOT EXISTS "{}"
		( 
	""".format(OutputTable)
	getTableFieldsQuery = """
		SELECT column_name, data_type
		FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = '{}'
	"""
	cur.execute(getTableFieldsQuery.format(InputTable1))
	colInfo1 = cur.fetchall()
	for i in range(0, len(colInfo1)):
		if(colInfo1[i][0] == Table1JoinColumn):
			join1Idx = i
		outputColName.append(colInfo1[i][0])
		input1Idx.append(i)
		queryTerm = '"{}" {}, '.format(colInfo1[i][0], colInfo1[i][1])
		createOutputTableQuery = createOutputTableQuery + queryTerm
	cur.execute(getTableFieldsQuery.format(InputTable2))
	colInfo2 = cur.fetchall()
	for i in range(0, len(colInfo2)):
		if(colInfo1[i][0] == Table2JoinColumn):
			queryTerm = ''
			join2Idx = i
			print('skipping duplicate')
		elif (i == (len(colInfo2) - 1)):
			queryTerm = '"{}" {})'.format(colInfo2[i][0], colInfo2[i][1])
			outputColName.append(colInfo2[i][0])
			input2Idx.append(i)
		else:
			queryTerm = '"{}" {}, '.format(colInfo2[i][0], colInfo2[i][1])
			outputColName.append(colInfo2[i][0])
			input2Idx.append(i)
		createOutputTableQuery = createOutputTableQuery + queryTerm
	print(createOutputTableQuery)
	cur.execute(createOutputTableQuery)
	#
	print('partitioning table on join column')
	rangepartition(InputTable1, Table1JoinColumn, numberOfThreads, openConnection)
	rangepartition(InputTable2, Table2JoinColumn, numberOfThreads, openConnection)
	#create insert query for table based on schema
	insertQuery = """
		INSERT INTO "{}"
		("""
	for i in range(0, len(outputColName)):
		if (i == (len(outputColName) - 1)):
			insertQuery = insertQuery + '"{}")'.format(outputColName[i])
		else:
			insertQuery = insertQuery + '"{}", '.format(outputColName[i])
	insertQuery = insertQuery + ' VALUES ('
	for i in range(0, len(outputColName)):
		if (i == (len(outputColName) - 1)):
			insertQuery = insertQuery + '%s)'
		else:
			insertQuery = insertQuery + '%s, '
	#print('insertQuery:' + insertQuery)
	#
	#defining function for partitioned inserts
	#
	#
	print('parallelizing join')
	selectTable1Partitions = """SELECT "partitionName" 
		FROM "partitions_meta_data"
		WHERE "tableName" = '{}'
		AND "partitionType" = '{}'
	"""
	getTable2Partition = """SELECT "partitionName" 
		FROM "partitions_meta_data"
		WHERE "tableName" = '{}'
		AND "partitionType" = '{}' 
		AND "rangeStart" = (SELECT "rangeStart" FROM "partitions_meta_data" WHERE "partitionName" = '{}') 
		AND "rangeEnd" = (SELECT "rangeEnd" FROM "partitions_meta_data" WHERE "partitionName" = '{}')
	"""
	cur.execute(selectTable1Partitions.format(InputTable1, 'range'))
	partitions = cur.fetchall()
	numberOfPartitions = len(partitions)
	print('part len:' + str(numberOfPartitions))
	for p in range(0, numberOfPartitions):
		cur.execute(getTable2Partition.format(InputTable2, 'range', partitions[p][0], partitions[p][0]))
		partition2 = cur.fetchone()[0]
		t = threading.Thread(target=mergeInsertFunc, args=(OutputTable, partitions[p][0], partition2, Table1JoinColumn, join1Idx, Table1JoinColumn, join2Idx, input1Idx, input2Idx, insertQuery, openConnection))
		t.daemon = True  # thread dies when main thread (only non-daemon thread) exits.		
		t.start()
		t.join()
		#mergeInsertFunc(OutputTable, partitions[p][0], partition2, Table1JoinColumn, join1Idx, Table1JoinColumn, join2Idx, input1Idx, input2Idx, insertQuery, openConnection)
	print('function end')
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
        print('A database named {0} already exists'.format(dbname))

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
            #Example code
            #RATINGS_TABLE_NAME="Ratings"
            #USER_TABLE_NAME="User"
            #DATA_FILE_PATH='E:\\College\\512\\Assn3\\test_data.dat'
            #USER_FILE_PATH='E:\\College\\512\\Assn3\\user.dat'
            #clearAllSchema(con)
            #loadratings(RATINGS_TABLE_NAME, DATA_FILE_PATH, con)
            #loadUserData(USER_TABLE_NAME, USER_FILE_PATH, con)
            #ParallelSort(RATINGS_TABLE_NAME, 'Rating', 'SortedRatings', con)
            #ParallelJoin(RATINGS_TABLE_NAME, USER_TABLE_NAME, "UserId", "UserId", "JoinResult", con)

            # Here is where I will start calling your functions to test them. For example,
            #
            #   Example test calls
            # ###################################################################################
            # Anything in this area will not be executed as I will call your functions directly
            # so please add whatever code you want to add in main, in the middleware functions provided "only"
            # ###################################################################################

            # Use this function to do any set up after I finish testing, if you want to
            #after_test_script_ends_middleware(con, DATABASE_NAME)

    except Exception as detail:
        print("OOPS! This is the error ==> ", detail)
