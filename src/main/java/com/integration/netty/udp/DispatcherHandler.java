package com.integration.netty.udp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.integration.netty.message.CacheManager;
import com.integration.netty.message.NettyMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class DispatcherHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	
	protected final static Logger LOG = LoggerFactory.getLogger(DispatcherHandler.class);
	
	private Map<String,DefaultHandler<?>> handlers = new ConcurrentHashMap<String,DefaultHandler<?>>();
	
	public DispatcherHandler register(Class<?> cls, DefaultHandler<?> handler) {
		handlers.put(cls.getName(), handler);
		return this;
	}
	
	@Override
	public boolean acceptInboundMessage(Object msg) throws Exception {
		//LOG.info("【acceptInboundMessage】");
		return super.acceptInboundMessage(msg);
	}

	
	@Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
		//LOG.info("【channelReadComplete】");
        ctx.flush();
   }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	//LOG.info("【exceptionCaught】");
        cause.printStackTrace();
   }


	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		//LOG.info("【channelRead】");
		super.channelRead(ctx, msg);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, DatagramPacket packet) throws Exception {
		ByteBuf buf =  context.alloc().directBuffer().writeBytes(packet.content());
        System.out.println("收到来自["+packet.sender().getHostName()+":"+packet.sender().getPort()+"]客户端的数据.");
        //性能测试表明，采用内存池的ByteBuf相比于朝生夕灭的ByteBuf，性能高23倍左右（性能数据与使用场景强相关）。
         //ByteBuf buf =  Unpooled.copiedBuffer(packet.content());
         byte[] req = new byte[buf.readableBytes()];
         buf.readBytes(req);
         String body = new String(req, CharsetUtil.UTF_8);
         buf.release();
           NettyMessage message = JSON.parseObject(body, NettyMessage.class);
         //消息确认包
         if(message.getPacketType()==1){
        	 CacheManager.remove(message.getSequence());
         }else{
			 //消息发送包------转发时转换成具体的类型类型
        	 if(handlers.containsKey(message.getRequestType())){
        		 handlers.get(message.getRequestType()).preHandle(message.getContent(),packet.sender());
        	 }else{
        		LOG.info("DispatcherHandler can not find appropriate handler.");
        	 }
        	 
        	//发送确认消息
			try {
				context.writeAndFlush(
						new DatagramPacket(Unpooled.copiedBuffer(NettyMessage.buildResponse(message.getSequence()), CharsetUtil.UTF_8),packet.sender())
					).sync();
			} catch (InterruptedException e) {
				LOG.error("Send  response error.");
					e.printStackTrace();
			}
				
		}
		
	}

	
    
    
}
