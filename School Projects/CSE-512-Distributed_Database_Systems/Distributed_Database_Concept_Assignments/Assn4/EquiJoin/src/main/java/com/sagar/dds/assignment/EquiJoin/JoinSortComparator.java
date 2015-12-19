package com.sagar.dds.assignment.EquiJoin;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class JoinSortComparator extends WritableComparator {
	protected JoinSortComparator() {
		super(CompositeKeyWritable.class, true);
	}
	
	@Override
	public int compare(WritableComparable wc1, WritableComparable wc2) {
		CompositeKeyWritable ck1 = (CompositeKeyWritable) wc1;
		CompositeKeyWritable ck2 = (CompositeKeyWritable) wc2;
		int result = ck1.compareTo(ck2);
		return result;
	}

}
