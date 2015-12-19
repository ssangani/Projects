package com.sagar.dds.assignment.EquiJoin;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class JoinMapper extends Mapper<LongWritable, Text, CompositeKeyWritable, Text> {
	CompositeKeyWritable ck = new CompositeKeyWritable();
	Text txtValue = new Text("");
	String sourceType;
	StringBuilder strValueTextBuilder = new StringBuilder("");
	
	//	Configuartion variables
	private int keyIndex;
	private int typeIndex;
	private String separator;
	
	@Override
	public void setup(Context context) {
		keyIndex = Integer.parseInt(context.getConfiguration().get("keyIndex"));
		typeIndex = Integer.parseInt(context.getConfiguration().get("typeIndex"));
		separator = context.getConfiguration().get("separator");
	}
	
	private String buildValueText(String[] valueParts) {
		//	Initialize
		strValueTextBuilder.setLength(0);
		//	Ignore first two parts as the parts of the key
		for(int i = 2; i < valueParts.length; i++) {
			strValueTextBuilder.append(valueParts[i]).append(",");
		}
		
		if(strValueTextBuilder.length() > 0) {
			//	Drop last comma
			strValueTextBuilder.setLength(strValueTextBuilder.length() - 1);
		}
		
		return strValueTextBuilder.toString();
	}
	
	@Override
	public void map(LongWritable key, Text value, Context context) 
			throws IOException, InterruptedException {
		if(value.toString().length() > 0) {
			String[] valueParts = value.toString().split(separator);
			
			ck.setJoinKey(valueParts[keyIndex]);
			ck.setSourceType(valueParts[typeIndex]);
			txtValue.set(buildValueText(valueParts));
			context.write(ck, txtValue);
		}
	}

}
