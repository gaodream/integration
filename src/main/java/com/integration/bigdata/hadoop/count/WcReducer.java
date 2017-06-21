package com.integration.bigdata.hadoop.count;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class WcReducer extends Reducer<Text, LongWritable, Text, LongWritable>{

	@Override
	protected void reduce(Text key, Iterable<LongWritable> values,
			Context context) throws IOException, InterruptedException {
		long count = 0;
		for(LongWritable i : values){
			count += i.get();
		}
		context.write(key, new LongWritable(count));
		
	}

	
	
}
