package com.dds.project.group12.GeoSpatial;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.broadcast.Broadcast;

import scala.Tuple2;

import com.dds.project.group12.GeoSpatial.entity.Point;
import com.dds.project.group12.GeoSpatial.entity.Polygon;
import com.dds.project.group12.GeoSpatial.util.Utilities;

public class Union {
	public static String debugPath;
	
	public static boolean geometryUnion (String InputLocation, String OutputLocation, JavaSparkContext sparkContext) {
		
			
			//	Read input file from System as String RDD
			JavaRDD<String> polyRaw = sparkContext.textFile(InputLocation).cache();
			
			//	Map polygon RDD
	        JavaRDD<Polygon> polygons = polyRaw.map(new Function<String, Polygon>() {
	        	@Override
	        	public Polygon call(String s) {
	        		return Utilities.getPolygon(s);
	        	}
	        }).filter(new Function<Polygon, Boolean>() {
	        	@Override
	        	public Boolean call (Polygon p) throws Exception {
	        		if (p == null)
	        			return false;
	        		else
	        			return true;
	        	}
	        });
	        
	        JavaRDD<Polygon> polygonsDup = polyRaw.map(new Function<String, Polygon>() {
	        	@Override
	        	public Polygon call(String s) {
	        		return Utilities.getPolygon(s);
	        	}
	        }).filter(new Function<Polygon, Boolean>() {
	        	@Override
	        	public Boolean call (Polygon p) throws Exception {
	        		if (p == null)
	        			return false;
	        		else
	        			return true;
	        	}
	        });
	        
	        //	Map points RDD
	        JavaRDD<Point> points = polygons.flatMap(new FlatMapFunction <Polygon, Point> () {
	        	@Override
	        	public ArrayList<Point> call (Polygon p) {
	        		return p.getAllPoints();
	        	}
	        });
	        
	        final Broadcast<List<Polygon>> polygonsBroadCast = sparkContext.broadcast(polygonsDup.collect());
	        
	        //	Find intersection points between any two polygons
	        JavaRDD<Point> intersectionPoints = polygons.mapPartitions(new FlatMapFunction<Iterator<Polygon>, Point> () {
	        	@Override
	        	public ArrayList<Point> call (Iterator<Polygon> p) {
	        		ArrayList<Point> intersectionPoints = new ArrayList<Point>();
	        		while(p.hasNext()) {
	        			Polygon temp = p.next();
	            		for (Polygon p2: polygonsBroadCast.value()) {
	            			intersectionPoints.addAll(temp.getIntersectionPoints(p2));
	            		}
	        		}        		
	        		return intersectionPoints;
	        	}
	        });
	        
	        JavaRDD<Point> updatedPoints = points.union(intersectionPoints);
	        JavaRDD<Point> updatedPointsDup = points.union(intersectionPoints);
	        final Broadcast<List<Point>> pointsBroadCast = sparkContext.broadcast(updatedPointsDup.collect());
	        // Cartesian product of Polygon and all possible points
	        
	        //	Check if any polygon vertex lies within/on edge of any polygon
	        JavaRDD<Point> redundantPoints = polygons.mapPartitions(new FlatMapFunction<Iterator<Polygon>, Point> () {
	        	@Override
	        	public ArrayList<Point> call (Iterator<Polygon> p) {
	        		ArrayList<Point> boundaryPoints = new ArrayList<Point>();
	        		while(p.hasNext()) {
	        			Polygon temp = p.next();
	        			for (Point pt: pointsBroadCast.value()) {
	            			if (temp.pointLiesWithin(pt)) {
	            				boundaryPoints.add(pt);
	                		}
	            		}
	        		}        		
	        		return boundaryPoints;
	        	}
	        }).filter(new Function<Point, Boolean> () {
	        	@Override
	        	public Boolean call (Point p) {
	        		if (p == null) 
	        			return false;
	        		else 
	        			return true;
	        	}
	        });
	        
	        
	        //	Convert point RDD's into String RDD's
	        JavaRDD<String> updatedPointsStr = updatedPoints.map(new Function<Point, String> () {
	        	@Override
	        	public String call (Point p) {
	        		return p.toString();
	        	}
	        });
	        
	        JavaRDD<String> filteredPointsStr = redundantPoints.map(new Function<Point, String> () {
	        	@Override
	        	public String call (Point p) {
	        		return p.toString();
	        	}
	        });
	        
	        //	Result is subtraction of above 2 RDD's
	        JavaRDD<String> result = updatedPointsStr.subtract(filteredPointsStr).distinct();
	        
	        //	Save RDD's
	        //polygons.coalesce(1).saveAsTextFile(debugFilePath+"/geoUnion/polygons");
	        //points.coalesce(1).saveAsTextFile(debugFilePath+"/geoUnion/points");
	        //intersectionPoints.coalesce(1).saveAsTextFile(debugFilePath+"/geoUnion/intersectionPoints");
	        //updatedPoints.coalesce(1).saveAsTextFile(debugFilePath+"/geoUnion/updatedPoints");
	        //filteredPoints.coalesce(1).saveAsTextFile(debugFilePath+"/geoUnion/filteredPoints");
	        result.coalesce(1).saveAsTextFile(OutputLocation+"/result");
	        
	        return true;
	}
	

}
