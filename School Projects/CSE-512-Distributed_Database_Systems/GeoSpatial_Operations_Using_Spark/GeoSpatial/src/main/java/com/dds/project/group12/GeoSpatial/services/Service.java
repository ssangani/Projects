package com.dds.project.group12.GeoSpatial.services;

import java.util.ArrayList;
import java.util.List;

import com.dds.project.group12.GeoSpatial.entity.Edge;
import com.dds.project.group12.GeoSpatial.entity.Point;

/**
 * @author nitina
 * Feb 23, 2015
 */
public class Service {

	public static List<Point> getIntersectionPoints(List<Edge> edges, Edge edge)
	{
		List<Point> points = new ArrayList<Point>();
		for(Edge e: edges)
		{
			Point p =getIntersectionPoint(e, edge);
			if (p!=null)
				points.add(p);
		}
		System.out.println("In Service");
		return points;
	}
	public static Point getIntersectionPoint(Edge e1, Edge e2){
		double x1 = e1.getA().getX();
		double x2 = e1.getB().getX();
		double y1= e1.getA().getY();
		double y2= e1.getB().getY();

		double x3 = e2.getA().getX();
		double x4 = e2.getB().getX();
		double y3= e2.getA().getY();
		double y4= e2.getB().getY();

		double d = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);

		if (d == 0) return null;
		double xi = ((x3-x4)*(x1*y2-y1*x2)-(x1-x2)*(x3*y4-y3*x4))/d;
		double yi = ((y3-y4)*(x1*y2-y1*x2)-(y1-y2)*(x3*y4-y3*x4))/d;
		Point p = new Point(xi,yi);
		if (xi < Math.min(x1,x2) || xi > Math.max(x1,x2)) return null;
		if (xi < Math.min(x3,x4) || xi > Math.max(x3,x4)) return null;
		return p;
	}

	public static double getCrossProduct(Point o, Point a, Point b)
	{

		return (a.getX() - o.getX()) * (b.getY() - o.getY()) - (a.getY() - o.getY()) * (b.getX() - o.getX());

	}

}



























