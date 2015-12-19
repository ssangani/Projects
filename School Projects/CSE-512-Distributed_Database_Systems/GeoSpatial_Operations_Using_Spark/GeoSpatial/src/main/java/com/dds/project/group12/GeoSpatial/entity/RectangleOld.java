package com.dds.project.group12.GeoSpatial.entity;

import java.util.ArrayList;

public class RectangleOld {
	private double xMin;
	private double xMax;
	private double yMin;
	private double yMax;
	
	ArrayList<Edge> edges;
	
	public RectangleOld (double x1, double y1, double x2, double y2) {
		this.xMin = Math.min(x1, x2);
		this.xMax = Math.max(x1, x2);
		this.yMin = Math.min(y1, y2);
		this.yMax = Math.max(y1, y2);
	}
	
	public void setXMin (double x) {
		this.xMin = x;
	}
	
	public double getXMin () {
		return this.xMin;
	}
	
	public void setXMax (double x) {
		this.xMax = x;
	}
	
	public double getXMax () {
		return this.xMax;
	}
	
	public void setYMin (double y) {
		this.yMin = y;
	}
	
	public double getYMin () {
		return this.yMin;
	}
	
	public void setYMax (double y) {
		this.yMax = y;
	}
	
	public double getYMax () {
		return this.yMax;
	}
	
	public ArrayList<Edge> getAllEdges(){
		ArrayList<Edge> edges = new ArrayList<Edge>();
		edges.add(new Edge(new Point(this.xMin, this.yMin), new Point(this.xMin, this.yMax)));
		edges.add(new Edge(new Point(this.xMin, this.yMax), new Point(this.xMax, this.yMax)));
		edges.add(new Edge(new Point(this.xMax, this.yMax), new Point(this.xMax, this.yMin)));
		edges.add(new Edge(new Point(this.xMax, this.yMin), new Point(this.xMin, this.yMin)));
		return edges;
	}
	
	public ArrayList<Point> getAllPoints(){
		
		ArrayList<Point> points= new ArrayList<Point>();
		points.add(new Point(this.xMin, this.yMin));
		points.add(new Point(this.xMin, this.yMax));
		points.add(new Point(this.xMax, this.yMax));
		points.add(new Point(this.xMax, this.yMin));		
		return points;		
	}
	
	public int compareTo (RectangleOld r) {
		if ((r.getXMin()== this.xMin) && (r.getXMax() == this.xMax) && 
				(r.getYMin() == this.yMin) && (r.getYMax() == this.yMax)) {
			return 0;
		}
		else 
			return 1;
	}
	
	@Override
	public String toString() {
		return this.xMin + "," + this.yMin + "," + this.xMax + "," + this.yMax;
	}

}
