package com.integration.bigdata.mq.producer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import com.integration.bigdata.mq.consumer.MQMessage;

public class ObjectMessageConverter  implements MessageConverter{

	public Logger logger = LogManager.getLogger(ObjectMessageConverter.class);
	@Override
	public MQMessage fromMessage(Message message) throws JMSException, MessageConversionException {
		MQMessage ipsMessage  = null ;
         if (message instanceof ObjectMessage) {
           ObjectMessage oMsg = (ObjectMessage)message;
          if (oMsg instanceof ActiveMQObjectMessage) {
        	  ActiveMQObjectMessage aMsg = (ActiveMQObjectMessage)oMsg;
        	  try {
        		  ipsMessage = (MQMessage)aMsg.getObject();
        	  } catch (Exception e) {
        		  logger.error("Message:${} is not a instance of NoticeInfo."+message.toString());
        	  }
          }
         }
		return ipsMessage;
	}

	@Override
	public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
		ActiveMQObjectMessage msg = (ActiveMQObjectMessage)session.createObjectMessage();
		msg.setObject((MQMessage)object);
		return msg;
	}

}
