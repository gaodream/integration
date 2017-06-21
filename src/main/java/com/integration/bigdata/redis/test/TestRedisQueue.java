package com.integration.bigdata.redis.test;

import org.apache.log4j.Logger;

import com.integration.bigdata.redis.message.RedisMessage;
import com.integration.bigdata.redis.queue.RedisQueue;

public class TestRedisQueue {

	
	protected final static Logger logger = Logger.getLogger(TestRedisQueue.class);

    
    public void execute()  {
    	RedisQueue<RedisMessage> redisQueue = null;
    	RedisMessage message = null;
        try {
        	redisQueue = TaskQueueManager.get(TaskQueueManager.SMS_QUEUE);
            /**
             * 读取队列
             */
            message = redisQueue.popMessage();
        }catch(Throwable e){
            logger.error(e.getMessage(),e);
            e.printStackTrace();
        }
        System.out.println(message);
    }

}
