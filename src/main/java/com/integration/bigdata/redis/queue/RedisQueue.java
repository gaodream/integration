package com.integration.bigdata.redis.queue;

public interface  RedisQueue<RedisMessage> {

	
	 /**
	 * 获取队列名
	 * @return
	 */
    public String getQueueName();

    /**
     * 往队列中添加任务
     * @param task
     */
    public  void pushMessage(RedisMessage message);

    /**
     * 从队列中取出一个任务
     * @return
     */
    public  RedisMessage  popMessage();
    
   
}
