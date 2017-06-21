package com.integration.bigdata.redis.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.integration.bigdata.redis.message.DefaultRedisMessage;
import com.integration.bigdata.redis.message.RedisMessage;
import com.integration.bigdata.redis.queue.DefaultRedisQueue;
import com.integration.bigdata.redis.queue.RedisQueue;

public class TaskQueueManager {
	
	 protected final static Logger logger = Logger.getLogger(TaskQueueManager.class);

	 private static Map<String, RedisQueue<RedisMessage>> queueMap = new ConcurrentHashMap<String, RedisQueue<RedisMessage>>();

	 //短信队列名。
	 public static final String SMS_QUEUE = "SMS_QUEUE";

	 //规则队列名。
	 public static final String RULE_QUEUE = "RULE_QUEUE";
	 
    private static void initQueueMap() {
        logger.debug("初始化任务队列...");
        queueMap.put(RULE_QUEUE, new DefaultRedisQueue<DefaultRedisMessage>(RULE_QUEUE));
        logger.debug("建立队列："+RULE_QUEUE);
        queueMap.put(SMS_QUEUE, new DefaultRedisQueue<DefaultRedisMessage>(SMS_QUEUE));
        logger.debug("建立队列："+SMS_QUEUE);
    }
    
    static {
        initQueueMap();
    }
    
    public static RedisQueue<RedisMessage> get(String name){
        return getRedisTaskQueue(name);
    }

    public static RedisQueue<RedisMessage> getRedisTaskQueue(String name){
		return queueMap.get(name);
    }
}
