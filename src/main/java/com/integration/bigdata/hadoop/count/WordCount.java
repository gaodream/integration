package com.integration.bigdata.hadoop.count;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {
	
	
	public static void main(String[] args) throws IOException {
		Job job = Job.getInstance(new Configuration());
		
		job.setJarByClass(WordCount.class);
		//1.mapper设置
		job.setMapperClass(WcMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		
		FileInputFormat.setInputPaths(job, new Path("/words.txt"));
		//2.reducer设置
		job.setReducerClass(WcReducer.class);
		job.setOutputKeyClass(LongWritable.class);
		
		FileOutputFormat.setOutputPath(job, new Path("/wc_out"));
		
		try {
			//打印进度和详情
			job.waitForCompletion(true);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
