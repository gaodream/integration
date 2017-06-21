package com.integration.bigdata.mq.consumer;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

import com.integration.bigdata.mq.producer.ObjectMessageConverter;

public class ConsumerManager {

	public Logger logger = LogManager.getLogger(ConsumerManager.class);
	public final static String QUEUE_PREFIX = "YH.QUEUE.";
	private PooledConnectionFactory connectionPool;
	private Connection connection = null;
	private Session session = null;
	
	public void init() {
		try {
			connection = connectionPool.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); //如果true,消费完后，队列不清空
		} catch (JMSException e) {
			e.printStackTrace();
		}
		System.err.println(".......ConsumerManager start........");
	}
	
	public void startConsumer(String queueName) throws JMSException{
		System.err.println("....consumer["+queueName+"]...ConsumerManager start........");
		MessageListenerAdapter adapter =  new MessageListenerAdapter(new CommonConsumer());
		
		adapter.setDefaultListenerMethod(queueName);//queueName 和消费者的方法默认一致
		adapter.setMessageConverter(new ObjectMessageConverter());
		
		MessageConsumer consumer = session.createConsumer(new ActiveMQQueue(QUEUE_PREFIX + queueName.toUpperCase()));
		
		consumer.setMessageListener(adapter);
	}

	public PooledConnectionFactory getConnectionPool() {
		return connectionPool;
	}

	public void setConnectionPool(PooledConnectionFactory connectionPool) {
		this.connectionPool = connectionPool;
	}
	
	
	
}
