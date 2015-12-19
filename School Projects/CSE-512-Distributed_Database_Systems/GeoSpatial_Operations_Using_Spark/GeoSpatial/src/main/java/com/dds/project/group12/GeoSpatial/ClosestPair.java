package com.dds.project.group12.GeoSpatial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
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
import com.dds.project.group12.GeoSpatial.util.Utilities;

public class ClosestPair implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4142254665590829820L;

	public static boolean getClosestPair(String InputLocation, String OutputLocation, JavaSparkContext sparkContext) {
		JavaRDD<String> textData = sparkContext.textFile(InputLocation);
        
        JavaRDD<Point> points = textData.map(new Function<String, Point>(){
        	/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@Override
			public Point call(String s){
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
        
        JavaRDD<Point> points1 = textData.map(new Function<String, Point>(){
        	/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@Override
			public Point call(String s){
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
       
        ArrayList<Point> pointsList = (ArrayList<Point>) points1.collect();
        final Broadcast<ArrayList<Point>> pointsListBrod = sparkContext.broadcast(pointsList);
     
             
        JavaPairRDD<Double, Tuple2<Point, Point>> minPair = points.mapToPair(new PairFunction<Point, Double, Tuple2<Point, Point>>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Tuple2<Double, Tuple2<Point, Point>> call(Point p)
					throws Exception {
				//Iterator<Point> itr = pointsListBrod.value().iterator();
				//Point current;
				//List<Tuple2<Point,Point>> pPairList = new ArrayList<Tuple2<Point,Point>>();		
				double min = Double.MAX_VALUE;
				Point temp = null;
				double dist = 0.0;
				for(Point pt: pointsListBrod.value()) {
					dist = pt.distance(p);
					if(dist != 0.0) {
						if (min > dist)
						{
							min = dist;
							temp = pt;
						}
					}
				}
				/*
				while(itr.hasNext())
				{
					current = itr.next();
					if (!(current.equals(p)))	
							{	
							if (min > current.distance(p))
							{
								min = current.distance(p);
								temp = current;
							}
							}
				}*/
				 Tuple2<Point, Point> tuple=new Tuple2<Point, Point>(p,temp); 
				return new Tuple2<Double, Tuple2<Point, Point>>(min, tuple);
			}
        	
		}).distinct().cache();
        
        //JavaPairRDD<Double, Tuple2<Point, Point>> minPair2 = minPair.mapPartitions(new FlatMapFunction<Iterator<Point>, Point>() {
			//public Iterable<Point> call(Iterator<Point> t) throws Exception {});
        
        JavaRDD<Tuple2<Double,Tuple2<Point,Point>>> localMin = minPair.mapPartitions(
        		new FlatMapFunction<Iterator<Tuple2<Double,Tuple2<Point,Point>>>, Tuple2<Double,Tuple2<Point,Point>>>()
        		{
        			public Iterable<Tuple2<Double,Tuple2<Point,Point>>> call(Iterator<Tuple2<Double,Tuple2<Point,Point>>> itr)
        			{
        				double min = Double.MAX_VALUE;
        				Tuple2<Double,Tuple2<Point,Point>> temp = null, next;// = new Tuple2<Double,Tuple2<Point,Point>>(); 
        				while(itr.hasNext())
        				{
        					next = itr.next();
        					if(min > next._1)
        					{
        						min = next._1;
        						temp = next; 
        					}
        				}
        				List <Tuple2<Double,Tuple2<Point,Point>>> L1 = new ArrayList();
        				L1.add(temp);
        				return (L1);
        			}

        		});
        		
        /*JavaRDD<Tuple2<Double,Tuple2<Point,Point>>> minRDD = minPair.map(new Function<Tuple2<Double,Tuple2<Point,Point>>, Tuple2<Double,Tuple2<Point,Point>>>() {

			@Override
			public Tuple2<Double, Tuple2<Point, Point>> call(
					Tuple2<Double, Tuple2<Point, Point>> v1) throws Exception {
				// TODO Auto-generated method stub
				return v1;
			}
		});*/
        
        
        /*Tuple2<Double, Tuple2<Point, Point>> minimum= minRDD.min(new Comparator<Tuple2<Double,Tuple2<Point,Point>>>() {

			@Override
			public int compare(Tuple2<Double, Tuple2<Point, Point>> o1,
					Tuple2<Double, Tuple2<Point, Point>> o2) {
				return (int)(o1._1() - o2._1());
			}
		});
        System.out.println("Points are: "+minimum._2()._1()+" and "+minimum._2()._2());
        */       
        //pointsListBrod.unpersist();
        
        List<Tuple2<Double, Tuple2<Point, Point>>> tuples = new ArrayList<Tuple2<Double, Tuple2<Point, Point>>>(localMin.collect());
        
        double min = Double.MAX_VALUE;
        Tuple2<Point, Point> closestPair=null;
        for(Tuple2<Double, Tuple2<Point, Point>> tuple: tuples)
        {
        	if(min > tuple._1())
        	{
        		min = tuple._1();
        		closestPair = tuple._2();
        	}
        }
        Tuple2<Point,Point> t4 = closestPair;
        if(t4!=null) {
        	List<Point> res = new ArrayList<Point>();
        	res.add(t4._1());
        	res.add(t4._2());        	
        	JavaRDD<Point>finalRes = sparkContext.parallelize(res);
        	finalRes.coalesce(1).saveAsTextFile(OutputLocation);
			return true;
		}
		else {
			return false;
		}
        //localMin.coalesce(1).saveAsTextFile(OutputLocation);
        //return true;
    }
}
