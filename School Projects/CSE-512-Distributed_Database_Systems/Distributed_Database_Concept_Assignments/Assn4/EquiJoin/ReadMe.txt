The following Maven project implements Equi-join using Hadoop MapReduce using Reduce side join operation

Building the code:
1. Un-tar the EquiJoin.tar.gz at in some directory
2. Open Eclipse (make sure Eclipse has maven plugins installed)
3. Click on File -> Import -> Existing Maven project and browse to the directory where you untarred the project
4. Select the pom.xml corresponding to the project
5. Open the JoinDriver.java under the package com.sagar.dds.assignment.EquiJoin
6. Change the file input path as to where the file is located in the following line
   FileInputFormat.setInputPaths(job, inputhFilePath);
	eg: FileInputFormat.setInputPaths(job, "/home/sagar/Downloads/DDS/assn4/data.csv");
7. Change the directory to the file path where you want to save the results
   FileOutputFormat.setOutputPath(job, new Path(outputPath));
	eg: FileOutputFormat.setOutputPath(job, new Path("/home/sagar/Downloads/DDS/assn4/result"));
8. You can also change configuration for mapping depending on input file
	For example, the provided JoinDriver parses the input data file a CSV filewith comma as the delimeter and structured in following format:

	R,key1,RTuple1
	S,key1,STuple1
	R,key2,RTuple2
	S,key2,STuple2
	S,key3,Stuple3

	Here the first parameter specifies the table to which data belongs, R type or S type; key1, key2, key3 etc. are keys on which data is to be joined on using equi-join; and RTuple1, RTuple2, STuple1, Stuple2, etc. is data tuples to be joined based on the key value

	Based on this we set the configuartion with following parameters:
	config.set("keyIndex", "1");
	config.set("typeIndex", "0");
	config.set("RTypeValue", "R");
	config.set("STypeValue", "S");
	config.set("separator", ",");

	"keyIndex" - index in CSV input file where key is located
	"typeIndex" - index in CSV input file where type of table is located
	"separator" - delimeter used in CSV input file
	"RTypeValue" - name of the first table or type to which tuple belongs
	"STypeValue" - name of the second table (to be joined) or type to which tuple belongs

9. Build the maven project using package command
10. Before running the program set following environment variables in your system
    export JAVA_HOME=pathToYourJVMInstallationDirectory
    export HADOOP_HOME=pathToYourHadoopInstallationDirectory
    export PATH="$JAVA_HOME/bin:$HADOOP_HOME:$PATH"
    
    here PackageJarFileName.jar is the name of *.jar file you packaged using maven
    and $jarDir is the directory where you saved the package

    Now running following command will execute the program:
    $HADOOP_HOME/bin/hadoop jar $jarDir/PackageJarFileName.jar com.sagar.dds.assignment.EquiJoin.JoinDriver


Implementation details:
CompositeKeyWritable - Class implements a way to create composite key for the object in map phase
JoinMapper - Mapper class implmentation which maps the data into (key, value) pair, where key consists of both key and type of data or table name to which data belongs
JoinPartitioner - Partitioner class implementation which partitions the reduce tasks into given number of tasks
JoinGroupingComparator - Comparator class implementation based on key value which sends the mapped (key, value) pairs to Reducer
JoinSortComparator - Comparator class implementation which sorts the (key, value) pairs being sent to given Reducer based on key and type. Here sending table 1 R type (key, value) pairs takes preferrence over table 2 S type (key, value) pair
JoinReducer - Reducer implementation of class which reduces the (key, value) pair and performs the join operation. The reducer was implemented in such a way that it could handle many-to-many relationship joins and creates a product result of all (key, value) pairs having same key
JoinDriver - Driver class where we specify the configuration etc. and run the map reduce job


An example of input file and result is also included with this package:
Input:
R,1,Shawshank Redemption
S,1,1994,10
R,2,The Karate Kid
S,2,1984,7
S,2,2010,6
R,3,Pulp Fiction
S,3,1994,9
R,4,Saving Private Ryan
S,4,1998,9
R,5,The Green Mile
S,5,1995,9
R,6,Full Metal Jacket
S,6,1987,8
R,7,Kill Bill: Volume 2
R,7,The Machinist
S,7,2004,8

Ouput:
4,Saving Private Ryan,1998,9
1,Shawshank Redemption,1994,10
5,The Green Mile,1995,9
2,The Karate Kid,1984,7
2,The Karate Kid,2010,6
6,Full Metal Jacket,1987,8
3,Pulp Fiction,1994,9
7,The Machinist,2004,8
7,Kill Bill: Volume 2,2004,8

