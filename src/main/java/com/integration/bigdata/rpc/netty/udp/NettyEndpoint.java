package com.integration.bigdata.rpc.netty.udp;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.integration.bigdata.rpc.netty.ThreadFactoryUtil;
import com.integration.common.Pair;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;


/**
 * Created by 伟佳 on 2015/9/24.
 * 基于netty框架的UDServer，并提供了绑定ip和端口
 *
 * 鉴于UDP无连接，每一个实例都既可以作为服务端也可以作为服务端
 *
 * 如果启用了消息监控超时重发，为了保证不会OOM，当前最多仅能同时缓存100W条消息，默认写死的数值，暂时未开放配置
 */
public class NettyEndpoint implements Endpoint {

    private static final Logger LOG = LoggerFactory.getLogger(NettyEndpoint.class);
    
    // TODO 待优化 最大重发次数
    private static final int MAX_RESEND_TIMES = 6;
    
    //读写锁，对monitorMap进行同步
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    //缓存的数据保留的时间
    private int timeOutSecs;
    
    // 监控数据报消息超时map，Map：key：业务标识（序号和索引）；value：pair<数据包，超时次数>
    private Map<String, Pair<MonitorDatagramPack, Long>> sendMsgCache = new LinkedHashMap<>();
    
    //Map<数据包地址，Map<数据包序列号，组装的数据包结构>>
    private Map<InetSocketAddress, Map<Long, AssemblePacket>> receiveMsgCache = new ConcurrentHashMap<>();
    
    // 序列号（从0开始递增）
    private AtomicLong seqNumber = new AtomicLong(0);
    
    // 分包:每次分包大小为1K: 头部[25]+内容[999]
    private static final int DEFAULT_PACKET_LENGTH = 999;
    
    /**
     * 数据包头部数据[25个byte]格式如下：
     * | 8 byte | 4 byte | 4 byte  | 4 bytes  | 4 bytes | 1 byte | 999 byte |
     * | 序列号        | 总包数         | 当前包序号   | 该包的偏移量  | 包总长度       | 确认消息    |  数据               |
     */
    private static final int HEADER_LENGTH = 25;
    private static final int SEQNUM_OFFSET = 0;
    private static final int TOTALNUM_OFFSET = 8;
    private static final int INDEX_OFFSET = 12;
    private static final int BEGIN_OFFSET = 16;
    private static final int TOTALLENGTH_OFFSET = 20;
    private static final int ACK_OFFSET = 24;
    private static final int LONG_DIST = 8;
    private static final int INT_DIST = 4;
    private static final int ACK_DIST = 1;
    private static final int REQUEST_VALUE = 0;
    private static final int RESPONSE_VALUE = 1;

    // netty 相关
    private String host;
    private int rpcPort;
    private ChannelHandler handler;
    private EventLoopGroup group;
    private Bootstrap bootStrap;
    private Channel channel;
    private ScheduledExecutorService threadPool = null;
    private int workerThreads = 0;
    
    /**
     * 缺省业务工作线程
     */
    private static final int DEFAULT_WORKER_THREADS;

    static {
        DEFAULT_WORKER_THREADS = Runtime.getRuntime().availableProcessors() * 2;

        LOG.info("DEFAULT_WORKER_THREADS is : {}", DEFAULT_WORKER_THREADS);
    }

    /**
     * 构造函数适配器
     * @param ip   绑定ip
     * @param port 绑定端口
     */
    public NettyEndpoint(String ip, int port, int timeOutSecs) {
        this.bootStrap = new Bootstrap();
        this.host = ip;
        this.rpcPort = port;
        this.timeOutSecs = timeOutSecs;
        threadPool = Executors.newScheduledThreadPool(workerThreads == 0 ? DEFAULT_WORKER_THREADS : workerThreads,ThreadFactoryUtil.create("Netty-%d"));
    }

    public NettyEndpoint(String ip, int port) {
        this(ip, port, 10);
    }

    /**
     * 初始化handler
     *
     * @param handler 处里者
     */
    public void registHandler(ChannelHandler handler) {
        this.handler = handler;
    }

