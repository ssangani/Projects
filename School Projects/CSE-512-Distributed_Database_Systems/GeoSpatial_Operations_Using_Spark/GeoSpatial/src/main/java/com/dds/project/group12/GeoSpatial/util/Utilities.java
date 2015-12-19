package com.dds.project.group12.GeoSpatial.util;

import com.dds.project.group12.GeoSpatial.entity.Point;
import com.dds.project.group12.GeoSpatial.entity.Polygon;
import com.dds.project.group12.GeoSpatial.entity.Rectangle;

import org.apache.spark.metrics.sink.GangliaSink;


public class Utilities {


	public static String generateOutputFilePath(String inputFilePath)
	{
		String prefix;
		String separator = System.getProperty("file.separator");
		if(separator.contains("/"))
		{
			prefix = inputFilePath.substring(0, inputFilePath.lastIndexOf(separator)+1);
		}
		else
		{
			prefix = inputFilePath.substring(0, inputFilePath.lastIndexOf(separator)+1);
		}
		String outputFilePath = prefix+"ConvexHullOutput";
		if(outputFilePath.contains("file:///"))
			outputFilePath = outputFilePath.replace("file:///", "hdfs:///");
		System.out.println("OutputFile Path: "+outputFilePath);
		return outputFilePath;
	}

	public static Polygon getPolygon(String s) {
		String pointsStr[] = s.split(",");
		double x1;
		double y1;
		double x2;
		double y2;
		try{
			x1 = Double.parseDouble(pointsStr[0]);
			y1 = Double.parseDouble(pointsStr[1]);
			x2 = Double.parseDouble(pointsStr[2]);
			y2 = Double.parseDouble(pointsStr[3]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return new Polygon(x1, y1, x2, y2);
	}

	public static Point getPointAreaLm(String s) {
		String[] pointsStr = s.split(",");
		//printStringArray(pointsStr);
		double x1 = Double.parseDouble(pointsStr[2]);
		double y1 = Double.parseDouble(pointsStr[3]);
		return new Point(x1, y1);
	}

	public static Point getPoint(String s) {
		String[] pointsStr = s.split(",");
		double x1;
		double y1;
		//printStringArray(pointsStr);
		try{
			x1 = Double.parseDouble(pointsStr[0]);
			y1 = Double.parseDouble(pointsStr[1]);
		}
		catch(Exception e)
		{
			System.out.println("Cannot parse");
			//e.printStackTrace();
			return null;
		}
		return new Point(x1, y1);
	}

	public static Rectangle getRectangle(String s)
	{
		String pointsStr[] = s.split(",");
		double x1;
		double y1;
		double x2;
		double y2;
		try{
			x1 = Double.parseDouble(pointsStr[0]);
			y1 = Double.parseDouble(pointsStr[1]);
			x2 = Double.parseDouble(pointsStr[2]);
			y2 = Double.parseDouble(pointsStr[3]);
		}
		catch(Exception e)
		{
			System.out.println("Cannot parse");
			//e.printStackTrace();
			return null;
		}
		return new Rectangle(x1, y1, x2, y2);
	}

	public static void printStringArray(String arr[])
	{
		for(String s: arr)
		{
			System.out.println(s);
		}
	}

}
