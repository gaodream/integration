package com.integration.bigdata.redis.message;

public class DefaultRedisMessage implements RedisMessage{

	private static final long serialVersionUID = 1L;
	
	private String messageId;
	private String title;
	private String content;
	
	
	public DefaultRedisMessage() {
		super();
	}
	
	public DefaultRedisMessage(String messageId, String title, String content) {
		super();
		this.messageId = messageId;
		this.title = title;
		this.content = content;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
    
}
