package com.integration.netty.tcp;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

@Component
public class NettyServer implements ApplicationContextAware{
	
	private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    
    @Value("${rpcServer.ioThreadNum:5}")
    private int ioThreadNum;
    
    @Value("${rpcServer.backlog:1024}")
    private int backlog;
    
    @Value("${rpcServer.host:127.0.0.1}")
    private String host;
    
    @Value("${rpcServer.port:9090}")
    private int port;


    @PostConstruct
    public void start() throws InterruptedException {
    	log.info("begin to start rpc server");
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup(ioThreadNum);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, backlog)
                //注意是childOption
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                //真实数据最大字节数为Integer.MAX_VALUE，解码时自动去掉前面四个字节
                                //io.netty.handler.codec.DecoderException: java.lang.IndexOutOfBoundsException: readerIndex(900) + length(176) exceeds writerIndex(1024): UnpooledUnsafeDirectByteBuf(ridx: 900, widx: 1024, cap: 1024)
                                .addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                .addLast("encoder", new LengthFieldPrepender(4, false))
                                //.addLast(new RpcDecoder(RpcRequest.class))
                                //.addLast(new RpcEncoder(RpcResponse.class))
                                //.addLast(new ServerRpcHandler(exportServiceMap))
                                ;
                    }
                });

        channel = serverBootstrap.bind(host,port).sync().channel();

        log.info("NettyRPC server listening on port " + port + " and ready for connections...");
    }

    @PreDestroy
    
    public void stop() {
    	log.info("destroy server resources");
        if (null == channel) {
        	log.error("server channel is null");
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
        bossGroup = null;
        workerGroup = null;
        channel = null;
    }

    /**
     * 利用此方法获取spring ioc接管的所有bean
     * @param ctx
     * @throws BeansException
     */
	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		//Map<String, Object> controllers = ctx.getBeansWithAnnotation(Controller.class);
    /*    log.info("获取到所有的RPC服务:{}", serviceMap);
        if (serviceMap != null && serviceMap.size() > 0) {
            for (Object serviceBean : serviceMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(ServiceExporter.class)
                        .targetInterface()
                        .getName();
                logger.info("register service mapping:{}",interfaceName);
                exportServiceMap.put(interfaceName, serviceBean);
            }
        }
        
        
            //获取所有的RequestMapping
        Map<String, HandlerMapping> allRequestMappings = BeanFactoryUtils.
        beansOfTypeIncludingAncestors(appContext, HandlerMapping.class, true, false);
        *
        */
	}

}
