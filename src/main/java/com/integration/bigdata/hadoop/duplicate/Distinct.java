package com.integration.bigdata.hadoop.duplicate;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class Distinct {

	public static class TokenizerMapper
    			extends Mapper<Object, Text, Text, IntWritable>{
	
	}
	
	public static class TokenizerRecuder 
			extends Reducer<Text,IntWritable,Text,IntWritable>{
		
	}
		
}
