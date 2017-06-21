package com.integration.bigdata.mq.consumer;

import org.springframework.jms.listener.adapter.MessageListenerAdapter;

public class CustomMessageListenerAdapter extends MessageListenerAdapter {

	public CustomMessageListenerAdapter(CommonConsumer commonConsumer) {
		super(commonConsumer);
	}
	
	
	
	

}
