package com.dds.project.group12.GeoSpatial;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import com.dds.project.group12.GeoSpatial.entity.Rectangle;
import com.dds.project.group12.GeoSpatial.util.Utilities;

import scala.Tuple2;

public class RangeQuery {

	public static final String debugPath = "/home/tchanana/Studies/DDS/Phase2/results/debug";
	public static final Boolean DEBUG = false;

	public static boolean spatialRangeQuery(String inputFilePath1, String inputFilePath2, String outputFilePath, 
			JavaSparkContext sparkContext) {
		//Read input file and store in RDD
		JavaRDD<String> polyData1 = sparkContext.textFile(inputFilePath1);
		JavaRDD<String> polyData2 = sparkContext.textFile(inputFilePath2);

		// Create RDD of Rectangles
		JavaRDD<Rectangle> rectangles = polyData1.map(new Function<String, Rectangle>(){
			public Rectangle call(String s){
				String[] parts = s.split(",");
				Rectangle r = new Rectangle(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), 
							Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
				
				return r;
		}
		});
		JavaRDD<Rectangle> queryRectangle = polyData2.map(new Function<String, Rectangle>(){
			public Rectangle call(String s){
				String[] parts = s.split(",");
				Rectangle r = new Rectangle(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), 
						Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
				return r;
			}
		});
		final Rectangle qRect = queryRectangle.first();

		JavaRDD<Rectangle> filteredRectangles = rectangles.filter(new Function<Rectangle, Boolean>() {
			public Boolean call(Rectangle rect) {
				return rect.doesLiesIn(qRect);
			}
		});

		if(DEBUG)
			filteredRectangles.saveAsTextFile(debugPath+"/rangeQuery2/filteredRectangles");

		filteredRectangles.saveAsTextFile(outputFilePath);
		return true;
	}

}
