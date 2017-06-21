package com.integration.bigdata.redis.message;

import java.io.Serializable;

public interface RedisMessage extends Serializable{
	
	 public String toJson();
}
