package com.dds.project.group12.GeoSpatial;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.broadcast.Broadcast;

import scala.Tuple2;

import com.dds.project.group12.GeoSpatial.entity.Point;

public class FarthestPair {
	public static boolean getFarthestPair (String InputLocation, String OutputLocation, JavaSparkContext sparkContext) {
		ArrayList<Point> convexHullPoints = ConvexHull.getConvexHullPoints(InputLocation, sparkContext);
		JavaRDD<Point> points = sparkContext.parallelize(convexHullPoints);
        ArrayList<Point> pointsList = (ArrayList<Point>) points.collect();
        final Broadcast<ArrayList<Point>> pointsListBrod = sparkContext.broadcast(pointsList);
        JavaPairRDD<Double, Tuple2<Point, Point>> maxPair = points.mapToPair(new PairFunction<Point, Double, Tuple2<Point, Point>>() {
			private static final long serialVersionUID = 1L;
			public Tuple2<Double, Tuple2<Point, Point>> call(Point p)
					throws Exception {
				Iterator<Point> itr = pointsListBrod.value().iterator();
				Point current;
				double max = Double.MIN_VALUE;
				Point temp = null;
				while(itr.hasNext())
				{
					current = itr.next();
					if (!(current.equals(p)))	
							{	
							if (max < current.distance(p))
							{
								max= current.distance(p);
								temp = current;
							}
							}
				}
				 Tuple2<Point, Point> tuple=new Tuple2<Point, Point>(p,temp); 
				return new Tuple2<Double, Tuple2<Point, Point>>(max, tuple);
			}
        	
		});        
        maxPair = maxPair.sortByKey(false);
        Tuple2<Double, Tuple2<Point,Point>> t3 = maxPair.first();
        Tuple2<Point,Point> t4 = t3._2();
        if(t4!=null) {
        	ArrayList<Point> res = new ArrayList<Point>();
        	res.add(t4._1());
        	res.add(t4._2());
        	JavaRDD<Point>finalRes = sparkContext.parallelize(res);
        	finalRes.coalesce(1).saveAsTextFile(OutputLocation);
        	return true;
		}
		else {
			return false;
		}
	
	}
}
