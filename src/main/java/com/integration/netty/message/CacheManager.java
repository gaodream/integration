package com.integration.netty.message;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CacheManager implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private static Map<String,NettyMessage> cache = new ConcurrentHashMap<String,NettyMessage>();

	
	public static NettyMessage get(String key){
		return cache.get(key);
	}
	
	public static void put(String key,NettyMessage msg){
		cache.put(key, msg);
	}
	
	public static void remove(String key){
		cache.remove(key);
	}
	
	
	public static void cleanAll(){
		cache.clear();
	}

	public static Map<String, NettyMessage> getCache() {
		return cache;
	}
	
}
