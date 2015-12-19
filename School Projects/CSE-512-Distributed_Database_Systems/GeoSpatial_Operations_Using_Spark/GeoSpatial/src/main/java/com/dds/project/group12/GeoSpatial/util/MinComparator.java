/**
 * 
 */
package com.dds.project.group12.GeoSpatial.util;

import java.io.Serializable;
import java.util.Comparator;

import scala.Tuple2;

import com.dds.project.group12.GeoSpatial.entity.Point;

/**
 * @author nitina
 * Apr 26, 2015
 */
public class MinComparator implements Comparator<Tuple2<Double,Tuple2<Point,Point>>>, Serializable {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Tuple2<Double, Tuple2<Point, Point>> o1,
			Tuple2<Double, Tuple2<Point, Point>> o2) {
		// TODO Auto-generated method stub
	return (int)(o1._1() - o2._1());
		
	}

}
