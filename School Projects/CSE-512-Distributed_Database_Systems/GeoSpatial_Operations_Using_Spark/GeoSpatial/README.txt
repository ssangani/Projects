README

The following project was created by Sagar Sangani, Nitin Ahuja, Tarun Chanana, Pooja Shastry, Deepika Dixit as a part of CSE 512: Distributed Database Systems curriculum at Arizona State University (Spring 2015)

Introduction:

Function descriptions:

GeometryUnion function takes the following inputs:
1. InputLocation
2. OutputLocation
3. sparkContext

GeometryUnion function return Boolean as status of the function.

GeometryConvexHull function takes the follwing input:
1. InputLocation
2. OutputLocation
3. sparkContext

GeometryConvexHull function return Boolean as status of the function.

GeometryFarthestPair function takes the follwing input:

1. InputLocation
2. OutputLocation
3. sparkContext

GeometryFarthestPair function return Boolean as status of the function.

GeometryClosestPair function takes the follwing input:

1. InputLocation
2. OutputLocation
3. sparkContext

GeometryClosestPair function return Boolean as status of the function.

SpatialRangeQuery function takes the follwing input:
1. InputLocation1
2. InputLocation2
3. OutputLocation
4. sparkContext

SpatialRangeQuery function return Boolean as status of the function.

SpatialJoinQuery function takes the follwing input:
1. InputLocation1 - path of Target Rectangles
2. InputLocation2 - path of Query Rectangles
3. OutputLocation
4. sparkContext

SpatialJoinQuery function return Boolean as status of the function.


File Formats:
Please make sure that all the input should be in CSV format (delimeter should be ',')
Rectangle files for GeometryUnion, SpatialRangeQuery and SpatialJoinQuery are in format:
x1,y1,x2,y2

Inputfile of GeometryConvexHull,GeometryFarthestPair,GeometryClosestPair  are in format:
x1,y1


Setting up configuration variables:

To set up the required variables to run this build please follow steps below:
1.	Extract the zipped project 'GeoSpatial.tar.gz' to a directory of your choice
2.	Go to $(PROJECT_DIR)/src/main/java and open the file 'config.properties' where $(PROJECT_DIR) is the directory where you extracted the project
3.	In this file each variable stands for input and output location of data corresponding to each function and names are pretty much self-explanatory. Please change values of each variable with path of corresponding input and output required to run each function
		For example if your input data is stored locally at '/home/sagar/Downloads/DDS/' as file 'PolygonUnionTestData.csv', then insert value for 'unionInput' variable as '/home/sagar/Downloads/DDS/PolygonUnionTestData.csv' (without quotes)
		eg: unionInput=/home/sagar/Downloads/DDS/PolygonUnionTestData.csv
		
		To use Hadoop files instead of files stored locally change corresponding HDFS variable instead
		eg: unionHDFSInput=hdfs://localhost:9000/home/sagar/Downloads/DDS/PolygonUnionTestData.csv
4.	After initializing 'config.properties' file with proper variables save it
5.	Open $(PROJECT_DIR)/src/main/java/com/dds/project/group12/GeoSpatial/GeometryOperations.java in text editor of your choice
	Alternately you can also import the entire project into Eclipse. To import please follow steps below:
		a.	Open Eclipse, click File -> Import
		b.	In pop-up select 'Maven' -> select 'Existing Maven Project' and click on 'Next'
		c.	Click on 'Browse' button and browse to directory where you extracted the project and click on 'Open'
		d.	Eclipse will discover the 'pom.xml' corresponding to the project and make sure box next to it is checked
		e.	Click on 'Finish' and wait for import to be completed
6.	In GeometryOperations.java in main() you can call the geometric operation functions. Example on how to call each of the 6 functions are already there as follows:
		//	call for GeometryUnion()
		Union.GeometryUnion(fileConfig.getUnionInput(), fileConfig.getUnionOutput(), sparkContext);
		//	call for ConvexHull()
		ConvexHull.GeometryConvexHull(fileConfig.getConvexHullInput(), fileConfig.getConvexHullOutput(), sparkContext);
		//	call for FarthestPair()
		FarthestPair.GeometryFarthestPair(fileConfig.getFarthestPairInput(), fileConfig.getFarthestPairOutput(), sparkContext);
		//	call for GeometryClosestPair()
		ClosestPair.GeometryClosestPair(fileConfig.getClosestPairInput(), fileConfig.getClosestPairOutput(), sparkContext);
		//	call for SpatialRangeQuery()
		RangeQuery.SpatialRangeQuery(fileConfig.getRangeQueryInput1(), fileConfig.getRangeQueryInput2(), 
				fileConfig.getRangeQueryOutput(), sparkContext);
		//	call for SpatialJoinQuery90
		JoinQuery.SpatialJoinQuery(fileConfig.getJoinQueryInput1(), fileConfig.getJoinQueryInput2(), 
				fileConfig.getJoinQueryOutput(), sparkContext);
7.	If please change the methods getUnionInput(), getUnionOutput(), getConvexHullInput() etc. in each function call depending whether you are using local of HDFS files
	Each getX() function for fetching input and output file locations exists to fetch local input and output variables
	Each getX() has equivalent getHDFSX() method which would be used instead to fetch input and output path variables to be used in function call
	For example, if you are using local files for GeometryUnion() function you would use getUnionInput(). Similarly  replace getUnionInput() with getUnionHDFSInput() if you want to use HDFS file instead of local one
8.	Once you have correctly configured the project following above steps build the project. For steps to build the project, please follow steps below


Building the code:

1.	Open command prompt/terminal/shell and go to directory where you extracted the project
2.	Run the command 'mvn package' (without quotes) in the project directory to build the project
3.	The build jar can be found under $(PROJECT_DIR)/target as 'GeoSpatial-0.0.1-SNAPSHOT.jar'
4.	You can use this jar to run on spark cluster

Running the code:
1.	Please run following command to run the project
	bin/spark-submit --class "com.dds.project.group12.GeoSpatial.GeometryOperations" --master "spark://master:7077" /home/nitina/Downloads/GeoSpatial/target/GeoSpatial-0.0.1-SNAPSHOT.jar
