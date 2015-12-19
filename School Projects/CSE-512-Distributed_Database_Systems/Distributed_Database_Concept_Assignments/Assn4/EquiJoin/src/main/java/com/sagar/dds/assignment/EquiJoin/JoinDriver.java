package com.sagar.dds.assignment.EquiJoin;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class JoinDriver {
	public static void main(String[] args) throws Exception {
		
		Configuration config = new Configuration();
		config.set("keyIndex", "1");
		config.set("typeIndex", "0");
		config.set("RTypeValue", "R");
		config.set("STypeValue", "S");
		config.set("separator", ",");
		
		Job job = Job.getInstance(config, "ReduceSideJoin");
		job.setJarByClass(JoinDriver.class);
		job.setJobName("ReduceSideJoin");
		
		FileInputFormat.setInputPaths(job, "/home/sagar/Downloads/DDS/assn4/data.csv");
		FileOutputFormat.setOutputPath(job, new Path("/home/sagar/Downloads/DDS/assn4/result"));
		
		job.setMapperClass(JoinMapper.class);
		job.setMapOutputKeyClass(CompositeKeyWritable.class);
		job.setMapOutputValueClass(Text.class);
 
		job.setPartitionerClass(JoinPartitioner.class);
		job.setSortComparatorClass(JoinSortComparator.class);
		job.setGroupingComparatorClass(JoinGroupingComparator.class);
 
		job.setNumReduceTasks(4);
		job.setReducerClass(JoinReducer.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
