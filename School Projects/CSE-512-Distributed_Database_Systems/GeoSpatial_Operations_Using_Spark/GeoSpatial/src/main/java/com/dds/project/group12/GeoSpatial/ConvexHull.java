package com.dds.project.group12.GeoSpatial;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.httpclient.URI;
import org.apache.spark.api.java.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;

import scala.Tuple2;

import com.dds.project.group12.GeoSpatial.entity.Edge;
import com.dds.project.group12.GeoSpatial.entity.Point;
import com.dds.project.group12.GeoSpatial.services.Service;
import com.dds.project.group12.GeoSpatial.util.Utilities;

public class ConvexHull implements Serializable{
	private static final long serialVersionUID = 1L;

	public static ArrayList<Point> getConvexHullPoints(String filePathInHDFS, JavaSparkContext sc) {
		
		JavaRDD<String> logData = sc.textFile(filePathInHDFS).cache();
		JavaRDD<Point> pointsRDD = logData.map(new Function<String, Point>() {

			private static final long serialVersionUID = 1L;

			public Point call(String s) {
				return Utilities.getPoint(s);
			}
		}).filter(new Function<Point, Boolean>(){

			public Boolean call(Point v1) throws Exception {
				if(v1==null)
					return false;
				return true;
			}
		});		
		JavaRDD<Point> sortedPointsRDD = pointsRDD.sortBy(new Function<Point, Point>(){
			public Point call(Point v1) throws Exception {
				return v1;
			} }, true,1);

		JavaRDD<Point> convexHullPoints = (JavaRDD<Point>) sortedPointsRDD.mapPartitions(new FlatMapFunction<Iterator<Point>, Point>() {
			public Iterable<Point> call(Iterator<Point> t) throws Exception {
				List<Point> upperHull= new ArrayList<Point>();
				List<Point> pointsList = new ArrayList<Point>();
				System.out.println("Building upperHullFirst");
				while(t.hasNext())
				{
					Point p = t.next();
					pointsList.add(p);
					while (upperHull.size() >=2 && !(Service.getCrossProduct(upperHull.get(upperHull.size()-2), upperHull.get(upperHull.size()-1), p) > 0))
					{
						upperHull.remove(upperHull.size()-1);
					}
					upperHull.add(p);
				}

				List<Point> lowerHull= new ArrayList<Point>();
				for (int i =  pointsList.size() - 1; i >= 0; i--) {
					while (lowerHull.size() >= 2 && Service.getCrossProduct(lowerHull.get(lowerHull.size()-2), lowerHull.get(lowerHull.size()-1), pointsList.get(i)) <= 0)
					{
						lowerHull.remove(lowerHull.size()-1);
					}
					lowerHull.add(pointsList.get(i));
				}
				upperHull.addAll(lowerHull);
				return upperHull;
			}
		}).distinct().sortBy(new Function<Point, Point>(){
			private static final long serialVersionUID = 1L;

			public Point call(Point v1) throws Exception {
				return v1;
			} }, true,1);
		ArrayList<Point> convexHull = (ArrayList<Point>) convexHullPoints.collect();
		return convexHull;
	}
	
	public static boolean getConvexHull(String InputLocation, String OutputLocation, JavaSparkContext sparkContext) {
		JavaRDD<Point> convexHull = sparkContext.parallelize(getConvexHullPoints(InputLocation, sparkContext));
		convexHull.coalesce(1).saveAsTextFile(OutputLocation);
		return true;
	}
}