package com.integration.netty.udp;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public abstract class DefaultHandler<T> {
	
	protected final static Logger LOG = LoggerFactory.getLogger(DefaultHandler.class);
	
	private Class<T> clz = null;
	
	public DefaultHandler() {
		initialize();
	}
	
	@SuppressWarnings({ "unchecked" })
	public void initialize(){
		Type type = this.getClass().getGenericSuperclass();
		if (!(type instanceof ParameterizedType)) {
			clz = (Class<T>)Object.class;
		}
		Type param = ((ParameterizedType) type).getActualTypeArguments()[0];
		clz = (Class<T>)param;
	}
	
	
	public void preHandle(String msg,InetSocketAddress sender){
		 this.handle(JSON.parseObject(msg, clz), sender);
	}

	public abstract void handle(T t,InetSocketAddress sender);
	
	
	public void afterHandle(String msg,InetSocketAddress sender){
		
	}
}
