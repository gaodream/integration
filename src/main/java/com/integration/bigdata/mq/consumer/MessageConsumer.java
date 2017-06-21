package com.integration.bigdata.mq.consumer;

public class MessageConsumer {

     public void receive(MQMessage message) {
         //如果消费到了就会打印出来
    	 String title = message.isSync()?"同步":"异步";
         System.err.println("第"+message.getOrder()+"条消息，"
        		 +title+"==="+message.getpMerCode()+"======"+message.getpErrCode());
         try {
			Thread.sleep(50000);
			System.err.println("sleep");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("sleep");
		}
     }
     
     public void receive1(MQMessage message) {
         //如果消费到了就会打印出来
    	 String title = message.isSync()?"同步":"异步";
         System.err.println("第"+message.getOrder()+"条消息，"
        		 +title+"==="+message.getpMerCode()+"====="+message.getpErrCode());
         try {
			Thread.sleep(50000);
			System.err.println("sleep");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("sleep");
		}
     }
}
