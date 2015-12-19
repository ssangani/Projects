package com.sagar.dds.assignment.EquiJoin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableUtils;

public class CompositeKeyWritable implements Writable, WritableComparable<CompositeKeyWritable>{
	
	private String joinKey;
	private String sourceType;
	
	public CompositeKeyWritable() {
		
	}
	
	public CompositeKeyWritable(String joinKey, String sourceType) {
		this.joinKey = joinKey;
		this.sourceType = sourceType;
	}
	
	public void setJoinKey(String joinKey) {
		this.joinKey = joinKey;
	}
	
	public String getJoinKey() {
		return this.joinKey;
	}
	
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	
	public String getSourceType() {
		return this.sourceType;
	}

	public int compareTo(CompositeKeyWritable ck) {
		int result = (this.joinKey).compareTo(ck.getJoinKey());
		if(result == 0) {
			result = this.sourceType.compareTo(ck.getSourceType());
		}
		return result;
	}

	public void readFields(DataInput dataIn) throws IOException {
		this.joinKey = WritableUtils.readString(dataIn);
		this.sourceType = WritableUtils.readString(dataIn);
	}

	public void write(DataOutput dataOut) throws IOException {
		WritableUtils.writeString(dataOut, this.joinKey);
		WritableUtils.writeString(dataOut, this.sourceType);
	}
	
	@Override
	public String toString() {
		return this.joinKey + "," + this.sourceType;
	}
}
