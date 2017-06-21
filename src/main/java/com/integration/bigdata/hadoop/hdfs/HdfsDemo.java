package com.integration.bigdata.hadoop.hdfs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class HdfsDemo {

	private FileSystem fs = null;

	@Before
	public void init() throws IOException, URISyntaxException, InterruptedException {
		Configuration conf = new Configuration();
		fs = FileSystem.get(new URI("hdfs://192.168.72.128:9000"), conf, "hadoop");
	}

	@Test
	public void testDownload() throws IllegalArgumentException, IOException {
		/*
		 * InputStream in = fs.open(new Path("/tns.jar")); OutputStream out =
		 * new FileOutputStream("F:/hadoop/tns.doc"); IOUtils.copyBytes(in, out,
		 * 4096,true);
		 */

		// 出现异常--待解决
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
				"com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
		// 初始化HDFS文件系统;
		Configuration cfg = new Configuration();
		cfg.set("hadoop.job.ugi", "hadoop,supergroup"); // "hadoop,supergroup"
		cfg.set("fs.default.name", "hdfs://192.168.72.128:9000"); // "hdfs://master:9000"
		cfg.set("mapred.job.tracker", "hdfs://192.168.72.128:9000"); // "hdfs://master:9001"
		cfg.set("dfs.http.address", "192.168.72.128:50070"); // "master:50070"
		fs.copyToLocalFile(false, new Path("/tns.jar"), new Path("F:/tns1.doc"));
	}

	@Test
	public void testUpload() throws IllegalArgumentException, IOException {
		InputStream in = new FileInputStream("F:/hadoop/tns.jar");
		OutputStream out = fs.create(new Path("/tns.jar"), true);
		IOUtils.copyBytes(in, out, 4096, true);

	}

	@Test
	public void testMkdir() throws IllegalArgumentException, IOException {
		boolean flag = fs.mkdirs(new Path("upload"));
		System.out.println(flag);
	}

}
