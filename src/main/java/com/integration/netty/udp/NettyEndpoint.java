package com.integration.netty.udp;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.integration.bigdata.rpc.netty.ThreadFactoryUtil;
import com.integration.netty.message.CacheManager;
import com.integration.netty.message.NettyMessage;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SystemPropertyUtil;

public class NettyEndpoint {
	
	private final static Logger LOG = LoggerFactory.getLogger(NettyEndpoint.class);
	
	private final static Integer TRY_TIME = 3;//閲嶅彂娆℃暟
    private ChannelHandler dispatcher = new DispatcherHandler();
    private Timer monitor ;
    private EventLoopGroup workerGroup ;
    private Bootstrap bootStrap;
    private Channel channel;
    private int timeInterval; //閲嶅彂鏃堕棿闂撮殧
    private int port;
    
    
    public NettyEndpoint(String host,int port,int timeInterval) {
        this.bootStrap = new Bootstrap();
        this.timeInterval = timeInterval;
        this.port = port;
    }
    
    public void bindHandler(ChannelHandler handler) {
        this.dispatcher = handler;
    }
    
    /**
     * https://my.oschina.net/miffa/blog/390931
     * SO_REUSEPORT使用条件：
     * 		JDK 1.8
     * 		Linux内核(>= 3.9)支持SO_REUSEPORT特性
     * 		Netty 4.0.19以及之后版本才真正提供了JNI方式单独包装的epoll native transport版本（在Linux系统下运行）
     * EpollChannelOption ：
     * 		扩展 socket option，增加 SO_REUSEPORT 选项，用来设置 reuseport
     * 		修改 bind 系统调用实现，以便支持可以绑定到相同的 IP 和端口
     * 		修改处理新建连接的实现，查找 listener 的时候，能够支持在监听相同 IP 和端口的多个 sock 之间均衡选择
     * 
     * SO_REUSEPORT支持多个进程或者线程绑定到同一端口，提高服务器程序的性能，解决的问题：
     * 		1.允许多个套接字 bind()/listen() 同一个TCP/UDP端口
     * 			每一个线程拥有自己的服务器套接字
     * 			在服务器套接字上没有了锁的竞争
     * 		2.内核层面实现负载均衡
     * 		3.安全层面，监听同一个端口的套接字只能位于同一个用户下面
     * 注：bindp是不借助Netty的另一种实现
     * @return
     */
	public NettyEndpoint start() {
		int totalThreads = SystemPropertyUtil.getInt("io.netty.eventLoopThreads",Runtime.getRuntime().availableProcessors());
		bootStrap = new Bootstrap(); 
	/*	if( System.getProperty("os.name").toUpperCase().indexOf("LINUX")>=0){
			this.workerGroup = new EpollEventLoopGroup(totalThreads,ThreadFactoryUtil.create("netty-%d"));//浠呭湪linux涓嬫敮鎸�
		}else{
		}*/
		this.workerGroup = new NioEventLoopGroup(totalThreads,ThreadFactoryUtil.create("netty-%d"));
    	if(null == dispatcher){
    		dispatcher = new DispatcherHandler();
    	}
    	try {
			ChannelFuture future = bootStrap.group(workerGroup)
				.channel(NioDatagramChannel.class)
				//.option(ChannelOption.SO_BROADCAST, true)
				//.option(ChannelOption.SO_REUSEADDR, true)
				.option(EpollChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(32 * 1024, 128 * 1024))
				.option(EpollChannelOption.SO_REUSEPORT, true)
				.option(EpollChannelOption.SO_SNDBUF, 128 *1024)
				.option(EpollChannelOption.SO_RCVBUF,256 *1024)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) 
				.option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(2,1024*1024,1024*1024*500))
				.handler(dispatcher)
/*				.handler(new ChannelInitializer<EpollDatagramChannel>() { 
					@Override
					public void initChannel(EpollDatagramChannel ch) throws Exception {
						ch.pipeline().addLast(new KryoDecoder());
						ch.pipeline().addLast(new KryoEncoder());
						ch.pipeline().addLast(dispatcher);
					}
				})
*/				.bind(port).sync();
    		channel = future.channel();
    		//future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			LOG.error("NettyRpcEndpoint start error. {}",e.getMessage());
		} 
    	
    	this.monitor = new Timer();
    	monitor.schedule(new MessageRetryMonitor(timeInterval), 0, timeInterval);
    	return this;
    }
    
	
	public void send(Object packet) {
		try {
			channel.writeAndFlush(packet).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void send(DatagramPacket packet) {
		try {
			channel.writeAndFlush(packet).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
    
    public void send(String content, InetSocketAddress dest){
		channel.writeAndFlush(new DatagramPacket(
				Unpooled.wrappedBuffer(content.getBytes(CharsetUtil.UTF_8)), 
				dest));
    }
	
	public void send(NettyMessage msg, InetSocketAddress dest){
		try {
			String content = JSON.toJSONString(msg);
			if(channel.isWritable()){
				ChannelFuture future = channel.writeAndFlush(new DatagramPacket(
						Unpooled.wrappedBuffer(content.getBytes(CharsetUtil.UTF_8)), 
						dest)).sync();
				future.addListener((ChannelFuture future1)->{
					if(future1.isSuccess()){
						//System.err.println(future1.channel()+"-"+msg.getSequence()+" send SUCESS!");
					}else{
						System.err.println("ERROR");
					}
				});
			}
		} catch (InterruptedException e) {
			LOG.error("Send msg to {} error . ",dest.getHostString());
		}
	}
	
	public void send(NettyMessage msg, InetSocketAddress dest,boolean isRetry){
		try {
			if(isRetry){
				msg.setDestination(dest);
				CacheManager.put(UUID.randomUUID().toString(),msg);
			}
			String content = JSON.toJSONString(msg);
			
			channel.writeAndFlush(new DatagramPacket(
					Unpooled.wrappedBuffer(content.getBytes(CharsetUtil.UTF_8)), 
					dest)).sync();
		} catch (InterruptedException e) {
			LOG.error("Send msg to {} error . ",dest.getHostString());
		}
	}
    

	public void stop()  {
    	if (channel != null) {
             channel.close();
        }
        if (workerGroup != null) {
     	   workerGroup.shutdownGracefully();
        }
    }

	class MessageRetryMonitor extends TimerTask {
		
		private int timeInterval = 1000;
		
		public MessageRetryMonitor(){}
		
		public MessageRetryMonitor(int timeInterval){
			this.timeInterval = timeInterval;
		}
		
		@Override
		public void run() {
			Map<String, NettyMessage> cache = CacheManager.getCache();
			Iterator<String> itor = cache.keySet().iterator();
			while(itor.hasNext()){
				String key = itor.next();
				NettyMessage msg = cache.get(key);
				//防止发送时，已到达重发的时间节点
				if(System.currentTimeMillis() - msg.getReportTime() > timeInterval){
					send(msg, msg.getDestination());
					int count = msg.getCount().incrementAndGet();
					if(count >= TRY_TIME ){
						cache.remove(key);
					}
				}
			}
		}
		
	}

	
    
}
