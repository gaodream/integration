package com.integration.bigdata.hadoop.count;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class WcMapper extends Mapper<LongWritable, Text, Text, LongWritable>{


	@Override
	protected void map(LongWritable key, Text value,Context context)
			throws IOException, InterruptedException {
		//super.map(key, value, context);
		String[] words = value.toString().split(" ");
		for(String word :words ){
			context.write(new Text(word), new LongWritable(1));
		}
	}
	
	

}
