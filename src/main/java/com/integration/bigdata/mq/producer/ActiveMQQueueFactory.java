package com.integration.bigdata.mq.producer;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.integration.bigdata.mq.consumer.ConsumerManager;


@Component("queueFactory")
public class ActiveMQQueueFactory {

	public Logger logger = LogManager.getLogger(ActiveMQQueueFactory.class);
	public final static String QUEUE_PREFIX = "YH.QUEUE.";
	private static Map<String,Destination> destinationMap = new HashMap<String,Destination>();
	
	@Autowired
	private ConsumerManager consumerManager;
	
	@Autowired
	private PooledConnectionFactory connectionPool;
	private Connection connection;
	private Session session;
	private JmsTemplate jmsTemplate;
	
	
	
	public JmsTemplate getJmsTemplate(String queueName){
		try {
			connection = connectionPool.createConnection();
			session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			//String queueAllName = QUEUE_PREFIX + queueName.toUpperCase();
			String queueAllName = QUEUE_PREFIX + queueName;
			Destination destination = null;
			if(null != destinationMap.get(queueAllName)){
				destination = destinationMap.get(queueAllName);
			}else{
				destination = session.createQueue(queueAllName);
				destinationMap.put(queueAllName, destination);
				consumerManager.startConsumer(queueName);//启动一个监听程序（即消费者）
			}
			jmsTemplate = new JmsTemplate();
			jmsTemplate.setConnectionFactory(connectionPool);
			jmsTemplate.setDefaultDestination(destination);
			jmsTemplate.setMessageConverter(new ObjectMessageConverter());
			jmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
			jmsTemplate.setDeliveryPersistent(true);
		} catch (JMSException e) {
			logger.error("获取队列链接异常。");
			e.printStackTrace();
		}finally {
			 try {
				session.close();
				connection.close(); 
			} catch (JMSException e) {
				e.printStackTrace();
			} 
		}
		return jmsTemplate;
	}
	
    public static String jmxDomain = "jms-broker";
    public static int port = 11099 ;
    public static String connectorPath = "/jmxrmi";
    private static String jmxUrl = "service:jmx:rmi:///jndi/rmi://192.168.1.124:"+ port + connectorPath;
	@SuppressWarnings("null")
	public static void initComsumer() throws Exception{
	    ConsumerManager consumerManager = null;//(ConsumerManager)SpringContext.getBean("consumerManager");
	    JMXServiceURL url = new JMXServiceURL(jmxUrl);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("activemq.password", "manager");
        map.put("jmx.remote.credentials", "admin,activemq".split(","));
        JMXConnector connector = JMXConnectorFactory.connect(url, map);
        connector.connect();
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        /**
         * 这里的jms-broker必须和上面配置的名称相同
         * 注意：brokerName 和 type要首字母要小写,否则
         * javax.management.InstanceNotFoundException: jms-broker:BrokerName=localhost,type=Broker
         */
        ObjectName name = new ObjectName(jmxDomain+":brokerName=localhost,type=Broker");
        BrokerViewMBean mBean =  (BrokerViewMBean)MBeanServerInvocationHandler.newProxyInstance(connection,  
                name, BrokerViewMBean.class, true);
         
        for(ObjectName queueName : mBean.getQueues()) {
            QueueViewMBean queueMBean =  (QueueViewMBean)MBeanServerInvocationHandler
                        .newProxyInstance(connection, queueName, QueueViewMBean.class, true);
            String queueAllName = queueMBean.getName();
            if(!destinationMap.containsKey(queueAllName)){
            	destinationMap.put(queueAllName, new ActiveMQQueue(queueAllName));
            }
            String  method = queueAllName.substring(queueAllName.lastIndexOf(".")+1).toLowerCase();
            consumerManager.startConsumer(method);
        }
		
	}
	

    public static void main(String[] args) throws Exception {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://192.168.1.124:"+
                port + connectorPath);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("activemq.password", "manager");
        map.put("jmx.remote.credentials", "admin,activemq".split(","));
        JMXConnector connector = JMXConnectorFactory.connect(url, map);
        connector.connect();
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        System.out.println(connection.getDefaultDomain());
        for(int i=0;i<connection.getDomains().length;i++){
        	System.out.println(connection.getDomains()[i]);
        	
        }
        /**
         * 这里的jms-broker必须和上面配置的名称相同
         * 注意：brokerName 和 type要首字母要小写,否则
         * javax.management.InstanceNotFoundException: jms-broker:BrokerName=localhost,type=Broker
         */
        ObjectName name = new ObjectName(jmxDomain+":brokerName=localhost,type=Broker");
        BrokerViewMBean mBean =  (BrokerViewMBean)MBeanServerInvocationHandler.newProxyInstance(connection,  
                name, BrokerViewMBean.class, true);
         
        for(ObjectName queueName : mBean.getQueues()) {
            QueueViewMBean queueMBean =  (QueueViewMBean)MBeanServerInvocationHandler
                        .newProxyInstance(connection, queueName, QueueViewMBean.class, true);
            System.out.println("\n------------------------------\n");
            // 消息队列名称
            
            System.out.println("States for queue --- " + queueMBean.getName());
            String  method = queueMBean.getName().substring(queueMBean.getName().lastIndexOf(".")).toLowerCase();
            System.out.println(method);
            // 队列中剩余的消息数
            System.out.println("Size --- " + queueMBean.getQueueSize());
            // 消费者数
            System.out.println("Number of consumers --- " + queueMBean.getConsumerCount());
            // 出队数
            System.out.println("Number of dequeue ---" + queueMBean.getDequeueCount() );
        }
	}
	
}
