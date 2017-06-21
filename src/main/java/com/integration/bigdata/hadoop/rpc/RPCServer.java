package com.integration.bigdata.hadoop.rpc;

import java.io.IOException;

import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RPC.Server;

public class RPCServer implements Bizable{
	
	public String  sayHi(String name){
		
		return "Hi ~ " +name;
	}

	
	public static void main(String[] args) {
		Configuration conf = new Configuration();
		Server server = null;
			try {
				server = new RPC.Builder(conf).setProtocol(Bizable.class)
												   .setInstance(new RPCServer())
												   .setBindAddress("192.168.0.101")
												   .setPort(9527)
												   .build();
			} catch (HadoopIllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if(server != null){
			server.start();
		}
	}
}
