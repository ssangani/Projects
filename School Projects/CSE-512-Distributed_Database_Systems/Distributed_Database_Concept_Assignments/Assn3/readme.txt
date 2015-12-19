The following Interface.py file contains implementation for Parallel Sorting and Parallel Join using Sort-Merge technique

1. Parallel Sorting
	The function defition is as follows:
	ParallelSort(TableName, SortingColumnName, OutputTableName, openConnection)
	
	Here the arguments mean following:
	TableName (type - String)- Name of the table stored in PostgreSQL database to be sorted
	SortingColumnName (type - String) - Name of the column on which sort has to performed (Column has to be of numeric type)
	OutputTableName (type - String) - Desired name of the database where sorted data has to be stored
	openConnection (type con) - Postgre database connection
	
	This function first partitions into same number of parts as number of threads the input table using range partition based on given column name
	Once the data is partitioned, the function creates 5 threads (we can modify number of threads by changing variable 'numberOfThreads' to some other value) which operate in parallel to call a function InsertSelectFromTable()
	This function selects data from each partition in sorted order and then inserts into output table along with an index called 'TupleOrder' which specifies the order of tuple in sorted data
	
	Example: ParallelSort("Ratings", 'Rating', 'SortedRatings', con)
	
2. Parallel Join
	The function defition is as follows:
	ParallelJoin(InputTable1, InputTable2, Table1JoinColumn, Table2JoinColumn, OutputTable, openConnection)
	Here the arguments mean following:
	InputTable1 (type - String) - Name of the first table stored in PostgreSQL databas
	InputTable2 (type - String) - Name of the second table stored in PostgreSQL database whose data is to be joined with data of InputTable1
	Table1JoinColumn (type - String) - Name of the join key column in first table
	Table2JoinColumn (type - String) - Name of the join key column in second table
	OutputTable (type - String) - Desired name of the database where sorted data has to be stored
	openConnection (type con) - Postgre database connection
	
	First the function checks whether the 'Table2JoinColumn' in 'InputTable2' is a unique key or not. 
	The reason behind this is that in Sort-Merge technique the two tables to joined are sorted on join key column so that interleaved linear scans occur simultaneously
	If duplicates are found then the logic behind sort-merge is ruined
	Here following standard SQL syntax 'SELECT FROM table1 join on table2' we check table 2 join column as primary key and not other way around
	
	If condition is met, we partition the data into same number as number of threads (can be modified by changing 'numberOfThreads')
	once data is partitioned we start 5 threads which start picking data from each partition in sorted order and then using sort-merge logic, we join the data using interleaving and insert into the output table
	
	Example: ParallelJoin("Ratings", "User", "UserId", "UserId", "JoinResult", con)
	

NOTE: Also please find with the zip file sample data cases 'test_data.dat' and 'user.dat' which contains ratings and user data respectively, and 'userdup.dat' which contains duplicate user with same id