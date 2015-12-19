package com.sagar.dds.assignment.EquiJoin;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class JoinGroupingComparator extends WritableComparator{
	protected JoinGroupingComparator() {
		super(CompositeKeyWritable.class, true);
	}
	
	@Override
	public int compare(WritableComparable wc1, WritableComparable wc2) {
		CompositeKeyWritable ck1 = (CompositeKeyWritable) wc1;
		CompositeKeyWritable ck2 = (CompositeKeyWritable) wc2;
		int result = (ck1.getJoinKey()).compareTo(ck2.getJoinKey());
		return result;
	}

}
