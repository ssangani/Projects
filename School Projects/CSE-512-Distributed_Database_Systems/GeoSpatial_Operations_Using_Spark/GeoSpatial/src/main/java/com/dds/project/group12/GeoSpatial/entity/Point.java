package com.dds.project.group12.GeoSpatial.entity;

import java.io.Serializable;

public class Point implements Serializable, Comparable<Point>  {


	private double x;
	private double y;
	
	public Point () {}
	
	public Point (double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	
	@Override
	public String toString() {
		return x + "," + y;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	
	public int compareTo(Point o) {
		// TODO Auto-generated method stub
		//System.out.println(this.toString() + " ---- " + o.toString());
		if(o.getX() == this.getX())
		{
			double diff = this.getY() - o.getY();
			if(diff < 0.0)
				return -1;
			else if(diff > 0.0)
				return 1;
			else
				return 0;
		}
		else
		{
			double diff = this.getX() - o.getX();
			if(diff < 0.0)
				return -1;
			else if(diff > 0.0)
				return 1;
			else
				return 0;
		}
	}
	
	@Override
	public boolean equals (Object obj) {
		if ((obj instanceof Point) && (obj != null)) {
			Point p = (Point) obj;
			if ((this.getX() == p.getX()) && (this.getY() == p.getY())) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public double distance(Point p)
	{
		double x=Math.pow((this.x-p.getX()), 2);
        double y=Math.pow((this.y-p.getY()), 2);
        return x+y;        
	}
}