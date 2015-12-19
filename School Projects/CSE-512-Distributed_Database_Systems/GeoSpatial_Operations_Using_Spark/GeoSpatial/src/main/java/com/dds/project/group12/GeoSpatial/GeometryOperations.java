package com.dds.project.group12.GeoSpatial;

import java.io.IOException;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import com.dds.project.group12.GeoSpatial.util.FileConfig;

/*
 * Authors: Sagar Sangani, Nitin Ahuja, Tarun Chanana, Deepika Dixit, Pooja Shastry
 * March 2015
 */


public class GeometryOperations 
{
	public static void main(String[] args ) throws IOException
	{    	

		long startTime = System.nanoTime();
		//code
		
		FileConfig fileConfig = FileConfig.getInstance();
		fileConfig.filepaths("config.properties");

		SparkConf conf = new SparkConf().setAppName("Geometric Union");
	//	conf.set("spark.eventLog.enabled", "true");
		conf.setMaster("local");
		JavaSparkContext sparkContext = new JavaSparkContext(conf);

		
		//	call for GeometryUnion()
	//Union.geometryUnion(fileConfig.getUnionInput(), fileConfig.getUnionOutput(), sparkContext);

		//	call for GeometryConvexHull()
		ConvexHull.getConvexHull(fileConfig.getConvexHullInput(), fileConfig.getConvexHullOutput(), sparkContext);

		//	call for GeometryFarthestPair()
	//	FarthestPair.getFarthestPair(fileConfig.getFarthestPairInput(), fileConfig.getFarthestPairOutput(), sparkContext);

		//	call for GeometryClosestPair()
		//ClosestPair.getClosestPair(fileConfig.getClosestPairInput(), fileConfig.getClosestPairOutput(), sparkContext);

		//	call for SpatialRangeQuery()
		//RangeQuery.spatialRangeQuery(fileConfig.getRangeQueryInput1(), fileConfig.getRangeQueryInput2(), 
			//	fileConfig.getRangeQueryOutput(), sparkContext);

		//	call for SpatialJoinQuery()
		//JoinQuery.spatialJoinQuery(fileConfig.getJoinQueryInput1(), fileConfig.getJoinQueryInput2(), 
			//	fileConfig.getJoinQueryOutput(), sparkContext);


		//	call for HeatMap()
	//HeatMap.getHeatMap(fileConfig.getJoinQueryInput1(), fileConfig.getJoinQueryInput2(), fileConfig.getJoinQueryOutput(), sparkContext);

		
		sparkContext.close();
		long endTime = System.nanoTime();
		System.out.println("Took "+((endTime - startTime)/(Math.pow(10,9))) + " s"); 

	}
}
