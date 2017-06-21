package com.integration.bigdata.mq.test;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.junit.After;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

import com.integration.bigdata.mq.consumer.CommonConsumer;

public class TestMQ {

	private  ApplicationContext context ;
	
	//private  MessageProducer producer;
	private PooledConnectionFactory connectionPool;
	public final static String QUEUE_PREFIX = "YH.QUEUE.";
	{
		context = new ClassPathXmlApplicationContext("classpath:applications.xml");
		//producer = context.getBean(MessageProducer.class);
		connectionPool = context.getBean(PooledConnectionFactory.class);
	}
	
	@Test
	public void testProducer() throws Exception{
		String queueName = "receive1";
		Connection connection = connectionPool.createConnection();
		
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); 
		Destination destination = session.createQueue(QUEUE_PREFIX + queueName.toUpperCase());
		
		System.err.println("==="+queueName);
		MessageListenerAdapter adapter = new MessageListenerAdapter(CommonConsumer.class);
		adapter.setDefaultListenerMethod(queueName);//queueName 和消费者的方法默认一致
		//adapter.setMessageConverter(new ObjectMessageConverter());
		
		MessageConsumer consumer = session.createConsumer(destination);
		
		consumer.setMessageListener(adapter);
		
		
		connection.start();
	}
	
	@Test
	public void testConsumer(){
		//producer.sendMessage("测试数据");
	}
	
	@After
	public void close(){
	}
	
	public static void main(String[] args) {
		/*ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applications.xml");
		
		MessageProducer producer = context.getBean(MessageProducer.class);

		producer.sendMessage("测试数据");*/
	}
}
