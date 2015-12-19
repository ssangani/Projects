package com.dds.project.group12.GeoSpatial.entity;

import java.io.Serializable;

public class Edge implements Serializable {
	
	private Point a;
	private Point b;
	
	public Edge(Point a, Point b)
	{
		this.a = a;
		this.b = b;
	}
	
	@Override
	public String toString() {
		return "Edge [a=" + a + ", b=" + b + "]";
	}
	
	public Point getA() {
		return a;
	}
	
	public void setA(Point a) {
		this.a = a;
	}
	
	public Point getB() {
		return b;
	}
	
	public void setB(Point b) {
		this.b = b;
	}
	
	public Point getIntersection (Edge e) {
		double x1 = this.getA().getX();
		double y1 = this.getA().getY();
		double x2 = this.getB().getX();
		double y2 = this.getB().getY();
		
		double x3 = e.getA().getX();
		double y3 = e.getA().getY();
		double x4 = e.getB().getX();
		double y4 = e.getB().getY();
		
		int cond = 0;
		
		if (x1 == x2) {
			if (x3 == x4) {
				return null;
			}
			else if (y3 == y4) {
				cond = 4;
			}
		}
		else if (y1 == y2) {
			if (y3 == y4) {
				return null;
			}
			else if (x3 == x4) {
				cond = 3;
			}
		}
		
		switch (cond) {
			case 3: 
				if(((x1 < x3) && (x2 < x3)) || ((x1 > x3) && (x2 > x3))) {
					return null;
				}
				if(((y1 < y3) && (y1 < y4)) || ((y1 > y3) && (y1 > y4))) {
					return null;
				}
				else {
					return new Point(x3, y1);
				}
			case 4:
				if(((x3 < x1) && (x4 < x1)) || ((x3 > x1) && (x4 > x1))) {
					return null;
				}
				if(((y3 < y1) && (y3 < y2)) || ((y3 > y1) && (y3 > y2))) {
					return null;
				}
				else {
					return new Point(x1, y3);
				}
			default:
					return null;
		}
	}
	
	/*
	public Point getIntersection (Edge e) {
		double x1 = this.getA().getX();
		double y1 = this.getA().getY();
		double x2 = this.getB().getX();
		double y2 = this.getB().getY();
		
		double x3 = e.getA().getX();
		double y3 = e.getA().getY();
		double x4 = e.getB().getX();
		double y4 = e.getB().getY();
		
		double d = (((x1 - x2)*(y3 - y4)) - ((x3 - x4)*(y1 - y2)));
		System.out.print(this.toString() + "|| " + e.toString() + "d:" + d);
		if (d == 0) {
			//	lines are parallel
			//	case A - lines don't overlap  -> no intersection point
			//	case B - lines overlap -> intersection points are end points of segments
			return null;
		}
		else {			
			double x = (((x3 - x4)*((x1*y2) - (x2*y1))) - ((x1 - x2)*((x3*y4) - (x4*y3))))/d;
			double y = (((y3 - y4)*((x1*y2) - (x2*y1))) - ((y1 - y2)*((x3*y4) - (x4*y3))))/d;
			System.out.print(" ("+x+","+y+")\n");
			//	check if point lies within bounding box of both lines simultaneously
			if ((x < Math.min(x1, x2)) || (x > Math.max(x1, x2))) {
				return null;
			}
			else if ((x < Math.min(x3, x3)) || (x > Math.max(x3, x4))) {
				return null;
			}
			else if ((y < Math.min(y1, y2)) || (y > Math.max(y1, y2))) {
				return null;
			}
			else if ((y < Math.min(y3, y4)) || (y > Math.max(y3, y4))) {
				return null;
			}
			else {
				//System.out.println("Edge A - (" + x1+ ","+y1+") ("+x2+","+y2+")");
				//System.out.println("Edge B - (" + x3+ ","+y3+") ("+x4+","+y4+")");
				//System.out.println("Intersect - (" + x+ ","+y+")");
				return new Point(x, y);
			}
		}
	}
	*/
}
