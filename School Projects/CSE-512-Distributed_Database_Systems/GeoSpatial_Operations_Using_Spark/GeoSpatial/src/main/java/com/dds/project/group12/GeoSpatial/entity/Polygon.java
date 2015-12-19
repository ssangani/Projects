package com.dds.project.group12.GeoSpatial.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Polygon implements Serializable {

	private Point a;
	private Point b;
	private Point c;
	private Point d;
	
	ArrayList<Edge> edges=null;
	
	public Polygon (Point a, Point c)
	{
		//	TODO - make sure a is left lower always
		this.a = a;
		this.b = new Point(a.getX(), c.getY());
		this.c = c;
		this.d =  new Point(c.getX(), a.getY());
	}
	
	public Polygon (double x1, double y1, double x2, double y2) {
		this.a = new Point(Math.min(x1, x2), Math.min(y1, y2));
		this.b = new Point(Math.min(x1, x2), Math.max(y1, y2));
		this.c = new Point(Math.max(x1, x2), Math.max(y1, y2));
		this.d = new Point(Math.max(x1, x2), Math.min(y1, y2));
	}
	
	@Override
	public String toString() {
		return "Polygon [a=" + a + ", b=" + b + ", c=" + c + ", d=" + d
				+ ", edges=" + edges + "]";
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
	
	public Point getC() {
		return c;
	}
	
	public void setC(Point c) {
		this.c = c;
	}
	
	public Point getD() {
		return d;
	}
	
	public void setD(Point d) {
		this.d = d;
	}
	
	public ArrayList<Edge> getEdges() {
		return edges;
	}
	
	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
	}
	
	public ArrayList<Edge> getAllEdges(){
		ArrayList<Edge> edges = new ArrayList<Edge>();
		edges.add(new Edge(a,b));
		edges.add(new Edge(b,c));
		edges.add(new Edge(c,d));
		edges.add(new Edge(d,a));
		return edges;
	}
	
	public ArrayList<Point> getAllPoints(){
		
		ArrayList<Point> points= new ArrayList<Point>();
		points.add(a);
		points.add(b);
		points.add(c);
		points.add(d);		
		return points;
		
	}
	
	public int compareTo (Polygon p) {
		if ((p.getA().compareTo(this.a) == 0) && (p.getB().compareTo(this.b) == 0) && 
				(p.getC().compareTo(this.c)== 0) && (p.getD().compareTo(this.d) == 0)) {
			return 0;
		}
		else return 1;
	}
	
	public boolean pointLiesWithin (Point p) {
		if ((p.compareTo(this.a) == 0) || (p.compareTo(this.b) == 0) || 
				(p.compareTo(this.c) == 0) || p.compareTo(this.d) == 0) {
			return false;
		}
		else if ((p.getX() > this.a.getX()) && (p.getX() < this.c.getX()) && 
				(p.getY() > this.a.getY()) && (p.getY() < this.c.getY())) {
			//System.out.println("~~~~~~~~~~~~~~~ Deletion Point: " + p.toString());
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isPointContained (Point p) {
		if ((p.compareTo(this.a) == 0) || (p.compareTo(this.b) == 0) || 
				(p.compareTo(this.c) == 0) || p.compareTo(this.d) == 0) {
			return false;
		}
		else if ((p.getX() >= this.a.getX()) && (p.getX() <= this.c.getX()) && 
				(p.getY() >= this.a.getY()) && (p.getY() <= this.c.getY())) {
			//System.out.println("~~~~~~~~~~~~~~~ Deletion Point: " + p.toString());
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isPolygonVertex (Point p) {
		if ((p.compareTo(this.getA()) == 0) || (p.compareTo(this.getB()) == 0) ||
				(p.compareTo(this.getC()) == 0) || (p.compareTo(this.getD()) == 0)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public ArrayList<Point> getIntersectionPoints (Polygon poly) {
		ArrayList<Point> intersectionPoints = new ArrayList<Point>();
		ArrayList<Edge> edgeList1 = this.getAllEdges();
		ArrayList<Edge> edgeList2 = poly.getAllEdges();
		for (int i = 0; i < edgeList1.size(); i++) {
			for (int j = 0; j < edgeList2.size(); j++) {
				Point p = edgeList1.get(i).getIntersection(edgeList2.get(j));
				if (p != null /*&& !(this.isPolygonVertex(p)) && !(poly.isPolygonVertex(p))*/) {
					intersectionPoints.add(p);
					//System.out.println("~~~~~~~~~~~~~~~ Intersection Point: " + p.toString());
				}
			}
		}		
		
		return intersectionPoints;
	}
}
