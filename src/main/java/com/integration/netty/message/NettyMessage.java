package com.integration.netty.message;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;

public class NettyMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String sequence;//ΨһID
	private int packetType;//���ͱ��ģ�0����Ӧ���ģ�1
	private String requestType;//��Ӧ����
	private String responseType;//��Ӧ����
	private long reportTime; //����ʱ��
	private InetSocketAddress destination;
	private String content;
	private AtomicInteger count = new AtomicInteger(0); //���ʹ���
	
	
	
	public AtomicInteger getCount() {
		return count;
	}
	public void setCount(AtomicInteger count) {
		this.count = count;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public int getPacketType() {
		return packetType;
	}
	public void setPacketType(int packetType) {
		this.packetType = packetType;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getResponseType() {
		return responseType;
	}
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
	public long getReportTime() {
		return reportTime;
	}
	public void setReportTime(long reportTime) {
		this.reportTime = reportTime;
	}
	public InetSocketAddress getDestination() {
		return destination;
	}
	public void setDestination(InetSocketAddress destination) {
		this.destination = destination;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public static NettyMessage buildRequest(Object obj){
		NettyMessage request = new NettyMessage();
		request.setRequestType(obj.getClass().getName());
		request.setContent(JSON.toJSONString(obj));
		return request;
	}
	
	public static String buildResponse(String sequence){
		return "{\"sequence\":\""+sequence+"\",\"reportTime\":"+System.currentTimeMillis()+",\"packetType\":1}";
	}
	
}
