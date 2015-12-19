package com.dds.project.group12.GeoSpatial.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Rectangle implements Serializable {

	private double x1;
	private double y1;
	private double x2;
	private double y2;
	
	public Rectangle (double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public String toString() {
		return Double.toString(this.x1) + "," + Double.toString(this.y1) + "," + Double.toString(this.x2) + "," + Double.toString(this.y2) ;
	}

	public int compareTo (Rectangle r) {
		if((this.x1 == r.x1) && (this.x2 == r.x2) && (this.y1 == r.y1) && (this.y2 == r.y2))
			return 0;
		else
			return 1;
	}

	public boolean doesLiesIn (Rectangle r) {
	    double maxX = 0;
	    double minX = 0;
	    double maxY = 0;
	    double minY = 0;

	    if(r.x1 > r.x2)
	    {
		maxX = r.x1;
	        minX = r.x2;
	    }
	    else
	    {
		maxX = r.x2;
		minX = r.x1;
	    }

	    if(r.y1 < r.y2)
	    {
		maxY = r.y2;
		minY = r.y1;
	    }
	    else
	    {
		maxY = r.y1;
		minY = r.y2;
	    }

	    if((minX<=this.x1) && (minX<=this.x2) 
              && (maxX>=this.x1) && (maxX>=this.x2) 
              && (maxY>=this.y1) && (maxY>=this.y2) 
              && (minY<=this.y1) && (minY<=this.y2))
                return true;
            else
                return false;
/*
		if(x1 < x2)
		{
			this.xmin = x1;
			this.xmax = x2;
		}
		else
		{
			this.xmin = x2;
			this.xmax = x1;
		}

		if(y1 < y2)
		{
			this.ymin = y1;
			this.ymax = y2;
		}
		else
		{
			this.ymin = y2;
			this.ymax = y1;
		}
		if((r.xmin <= this.xmin) && (r.xmin <= this.xmax) 
	              && (r.xmax >= this.xmin) && (r.xmax >= this.xmax) 
	              && (r.ymax >= this.ymin) && (r.ymax >= this.ymax) 
	              && (r.ymin <= this.ymin) && (r.ymin <= this.ymax))
	                return true;
	        else
	                return false;
*/
	}
	
	public boolean doesPointLieIn(Point p) {
		double xMin = Math.min(this.x1, this.x2);
		double xMax = Math.max(this.x1, this.x2);
		double yMin = Math.min(this.y1, this.y2);
		double yMax = Math.max(this.y1, this.y2);
		
		if((p.getX() > xMin) && (p.getX() < xMax) && (p.getY() > yMin) && (p.getY() < yMax)) {
			return true;
		}
		else {
			return false;
		}
	}
}
