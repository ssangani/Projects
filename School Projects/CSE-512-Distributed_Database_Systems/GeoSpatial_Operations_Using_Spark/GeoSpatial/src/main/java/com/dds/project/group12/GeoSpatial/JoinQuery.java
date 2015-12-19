package com.dds.project.group12.GeoSpatial;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.broadcast.Broadcast;

import scala.Tuple2;
import com.dds.project.group12.GeoSpatial.entity.Rectangle;
import com.dds.project.group12.GeoSpatial.util.Utilities;

public class JoinQuery {
	
	public static final String debugPath = "/home/tchanana/Studies/DDS/Phase2/results/debug";
	public static final Boolean DEBUG = false;
	public static boolean spatialJoinQuery (String inputFilePath1, String inputFilePath2, String outputFilePath, JavaSparkContext sparkContext) {
	    //Read input file and store in RDD
	    JavaRDD<String> polyData1 = sparkContext.textFile(inputFilePath1);
	    JavaRDD<String> polyData2 = sparkContext.textFile(inputFilePath2);

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
	    
	    JavaRDD<Rectangle> queryRectangles = polyData2.map(new Function<String, Rectangle>(){
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

        final Broadcast<List<Rectangle>> qRectListBrod = sparkContext.broadcast(queryRectangles.collect());

	    // Create java pair RDD of rectangles that lie inside the other
	    JavaPairRDD<Rectangle, Rectangle> filteredRectangles = targetRectangles.flatMapToPair(new PairFlatMapFunction<Rectangle, Rectangle, Rectangle>(){
		public Iterable<Tuple2<Rectangle, Rectangle>> call(Rectangle rect)
		{
			List<Tuple2<Rectangle,Rectangle>> rPairList = new ArrayList<Tuple2<Rectangle,Rectangle>>();
			Rectangle qRect;
			//while(qRectListBrod.value().iterator().hasNext())
			int i=0;
			Iterator<Rectangle> itr = qRectListBrod.value().iterator();
			while(itr.hasNext())
			{
				//for each qRect in rectList // broadcasted list of rectangles
				qRect = itr.next();
				if (rect.doesLiesIn(qRect))
					rPairList.add(new scala.Tuple2<Rectangle,Rectangle>(rect,qRect));
			}
			return rPairList;
		}
	    });
	    
	   // Create Java RDD pairs of rectangles
	    if(DEBUG)
		    filteredRectangles.saveAsTextFile(debugPath+"/joinQuery/filteredRectangles");
 
	    // Performing group by to accumulate result for target rectangles
	    JavaPairRDD<Rectangle, Iterable<Rectangle>> result = filteredRectangles.groupByKey();

	    // prepare the output format RDD
	    JavaRDD<String> resultString = result.map(new Function<scala.Tuple2<Rectangle, Iterable<Rectangle>>, String>(){
	        public String call (Tuple2<Rectangle, Iterable<Rectangle>> T1)
	        {
			String s = T1._1.toString();
			Iterator<Rectangle> t = T1._2.iterator();
			while(t.hasNext())
			{
				s = s + "," + t.next().toString();
			}
		        return s;
	        }
	    });
	    resultString.coalesce(1).saveAsTextFile(outputFilePath);
	    return true;
	}

}
