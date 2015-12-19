package com.sagar.dds.assignment.EquiJoin;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class JoinPartitioner extends Partitioner<CompositeKeyWritable, Text> {

	@Override
	public int getPartition(CompositeKeyWritable ck, Text value, int reduceTaskNum) {
		int result = (ck.getJoinKey().hashCode()) % reduceTaskNum; 
		return result;
	}
}
