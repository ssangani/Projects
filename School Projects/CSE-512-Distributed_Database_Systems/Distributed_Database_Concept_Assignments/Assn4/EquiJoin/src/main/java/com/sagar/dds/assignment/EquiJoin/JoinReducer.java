package com.sagar.dds.assignment.EquiJoin;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class JoinReducer extends Reducer<CompositeKeyWritable, Text, NullWritable, Text> {
	//private String separator = ",";
	NullWritable nullWritableKey = NullWritable.get();
	
	//	Configuartion variables
	private String RTypeValue;
	private String STypeValue;
	private String separator;
	
	@Override
	public void setup(Context context) {
		RTypeValue = context.getConfiguration().get("RTypeValue");
		STypeValue = context.getConfiguration().get("STypeValue");
		separator = context.getConfiguration().get("separator");
	}
	
	@Override
	public void reduce(CompositeKeyWritable ck, Iterable<Text> values, Context context) 
			throws IOException, InterruptedException {
		ArrayList<String> RKey = new ArrayList<String>();
		ArrayList<String> RVal = new ArrayList<String>();
		ArrayList<String> SKey = new ArrayList<String>();
		ArrayList<String> SVal = new ArrayList<String>();
		//	Push RTuple and STuple into separate tables
		for (Text value: values) {
			if(ck.getSourceType().equals(RTypeValue)) {
				RKey.add(ck.getJoinKey());
				RVal.add(value.toString());
			}
			else if(ck.getSourceType().equals(STypeValue)) {
				SKey.add(ck.getJoinKey());
				SVal.add(value.toString());
			}
		}
		
		//	Create a product between R and S and write the output
		//	in the form of (key,RTuple,STuple)
		for(int i = 0; i < RKey.size(); i++) {
			for(int j = 0; j < SKey.size(); j++) {
				if(RKey.get(i).equals(SKey.get(j))) {
					String outputValue = RKey.get(i) + separator + RVal.get(i) + separator + SVal.get(j);
					Text reduceOutputValue = new Text(outputValue);
					context.write(nullWritableKey, reduceOutputValue);
				}
			}
		}
	}
}
