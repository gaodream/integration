package com.integration.bigdata.redis.queue;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.integration.bigdata.redis.message.DefaultRedisMessage;
import com.integration.bigdata.redis.message.RedisMessage;
import com.integration.bigdata.redis.test.RedisManager;

import redis.clients.jedis.Jedis;

public  class DefaultRedisQueue<T extends DefaultRedisMessage> implements RedisQueue<RedisMessage>{

	private final static int REDIS_DB_IDX = 9;

	private final static Logger logger = Logger.getLogger(DefaultRedisQueue.class);

	private final String queueName ;
	
	
	public DefaultRedisQueue(String queueName) {
		super();
		this.queueName = queueName;
	}

	@Override
	public String getQueueName() {
		// TODO Auto-generated method stub
		return queueName;
	}

	@Override
	public void pushMessage(RedisMessage message) {
		Jedis jedis = null;
        try{
            jedis = RedisManager.getResource(REDIS_DB_IDX);
            jedis.lpush(this.queueName, message.toString());
        }catch(Throwable e){
            logger.error(e.getMessage(),e);
        }finally{
            if(jedis!=null){
                RedisManager.returnResource(jedis);
            }
        }
		
	}

	@Override
	public RedisMessage popMessage() {
		Jedis jedis = null;
        String message = null;
        try{
            jedis = RedisManager.getResource(REDIS_DB_IDX);
            message = jedis.rpop(this.queueName);
        }catch(Throwable e){
            logger.error(e.getMessage(),e);
        }finally{
            if(jedis!=null){
                RedisManager.returnResource(jedis);
            }
        }
        return JSONObject.parseObject(message, RedisMessage.class);
	}
	

}
