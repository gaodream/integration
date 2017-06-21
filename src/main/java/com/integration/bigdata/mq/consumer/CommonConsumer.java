package com.integration.bigdata.mq.consumer;

public class CommonConsumer {
	
	 public void receive(MQMessage message) {
         //如果消费到了就会打印出来
    	 String title = message.isSync()?"同步":"异步";
         System.err.println("receive第"+message.getOrder()+"条消息，"
        		 +title+"==="+message.getpMerCode()+"======"+message.getpErrCode());
     }
	 
   public void receive1(MQMessage message) {
         //如果消费到了就会打印出来
    	 String title = message.isSync()?"同步":"异步";
         System.err.println("receive1第"+message.getOrder()+"条消息，"
        		 +title+"==="+message.getpMerCode()+"======"+message.getpErrCode());
     }
}