    public void start()  {
        // 构造并绑定group（group构造可能抛出异常，不放在构造函数中）
        this.group = new NioEventLoopGroup();
        bootStrap.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true).handler(this.handler);
        // 绑定ip和端口号并获取通道，用于后续的读写操作
        try {
			channel = bootStrap.bind(host, rpcPort).sync().channel();
		} catch (InterruptedException e) {
			LOG.error("Netty Adpater start error {}",e.getMessage());
		}

        cleanMsgCache();
    }


    @Override
    public void sendData(byte[] data, InetSocketAddress address) {
        try {
			sendData(data, address, true);
		} catch (Exception e) {
			LOG.error("send message to {} when happen error.",address.getAddress());
		}
    }

    @Override
    public void sendData(byte[] data, InetSocketAddress address, boolean needMonitor) throws Exception {
        // 首先先对data进行分包，然后返回byte数组列表进行循环发送
        List<byte[]> list = disassemblePacket(data);
        for (int i = 0; i < list.size(); i++) {
            if (needMonitor) {
                // 从分包的消息中获取序列号和index拼接作为id，格式为 seq_index
                long seq = getLongFromBytes(list.get(i), SEQNUM_OFFSET, LONG_DIST);
                int index = getIntFromBytes(list.get(i), INDEX_OFFSET, INT_DIST);
                putDatagramPack(seq + "_" + index, list.get(i), address);
                //LOG.debug("has monitored, id is {}", seq + "_" + index);
            }
            LOG.debug("send content >>> {}", new String( Arrays.copyOfRange(list.get(i), 25, list.get(i).length), "UTF-8"));
            sendData(new DatagramPacket(Unpooled.wrappedBuffer(list.get(i)), address));
        }
    }

    /**
     * 发送消息
     *
     * @param packet 数据报
     * @throws InterruptedException
     */
    private void sendData(DatagramPacket packet) throws InterruptedException {
        channel.writeAndFlush(packet);
    }

    /**
     * 从接受数据包缓存中组装完整的数据
     * 确认包：删除本地的发送缓存
     * 接受包：
     * @param packet 包含部分数据的数据包
     * @return 组装完返回该数据包的byte数组，否则返回null
     */
    public byte[] assemblePacket(byte[] packet, InetSocketAddress sockAddress) throws Exception {
        //1.从报文中提取信息[确认标志、序号、索引]
        int ackValue = getIntFromBytes(packet, ACK_OFFSET, ACK_DIST);
        long seqNumber = getLongFromBytes(packet, SEQNUM_OFFSET, LONG_DIST);
        int index = getIntFromBytes(packet, INDEX_OFFSET, INT_DIST);
        
        //2.判断该包类型 确认包还是接收包,如果是确认包
        if (ackValue == RESPONSE_VALUE) {
            LOG.debug("receive ack msg, seq is {}, index is {}.", seqNumber, index);
            removeDatagramPack(seqNumber + "_" + index);
            //System.err.println("sendMsgCache="+sendMsgCache.size());
            return null;
        }
        
        int totalLength = getIntFromBytes(packet, TOTALLENGTH_OFFSET, INT_DIST);
        int offset = getIntFromBytes(packet, BEGIN_OFFSET, INT_DIST);
        int total = getIntFromBytes(packet, TOTALNUM_OFFSET, INT_DIST); 
    	//TODO ：待优化不需要吧整体报文发送回去，发送响应确认报文
    	sendResponse(packet, seqNumber, index, total, sockAddress);
        //3.报文的总长度小于一个报文的最大长度，立即可以组装出报文不需要放到接收缓存中
        AssemblePacket assemblePacket ;
        if(totalLength < DEFAULT_PACKET_LENGTH){
        	assemblePacket = new AssemblePacket(totalLength, total);
        	assemblePacket.copyData(packet, HEADER_LENGTH, packet.length - HEADER_LENGTH, index, offset);
        	return assemblePacket.getData();
        }
        
        //4.如果报文总长度大于一个报文的最大长度
        lock.writeLock().lockInterruptibly();
        Map<Long, AssemblePacket> map = receiveMsgCache.get(sockAddress);
        if (map == null) {
            map = new ConcurrentHashMap<>();
            receiveMsgCache.put(sockAddress, map);
        }
        
        assemblePacket = map.get(seqNumber);
        //5.说明是一个包的第一个分包
        if (assemblePacket == null) {
            assemblePacket = new AssemblePacket(totalLength, total);
            map.put(seqNumber, assemblePacket);
        }
        
        //6.提取包的详细信息 提取包进行组包
        assemblePacket.copyData(packet, HEADER_LENGTH, packet.length - HEADER_LENGTH, index, offset);
        
        //7.在判断该包在缓存中是否已经可以组成完整包
        if (assemblePacket.getCount() == total) {
            //byte[] retBytes = new byte[assemblePacket.getData().length];
            //System.arraycopy(assemblePacket.getData(), 0, retBytes, 0, assemblePacket.getData().length);
            // 从map中删除对应的序号
            map.remove(seqNumber);
            // 回复一个响应确认消息
            // return retBytes;
            return assemblePacket.getData();
        }

        LOG.trace("this packet seqNumber is {}, index is {}, total packet num is " + total, seqNumber, index);
        return null;
    }

    /**
     * 将超过1k的数据包进行拆分
     * @param data 要分解的数据
     * @return 返回分包且序列化后的byte数组列表
     */
    public List<byte[]> disassemblePacket(byte[] data) throws Exception {
        long seqNumber = this.seqNumber.getAndIncrement();
        if (data.length <= DEFAULT_PACKET_LENGTH) {
            byte[] packet = fillPacket(data, 0, 1, 1, data.length, data.length, seqNumber, REQUEST_VALUE);
            return Arrays.asList(packet);
        } else {
            // 当数据包的长度可以分解为多个数据包时
            List<byte[]> retList = new ArrayList<>();
            // 获取最后一个数据包的长度，以及需要循环的次数（整除和不整除两种情况）
            int lastPackLength = data.length % DEFAULT_PACKET_LENGTH;
            // 考虑临界值情况，如果刚好数据包的长度整除一个分包的长度，则需做如下检查
            lastPackLength = lastPackLength == 0 ? DEFAULT_PACKET_LENGTH : lastPackLength;
            int loop = (data.length + DEFAULT_PACKET_LENGTH - 1) / DEFAULT_PACKET_LENGTH;
            for (int i = 0; i < loop; i++) {
                // 循环填充数据包，返回后直接添加到list中
                if (i == loop - 1) {
                    retList.add(fillPacket(data, i * DEFAULT_PACKET_LENGTH, loop, i + 1, data.length, lastPackLength, seqNumber, REQUEST_VALUE));
                } else {
                    retList.add(fillPacket(data, i * DEFAULT_PACKET_LENGTH, loop, i + 1, data.length, DEFAULT_PACKET_LENGTH, seqNumber, REQUEST_VALUE));
                }
            }
            return retList;
        }
    }

    
    /**
     * 发送响应消息，根据序号进行响应
     *
     * @param data        数据
     * @param sockAddress 目标地址
     * @throws Exception
     */
    public void sendResponse(byte[] data, long seqNum, int index, int total, InetSocketAddress sockAddress) throws Exception {
        // 此处只要确保packetLength为0，就不会有拷贝操作
        byte[] respPacket = fillPacket(data, HEADER_LENGTH, total, index, 0, 0, seqNum, RESPONSE_VALUE);
        // 使用内存池的bytebuf，每次senddata后自动释放
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(respPacket.length);
        byteBuf.writeBytes(respPacket);
        sendData(new DatagramPacket(byteBuf, sockAddress));
    }

    
    /**
     * 清除本地缓存
     * @throws InterruptedException 
     */
    private void cleanMsgCache() {
    	threadPool.scheduleAtFixedRate(()->{
    		Iterator<Entry<String, Pair<MonitorDatagramPack, Long>>> iter = null ;
    		try {
				lock.readLock().lockInterruptibly();
				iter = sendMsgCache.entrySet().iterator();
			} catch (Exception e) {
				LOG.error("");
			}finally {
				lock.readLock().unlock();
			}
    		
    	    while (iter.hasNext()) {
               Entry<String, Pair<MonitorDatagramPack, Long>> entry = iter.next();
               int timeOut = entry.getValue().getFirst().getTimeOut();
               //超过指定时间未接收到响应并未从发送缓存中移除的报文
               if (timeOut >= this.timeOutSecs) {
            	   String id = entry.getKey();
            	   InetSocketAddress address = entry.getValue().getFirst().getAddress();
                    try {
                    	byte[] data = entry.getValue().getFirst().getData();
                    	//LOG.debug("send content >>> {}", new String( Arrays.copyOfRange(data, 25, data.length), "UTF-8"));
                	    sendData(new DatagramPacket(Unpooled.wrappedBuffer(data), address));
					} catch (Exception e) {
					   LOG.error("send messsage[id={}] error  for TimeOut:{}",id,e.getMessage());
					}
                   //entry.getValue().getFirst().setTimeOut(1);
                   //如果当重发次数已经大于最大重发次数时，则打印日志删除消息
                   if (entry.getValue().getSecond() >= MAX_RESEND_TIMES) {
                       iter.remove();
                       //System.err.println("sendMsgCache.size="+sendMsgCache.size());
                   } else {
                       entry.getValue().setSecond(entry.getValue().getSecond() + 1);
                   }
               } else {
                   entry.getValue().getFirst().setTimeOut(timeOut + 1);
               }
           }
    		
    	}, 0, 10, TimeUnit.SECONDS);
    	
    }
    
    /**
     * 向monitor中放如数据报
     *
     * @param id       数据报的id
     * @param data     字节数组
     * @param sockAddr 目的socket地址
     */
    public void putDatagramPack(String id, byte[] data, InetSocketAddress address) {
        try {
            lock.writeLock().lockInterruptibly();
            Pair<MonitorDatagramPack, Long> pair = new Pair<MonitorDatagramPack, Long>(new MonitorDatagramPack(data, address, 1),0L);
            sendMsgCache.put(id, pair);
            //该数据包的引用增加，防止被释放
            //packet.retain();
        } catch (InterruptedException e) {
            LOG.error("{}", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 移除缓存的数据包
     *
     * @param id 消息id
     */
    public void removeDatagramPack(String id) {
        try {
            lock.writeLock().lockInterruptibly();
            sendMsgCache.remove(id);
        } catch (InterruptedException e) {
            LOG.error("{}", e);
        } finally {
            lock.writeLock().unlock();
        }
    }
    

    /**
     * 设置业务工作者线程数目
     * 该参数设置仅有启动工作线程模式处理业务时才会生效
     *
     * @param workerThreads 工作线程数目
     */
    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }
    

    /**
     * 填充数据包 用常量代替数字
     *
     * @param data         数据
     * @param offset       该包的偏移量
     * @param total        分解的包总数
     * @param index        当前包数的索引（从1开始）
     * @param totalLength  整个数据包的总长度
     * @param packetLength 该分包的长度
     * @param seqNumber    序号
     * @param ackVal       ack位值，0代表发送数据包，1代表确认数据包
     * @return 返回填充好的数据包
     * @throws Exception
     */
    private byte[] fillPacket(byte[] data, int offset, int total, int index, int totalLength, int packetLength, long seqNumber, int ackVal) throws Exception {
        final byte[] packet = new byte[HEADER_LENGTH + packetLength];
        // 写入序列号
        putLongToBytes(packet, seqNumber, SEQNUM_OFFSET, LONG_DIST);
        // 写入总的包数目
        putIntToBytes(packet, total, TOTALNUM_OFFSET, INT_DIST);
        // 写入当前包序号，注意序号从1开始
        putIntToBytes(packet, index, INDEX_OFFSET, INT_DIST);
        // 写入该包的偏移量
        putIntToBytes(packet, offset, BEGIN_OFFSET, INT_DIST);
        // 写入包的总长度
        putIntToBytes(packet, totalLength, TOTALLENGTH_OFFSET, INT_DIST);
        // 写入当前数据包为发送包，需要进行确认，发送包为0，确认包为1
        putIntToBytes(packet, ackVal, ACK_OFFSET, ACK_DIST);
        // 写入该包的数据
        if (packetLength != 0) {
            System.arraycopy(data, offset, packet, HEADER_LENGTH, packetLength);
        }
        return packet;
    }

    /**
     * 将一个long值写入byte数组
     *
     * @param data  byte数组
     * @param value 要写入的long值
     * @param index 从哪个位置开始写入
     * @param dist  表示从index位置开始截取字节的距离
     * @throws Exception 如果byte数组的剩余空间不足以写入时抛出参数违法异常
     */
    private void putLongToBytes(byte[] data, long value, int index, int dist) throws Exception {
        if (data.length - index < dist) {
            throw new IllegalArgumentException("index is error, data has no enough space for put long");
        }
        // 将long值的最低位保存在数组的最低位
        for (int i = index; i < index + dist; i++) {
            data[i] |= (value & 0xff);
            // 每次右移8位
            value >>= 8;
        }
    }

    /**
     * 从byte数组中获取一个long值
     *
     * @param data  byte数组
     * @param index 从哪个位置开始获取
     * @param dist  表示从index位置开始截取字节的距离
     * @return 返回一个long值
     * @throws Exception 如果byte数组的剩余空间不足以写入时抛出参数违法异常
     */
    private long getLongFromBytes(byte[] data, int index, int dist) throws Exception {
        if (data.length - index < dist) {
            throw new IllegalArgumentException("index is error, data has no enough space for put long");
        }

        long value = 0;
        // 从byte数组的最高位开始取值，最后拼成long
        for (int i = index + dist - 1; i >= index; i--) {
            // 每次左移8位
            value <<= 8;
            value |= (data[i] & 0xff);
        }
        return value;
    }

    /**
     * 将一个int值写入byte数组
     *
     * @param data  byte数组
     * @param value 要写入的int值
     * @param index 从哪个位置开始写入
     * @param dist  表示从index位置开始截取字节的距离
     * @throws Exception 如果byte数组的剩余空间不足以写入时抛出参数违法异常
     */
    private void putIntToBytes(byte[] data, int value, int index, int dist) throws Exception {
        if (data.length - index < dist) {
            throw new IllegalArgumentException("index is error, data has no enough space for put int");
        }

        // 将int值的最低位保存在数组的最低位
        for (int i = index; i < index + dist; i++) {
            data[i] |= (value & 0xff);
            // 每次右移8位
            value >>= 8;
        }
    }

    /**
     * 从byte数组中获取一个int值
     *
     * @param data  byte数组
     * @param index 从哪个位置开始获取
     * @param dist  表示从index位置开始截取字节的距离
     * @return 返回一个int值
     * @throws Exception 如果byte数组的剩余空间不足以写入时抛出参数违法异常
     */
    private int getIntFromBytes(byte[] data, int index, int dist) throws Exception {
        if (data.length - index < dist) {
            throw new IllegalArgumentException("index is error, data has no enough space for put int");
        }
        int value = 0;
        // 从byte数组的最高位开始取值，最后拼成long
        for (int i = index + dist - 1; i >= index; i--) {
            // 每次左移8位
            value <<= 8;
            value |= (data[i] & 0xff);
        }
        return value;
    }


    /**
     * 关闭相关的资源
     */
    public void stop() {
        if (channel != null) {
            // 关闭channel
            channel.close();
        }
        if (group != null) {
            // 优雅地关闭group
            group.shutdownGracefully();
        }
    }
    

    /**
     * 监控数据报结构
     */
    class MonitorDatagramPack {
        private byte[] data;
        private InetSocketAddress address;
        private int timeOut;

        public MonitorDatagramPack(byte[] data, 
        		InetSocketAddress address,
                int timeOut) {
            super();
            this.data = data;
            this.address = address;
            this.timeOut = timeOut;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

		public InetSocketAddress getAddress() {
			return address;
		}

		public void setAddress(InetSocketAddress address) {
			this.address = address;
		}

		public int getTimeOut() {
			return timeOut;
		}

		public void setTimeOut(int timeOut) {
			this.timeOut = timeOut;
		}
        
    }


}
