package com.integration.bigdata.redis.test;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.Pool;

public class RedisManager {

	
	private final static int POOL_MAX_TOTAL = 5000;
	private final static int POOL_MAX_IDLE = 256;
	private final static long POOL_MAX_WAIT_MILLIS = 5000L;
	private final static String CONNECT_MODE = "single";//single sentinel
	private final static String HOST_POSTS = "192.168.1.124:6379";
	private static Pool<Jedis> pool;

    protected final static Logger logger = Logger.getLogger(RedisManager.class);

    static{
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init() throws Exception {

        // 创建jedis池配置实例
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 设置池配置项值
        jedisPoolConfig.setMaxTotal(5000);

        jedisPoolConfig.setMaxIdle(256);

        jedisPoolConfig.setMaxWaitMillis(5000L);

        logger.debug(String.format("poolMaxTotal: %s , poolMaxIdle : %s , poolMaxWaitMillis : %s ",
        		POOL_MAX_TOTAL,POOL_MAX_IDLE,POOL_MAX_WAIT_MILLIS));

        String[] hostPortSet = HOST_POSTS.split(","); 
        if("single".equals(CONNECT_MODE)){
            String[] hostPort = hostPortSet[0].split(":");
            pool = new JedisPool(jedisPoolConfig, hostPort[0], Integer.valueOf(hostPort[1].trim()));
        }else if("sentinel".equals(CONNECT_MODE)){
            Set<String> sentinels = new HashSet<String>();     
            for(String hostPort : hostPortSet){
                sentinels.add(hostPort);
            }
            pool = new JedisSentinelPool("mymaster", sentinels, jedisPoolConfig);
        }
    }

    /**
     * 使用完成后，必须调用 returnResource 还回。
     * @return 获取Jedis对象
     */
    public static Jedis getResource(){
        Jedis jedis = pool.getResource();
        if(logger.isDebugEnabled()){
            logger.debug("获得链接：" + jedis);
        }
        return jedis;
    }

    /**
     * 获取Jedis对象。
     * 
     * 用完后，需要调用returnResource放回连接池。
     * 
     * @param db 数据库序号
     * @return
     */
    public static Jedis getResource(int db){
        Jedis jedis = pool.getResource();
        jedis.select(db);
        if(logger.isDebugEnabled()){
            logger.debug("获得链接：" + jedis);
        }
        return jedis;
    }

    /**
     * @param jedis
     */
    @SuppressWarnings("deprecation")
	public static void returnResource(Jedis jedis){
        if(jedis!=null){
            pool.returnResource(jedis);
            if(logger.isDebugEnabled()){
                logger.debug("放回链接：" + jedis);
            }
        }
    }

    /**
     * 需要通过Spring确认这个方法被调用。
     * @throws Exception
     */
    public static void destroy() throws Exception {
        pool.destroy();
    }
}