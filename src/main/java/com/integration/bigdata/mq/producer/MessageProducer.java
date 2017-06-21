package com.integration.bigdata.mq.producer;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.integration.bigdata.mq.consumer.MQMessage;


@Service("messageProducer")
public class MessageProducer {
	
	public Logger logger = LogManager.getLogger(MessageProducer.class);
	
	private final static String DEFAULT_QUEUE = "DEFAULT"; 
	
	ActiveMQQueue defaultDestination = new ActiveMQQueue(DEFAULT_QUEUE);
	
	@Autowired
	private ActiveMQQueueFactory queueFactory;
	
    //原子整型计数（CAS），可以不使用同步
    //private AtomicInteger current = new AtomicInteger(0);

    //发送消息
    public void sendMessage(String queueName,MQMessage message){
    	//jmsTemplate = queueFactory.getJmsTemplate(queueName);
    	//queueFactory.getJmsTemplate(queueName).convertAndSend(new ActiveMQQueue(queueName), message);
    	queueFactory.getJmsTemplate(queueName).convertAndSend( message);
    }
    //发送消息
    public void sendMessage(String queueName,String message){
    	//jmsTemplate = queueFactory.getJmsTemplate(queueName);
    	queueFactory.getJmsTemplate(queueName).convertAndSend(message);
    }
    //发送消息
    public void sendMessage(MQMessage message){
    	queueFactory.getJmsTemplate(DEFAULT_QUEUE).convertAndSend(message);
    }
    
    //发送消息
    public void sendMessage(String message){
    	queueFactory.getJmsTemplate(DEFAULT_QUEUE).convertAndSend( message);
    }

}
