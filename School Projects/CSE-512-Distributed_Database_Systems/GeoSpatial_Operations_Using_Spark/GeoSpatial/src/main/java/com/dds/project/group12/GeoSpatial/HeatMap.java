package com.dds.project.group12.GeoSpatial;

import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.broadcast.Broadcast;

import scala.Tuple2;

import com.dds.project.group12.GeoSpatial.entity.Point;
import com.dds.project.group12.GeoSpatial.entity.Rectangle;
import com.dds.project.group12.GeoSpatial.util.Utilities;

public class HeatMap {
	
	public static final String debugPath = "/home/sagar/Downloads/DDS/results/debug";
	public static final Boolean DEBUG = false;

	public static boolean getHeatMap (String InputLocation1, String InputLocation2, 
			String OutputLocation, JavaSparkContext sparkContext) {
		
		//Read input file and store in RDD
	    JavaRDD<String> polyData1 = sparkContext.textFile(InputLocation1);
	    JavaRDD<String> polyData2 = sparkContext.textFile(InputLocation2);

	    // Create RDD of Rectangles
	    JavaRDD<Rectangle> targetRectangles = polyData1.map(new Function<String, Rectangle>(){
	    	@Override
	    	public Rectangle call(String s) {
	    		return Utilities.getRectangle(s);
	    	}
	    }).filter(new Function<Rectangle, Boolean>() {
        	@Override
        	public Boolean call (Rectangle r) throws Exception {
        		if (r == null)
        			return false;
        		else
        			return true;
        	}
        });
	    
	    JavaRDD<Point> queryPoints = polyData2.map(new Function<String, Point>(){
	    	@Override
	    	public Point call(String s) {
	    		return Utilities.getPoint(s);
	    	}
	    }).filter(new Function<Point, Boolean>() {
        	@Override
        	public Boolean call (Point p) throws Exception {
        		if (p == null)
        			return false;
        		else
        			return true;
        	}
        });
	    final Broadcast<List<Point>> pointBroadcast = sparkContext.broadcast(queryPoints.collect());

	    // Create PairRDD of rectangles and query Rectangle
	    //JavaPairRDD<Rectangle, Point> rectanglePairs = targetRectangles.cartesian(queryPoints);
	    //if(DEBUG)
		//    rectanglePairs.saveAsTextFile(debugPath+"/joinQuery/rectanglePairs");

	    // filter out rectangles that lie outside the query rectangle
	    
	    //if(DEBUG)
		//    filteredRectangles.saveAsTextFile(debugPath+"/joinQuery/filteredRectangles");
 
	    // Performing groupby to accumulate result for target rectangles
	    //JavaPairRDD<Rectangle, Iterable<Point>> result = filteredRectangles.groupByKey();

	    // prepare the output format RDD
	    JavaPairRDD<Rectangle, Integer> countResult = targetRectangles.mapToPair(new PairFunction<Rectangle, Rectangle, Integer>() {
	    	@Override
	    	public Tuple2<Rectangle, Integer> call (Rectangle r) {
	    		int count = 0;
	    		for (Point p: pointBroadcast.value()) {
	    			if(r.doesPointLieIn(p)) {
	    				count++;
	    			}
	    		}
	    		return new Tuple2(r, count);
	    	}
	    });
	    
	    JavaRDD<String> result = countResult.map(new Function<Tuple2<Rectangle, Integer>, String>() {
	    	@Override
	    	public String call(Tuple2<Rectangle, Integer> t) {
	    		return (t._1().toString() + "," + t._2());
	    	}
	    });
	    
	    
	    result.coalesce(1).saveAsTextFile(OutputLocation);
	    
	    //	TODO - check if operation was successful or not		
		return true;
	}
}
