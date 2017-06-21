package com.integration.bigdata.hadoop.rpc;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

public class RPCClient {


	
	public static void main(String[] args) throws IOException {
		Bizable proxy = RPC.getProxy(Bizable.class, 10010, new InetSocketAddress("192.168.0.101",9527), new Configuration());
		
		System.out.println(proxy.sayHi("tomcat"));
		RPC.stopProxy(proxy);
	}
}
