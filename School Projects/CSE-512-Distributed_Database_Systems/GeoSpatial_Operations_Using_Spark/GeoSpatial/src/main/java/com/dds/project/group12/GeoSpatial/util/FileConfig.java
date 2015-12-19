package com.dds.project.group12.GeoSpatial.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FileConfig
{
	private static FileConfig fileConfigInstance = null;
	
	private FileConfig () {
		//	prevent instantiation
	}
	
	public static FileConfig getInstance () {
		if (fileConfigInstance == null) {
			fileConfigInstance = new FileConfig();
		}
		return fileConfigInstance;
	}
	
	private String masterName;
	
	private String HDFSExcecution;
	
	private String closestPairInput;
	private String closestPairOutput;
	//private String closestPairHDFSInput;
	//private String closestPairHDFSOutput;

	private String farthestPairInput;
	private String farthestPairOutput;
	//private String farthestPairHDFSInput;
	//private String farthestPairHDFSOutput;

	private String convexHullInput;
	private String convexHullOutput;
	//private String convexHullHDFSInput;
	//private String convexHullHDFSOutput;

	private String rangeQueryInput1;
	private String rangeQueryInput2;
	private String rangeQueryOutput;
	//private String rangeQueryHDFSInput1;
	//private String rangeQueryHDFSInput2;
	//private String rangeQueryHDFSOutput;

	private String joinQueryInput1;
	private String joinQueryInput2;
	private String joinQueryOutput;
	//private String joinQueryHDFSInput1;
	//private String joinQueryHDFSInput2;
	//private String joinQueryHDFSOutput;
	
	private String unionInput;
	private String unionOutput;
	//private String unionHDFSInput;
	//private String unionHDFSOutput;
	
	private String heatMapInput1;
	private String heatMapInput2;
	private String heatMapOutput;

	
	public FileConfig filepaths(String configpath) throws IOException {	
		Properties prop = new Properties();
		String propFileName = configpath;
 
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
 
		if (inputStream != null) 
		{
			prop.load(inputStream);
		} 
		
		fileConfigInstance.setMasterName(prop.getProperty("masterName"));
		
		fileConfigInstance.setHDFSExcecution(prop.getProperty("HDFSExecution"));		
		
		fileConfigInstance.setClosestPairInput(prop.getProperty("closestPairInput"));
		fileConfigInstance.setClosestPairOutput(prop.getProperty("closestPairOutput"));
		//fileConfigInstance.setClosestPairHDFSInput(prop.getProperty("closestPairHDFSInput"));
		//fileConfigInstance.setClosestPairHDFSOutput(prop.getProperty("closestPairHDFSOutput"));
		
		fileConfigInstance.setFarthestPairInput(prop.getProperty("farthestPairInput"));
		fileConfigInstance.setFarthestPairOutput(prop.getProperty("farthestPairOutput"));
		//fileConfigInstance.setFarthestPairHDFSInput(prop.getProperty("farthestPairHDFSInput"));
		//fileConfigInstance.setFarthestPairHDFSOutput(prop.getProperty("farthestPairHDFSOutput"));
		
		fileConfigInstance.setConvexHullInput(prop.getProperty("convexHullInput"));
		fileConfigInstance.setConvexHullOutput(prop.getProperty("convexHullOutput"));
		//fileConfigInstance.setConvexHullHDFSInput(prop.getProperty("convexHullHDFSInput"));
		//fileConfigInstance.setConvexHullHDFSOutput(prop.getProperty("convexHullHDFSOutput"));
		
		fileConfigInstance.setJoinQueryInput1(prop.getProperty("joinQueryInput1"));
		fileConfigInstance.setJoinQueryInput2(prop.getProperty("joinQueryInput2"));
		fileConfigInstance.setJoinQueryOutput(prop.getProperty("joinQueryOutput"));
		//fileConfigInstance.setJoinQueryHDFSInput1(prop.getProperty("joinQueryHDFSInput1"));
		//fileConfigInstance.setJoinQueryHDFSInput2(prop.getProperty("joinQueryHDFSInput2"));
		//fileConfigInstance.setJoinQueryHDFSOutput(prop.getProperty("joinQueryHDFSOutput"));
		
		fileConfigInstance.setRangeQueryInput1(prop.getProperty("rangeQueryInput1"));
		fileConfigInstance.setRangeQueryInput2(prop.getProperty("rangeQueryInput2"));
		fileConfigInstance.setRangeQueryOutput(prop.getProperty("rangeQueryOutput"));
		//fileConfigInstance.setRangeQueryHDFSInput1(prop.getProperty("rangeQueryHDFSInput1"));
		//fileConfigInstance.setRangeQueryHDFSInput2(prop.getProperty("rangeQueryHDFSInput2"));
		//fileConfigInstance.setRangeQueryHDFSOutput(prop.getProperty("rangeQueryHDFSOutput"));
		
		fileConfigInstance.setUnionInput(prop.getProperty("unionInput"));
		fileConfigInstance.setUnionOutput(prop.getProperty("unionOutput"));
		//fileConfigInstance.setUnionHDFSInput(prop.getProperty("unionHDFSInput"));
		//fileConfigInstance.setUnionHDFSOutput(prop.getProperty("unionHDFSOutput"));
		
		fileConfigInstance.setHeatMapInput1(prop.getProperty("heatMapInput1"));
		fileConfigInstance.setHeatMapInput2(prop.getProperty("heatMapInput2"));
		fileConfigInstance.setHeatMapOutput(prop.getProperty("heatMapOutput"));
		
		return fileConfigInstance;
	}

	public String getMasterName() {
		return masterName;
	}
	
	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}
	
	public String getHDFSExcecution() {
		return HDFSExcecution;
	}
	
	public void setHDFSExcecution(String HDFSExcecution) {
		this.HDFSExcecution = HDFSExcecution;
	}
	
	public String getClosestPairInput() {
		return closestPairInput;
	}


	public void setClosestPairInput(String closestPairInput) {
		this.closestPairInput = closestPairInput;
	}


	public String getClosestPairOutput() {
		return closestPairOutput;
	}


	public void setClosestPairOutput(String closestPairOutput) {
		this.closestPairOutput = closestPairOutput;
	}


	//public String getClosestPairHDFSInput() {
	//	return closestPairHDFSInput;
	//}


	//public void setClosestPairHDFSInput(String closestPairHDFSInput) {
	//	this.closestPairHDFSInput = closestPairHDFSInput;
	//}


	//public String getClosestPairHDFSOutput() {
	//	return closestPairHDFSOutput;
	//}


	//public void setClosestPairHDFSOutput(String closestPairHDFSOutput) {
	//	this.closestPairHDFSOutput = closestPairHDFSOutput;
	//}


	public String getFarthestPairInput() {
		return farthestPairInput;
	}


	public void setFarthestPairInput(String farthestPairInput) {
		this.farthestPairInput = farthestPairInput;
	}


	public String getFarthestPairOutput() {
		return farthestPairOutput;
	}


	public void setFarthestPairOutput(String farthestPairOutput) {
		this.farthestPairOutput = farthestPairOutput;
	}


	//public String getFarthestPairHDFSInput() {
	//	return farthestPairHDFSInput;
	//}


	//public void setFarthestPairHDFSInput(String farthestPairHDFSInput) {
	//	this.farthestPairHDFSInput = farthestPairHDFSInput;
	//}


	//public String getFarthestPairHDFSOutput() {
	//	return farthestPairHDFSOutput;
	//}


	//public void setFarthestPairHDFSOutput(String farthestPairHDFSOutput) {
	//	this.farthestPairHDFSOutput = farthestPairHDFSOutput;
	//}


	public String getConvexHullInput() {
		return convexHullInput;
	}


	public void setConvexHullInput(String convexHullInput) {
		this.convexHullInput = convexHullInput;
	}


	public String getConvexHullOutput() {
		return convexHullOutput;
	}


	public void setConvexHullOutput(String convexHullOutput) {
		this.convexHullOutput = convexHullOutput;
	}


	//public String getConvexHullHDFSInput() {
	//	return convexHullHDFSInput;
	//}


	//public void setConvexHullHDFSInput(String convexHullHDFSInput) {
	//	this.convexHullHDFSInput = convexHullHDFSInput;
	//}


	//public String getConvexHullHDFSOutput() {
	//	return convexHullHDFSOutput;
	//}


	//public void setConvexHullHDFSOutput(String convexHullHDFSOutput) {
	//	this.convexHullHDFSOutput = convexHullHDFSOutput;
	//}


	public String getRangeQueryInput1() {
		return rangeQueryInput1;
	}


	public void setRangeQueryInput1(String rangeQueryInput) {
		this.rangeQueryInput1 = rangeQueryInput;
	}
	
	public String getRangeQueryInput2() {
		return rangeQueryInput2;
	}
	
	public void setRangeQueryInput2(String rangeQueryInput) {
		this.rangeQueryInput2 = rangeQueryInput;
	}


	public String getRangeQueryOutput() {
		return rangeQueryOutput;
	}


	public void setRangeQueryOutput(String rangeQueryOutput) {
		this.rangeQueryOutput = rangeQueryOutput;
	}


	//public String getRangeQueryHDFSInput1() {
	//	return rangeQueryHDFSInput1;
	//}


	//public void setRangeQueryHDFSInput1(String rangeQueryHDFSInput) {
	//	this.rangeQueryHDFSInput1 = rangeQueryHDFSInput;
	//}
	
	//public String getRangeQueryHDFSInput2() {
	//	return rangeQueryHDFSInput2;
	//}


	//public void setRangeQueryHDFSInput2(String rangeQueryHDFSInput) {
	//	this.rangeQueryHDFSInput2 = rangeQueryHDFSInput;
	//}


	//public String getRangeQueryHDFSOutput() {
	//	return rangeQueryHDFSOutput;
	//}


	//public void setRangeQueryHDFSOutput(String rangeQueryHDFSOutput) {
	//	this.rangeQueryHDFSOutput = rangeQueryHDFSOutput;
	//}


	public String getJoinQueryInput1() {
		return joinQueryInput1;
	}


	public void setJoinQueryInput1(String joinQueryInput) {
		this.joinQueryInput1 = joinQueryInput;
	}
	
	public String getJoinQueryInput2() {
		return joinQueryInput2;
	}


	public void setJoinQueryInput2(String joinQueryInput) {
		this.joinQueryInput2 = joinQueryInput;
	}


	public String getJoinQueryOutput() {
		return joinQueryOutput;
	}


	public void setJoinQueryOutput(String joinQueryOutput) {
		this.joinQueryOutput = joinQueryOutput;
	}


	//public String getJoinQueryHDFSInput1() {
	//	return joinQueryHDFSInput1;
	//}


	//public void setJoinQueryHDFSInput1(String joinQueryHDFSInput) {
	//	this.joinQueryHDFSInput1 = joinQueryHDFSInput;
	//}
	
	//public String getJoinQueryHDFSInput2() {
	//	return joinQueryHDFSInput2;
	//}


	//public void setJoinQueryHDFSInput2(String joinQueryHDFSInput) {
	//	this.joinQueryHDFSInput2 = joinQueryHDFSInput;
	//}


	//public String getJoinQueryHDFSOutput() {
	//	return joinQueryHDFSOutput;
	//}


	//public void setJoinQueryHDFSOutput(String joinQueryHDFSOutput) {
	//	this.joinQueryHDFSOutput = joinQueryHDFSOutput;
	//}

	
	public String getUnionInput() {
		return unionInput;
	}


	public void setUnionInput(String unionInput) {
		this.unionInput = unionInput;
	}


	public String getUnionOutput() {
		return unionOutput;
	}


	public void setUnionOutput(String unionOutput) {
		this.unionOutput = unionOutput;
	}


	//public String getUnionHDFSInput() {
	//	return unionHDFSInput;
	//}


	//public void setUnionHDFSInput(String unionHDFSInput) {
	//	this.unionHDFSInput = unionHDFSInput;
	//}


	//public String getUnionHDFSOutput() {
	//	return unionHDFSOutput;
	//}


	//public void setUnionHDFSOutput(String unionHDFSOutput) {
	//	this.unionHDFSOutput = unionHDFSOutput;
	//}
	
	public String getHeatMapInput1() {
		return heatMapInput1;
	}
	
	public void setHeatMapInput1(String heatMapInput1) {
		this.heatMapInput1 = heatMapInput1;
	}
	
	public String getHeatMapInput2() {
		return heatMapInput2;
	}
	
	public void setHeatMapInput2(String heatMapInput2) {
		this.heatMapInput2 = heatMapInput2;
	}
	
	public String getHeatMapOutput() {
		return heatMapOutput;
	}
	
	public void setHeatMapOutput(String heatMapOutput) {
		this.heatMapOutput = heatMapOutput;
	}
}
