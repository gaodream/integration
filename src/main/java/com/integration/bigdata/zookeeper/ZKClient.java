package com.integration.bigdata.zookeeper;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKClient {

	private static Logger logger = LoggerFactory.getLogger(ZKClient.class);
	private final static String CHARSET = "UTF-8";
	
	private static String connectString = "";  
    private static int sessionTimeOut = 30000;  
    private static int connectionTimeOut = 30000; 
    
    private static CuratorFramework zkClient = null;
    
    private static boolean singleton = true;  
    private volatile static boolean isConnected = false;
    
    /**
     * 
     * @param connectString 连接字符串
     * @param sessionTimeOut session超时时间
     * @param connectionTimeout 连接超时时间
     * @param namespace 命名空降
     * @return
     */
	public static CuratorFramework connect(String connectString, int sessionTimeOut, int connectionTimeout, String namespace) {
		ZKClient.connectString = connectString;
		ZKClient.sessionTimeOut = sessionTimeOut;
		ZKClient.connectionTimeOut = connectionTimeout;
		CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();  
	    zkClient = builder.connectString(connectString).sessionTimeoutMs(sessionTimeOut).connectionTimeoutMs(connectionTimeout)  
	            .canBeReadOnly(true).namespace(namespace).retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
	            .defaultData(null).build();  
	    zkClient.start();
		try {
			boolean flag = zkClient.getZookeeperClient().blockUntilConnectedOrTimedOut();
			if(flag){
				logger.info("connect zookeeper success.");
				isConnected = true;
			}else{
				logger.error("connect zookeeper timeout.");
			}
		} catch (InterruptedException e) {
			logger.error("connect zookeeper exception.");
		}
	    return zkClient;
		
	}
	
	public static CuratorFramework connect(String connectString, int sessionTimeOut, int connectionTimeout) {
		ZKClient.connectString = connectString;
		ZKClient.sessionTimeOut = sessionTimeOut;
		ZKClient.connectionTimeOut = connectionTimeout;
		CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();  
	    zkClient = builder.connectString(connectString)
	    		.sessionTimeoutMs(sessionTimeOut)
	    		.connectionTimeoutMs(connectionTimeout)  
	            .canBeReadOnly(true)
	            .retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
	            .defaultData(null).build();  
	    zkClient.start();
		try {
			boolean flag = zkClient.getZookeeperClient().blockUntilConnectedOrTimedOut();
			if(flag){
				logger.info("connect zookeeper success.");
				isConnected = true;
			}else{
				logger.error("connect zookeeper timeout.");
			}
		} catch (InterruptedException e) {
			logger.error("connect zookeeper exception.");
		}
	    return zkClient;
		
	}
	
	public static CuratorFramework connect(String connectString,int sessionTimeOut) {
		return connect(connectString,sessionTimeOut,connectionTimeOut);
	}
	
	public static CuratorFramework connect(String connectString) {
		return connect(connectString,sessionTimeOut,connectionTimeOut);
	}
	public static CuratorFramework connect() {
		return connect(connectString,sessionTimeOut,connectionTimeOut);
	}
	
	/**
	 * 非第一次使用
	 * @return
	 */
    public static CuratorFramework getInstance() {  
        if (singleton) {  
            if (zkClient == null) {  
                zkClient = connect();  
                zkClient.start();  
            }  
            return zkClient;  
        }  
        return connect();  
    }  
	
    /**
     * 判断是否已经连接
     * @return
     */
    public static boolean isConnected(){
    	return isConnected;
    }
	
	public static void close(){
		logger.info("close zookeeper.");
		if (zkClient != null) {
			zkClient.close();
		}

		
	}
	
	
	 /**
     * 去掉path的结尾的 / 字符
     * @param path
     * @return
     */
    private static String formatPath(String path) {
        return path.endsWith("/") ? path.substring(0, path.length() -1) : path;
    }
	
	/**
	 * 检查目录是否存在
	 * @param path 目录
	 * @return 是否存在
	 * @throws Exception
	 */
    public static boolean checkExists(String path) throws Exception {
        String trimPath = formatPath(path);
    	return (zkClient.checkExists().forPath(trimPath) != null);
    }
    
	/**
	 * 创建目录：默认情况下，如果父目录不存在会创建，是否创建存储模式是PERSISTENT
	 * @param path 创建路径
	 * @throws Exception
	 */
	public static void createPath(String path) throws Exception {
		createPath(path,true,CreateMode.PERSISTENT);
	}
	
	/**
	 * 创建目录
	 * @param path 目录
	 * @param force 如果父目录不存在是否创建
	 * @param mode 存储模式
	 * @throws Exception
	 */
	public static void createPath(String path,boolean force,CreateMode mode) throws Exception {
		String trimPath = formatPath(path);
		if(!checkExists(trimPath)){
			if(force){
				zkClient.create().creatingParentsIfNeeded().withMode(mode).forPath(trimPath);
			}else{
				zkClient.create().withMode(mode).forPath(trimPath);
			}
		}
	}
	
	public static void createAndWrite(String path,String data) throws Exception {
		createAndWrite(path,data,CreateMode.PERSISTENT);
	}
	
	public static void createAndWrite(String path,String data,CreateMode mode) throws Exception {
		String trimPath = formatPath(path);
		if(!checkExists(trimPath)){
			zkClient.create().creatingParentsIfNeeded().withMode(mode).forPath(trimPath, data.getBytes(CHARSET));
		}else{
			zkClient.setData().forPath(trimPath, data.getBytes(CHARSET));
		}
	}
	
	
	
	/**
	 * 删除目录:默认情况下会删除子目录
	 * @param path 
	 * @throws Exception
	 */
	public static void deletePath(String path) throws Exception {
		deletePath(path,true);
	}
	
	/**
	 * 删除目录
	 * @param path
	 * @param withParent 是否子目录
	 * @throws Exception
	 */
	public static void deletePath(String path,boolean force) throws Exception {
		String trimPath = formatPath(path);
		if(checkExists(trimPath)){
			if(force){
				zkClient.delete().deletingChildrenIfNeeded().forPath(trimPath);
			}else{
				zkClient.delete().forPath(trimPath);
			}
		}
	}

	
	
	/**
	 * 向指定目录写数据：默认情况下，路径不存在时，是否创建存储模式是PERSISTENT
	 * @param path 路径
	 * @param data 数据
	 * @throws Exception
	 */
	public static void write(String path,String data) throws Exception {
		write(path,data,true,CreateMode.PERSISTENT);
	}
	
	/**
	 * 向指定目录写数据
	 * @param path 路径
	 * @param data 数据
	 * @param force 路径不存在时，是否创建
	 * @throws Exception
	 */
	public static void write(String path,String data,boolean force) throws Exception {
		write(path,data,force,CreateMode.PERSISTENT);
	}
	
	/**
	 * 像指定目录写数据
	 * @param path 路径
	 * @param data 数据
	 * @param force 路径不存在是否创建
	 * @param mode 模式：持久化，持久化序列，临时，临时序列
	 * @throws Exception
	 */
	public static void write(String path, String data,boolean force,CreateMode mode) throws Exception {
		String trimPath = formatPath(path);
		data = data==null?"": data;
		if(checkExists(trimPath)){
			zkClient.setData().forPath(trimPath, data.getBytes(CHARSET));
		}else{
			if(force)
			zkClient.create().creatingParentsIfNeeded().withMode(mode).forPath(trimPath, data.getBytes(CHARSET));
		}
	}


	
	/**
	 * 获取数据指定节点数据
	 * @param path 节点路径
	 * @return
	 * @throws Exception
	 */
	public static String getData(String path) throws Exception {
        String trimPath = formatPath(path);
		return new String(zkClient.getData().forPath(trimPath), CHARSET);
	}
	
	/**
	 * 获取最小序号的zk节点（针对有序节点）
	 * @param path 目录
	 * @return
	 */
	public static String getMinSeqNode(String path) throws Exception{
		return getMinSeqNode(path,null);
	}
	
	/**
	 * 获取最小序号的zk节点（针对有序节点）
	 * @param path 目录
	 * @param prefix 子节点前缀
	 * @return
	 */
	public static String getMinSeqNode(String path,String prefix) throws Exception{
		if (checkExists(path)) {
			List<String> nodes = getChildrenPath(path);
			prefix = null==prefix?"":prefix;
			final String tmp = prefix;
			Optional<String> result = nodes.parallelStream()
			.filter(node -> node.startsWith(tmp))
			.sorted()
			.findFirst();
			if(result.isPresent())  {
				return getData(path + "/"+ result.get());
			}
		}
		return null;
		
	}


	
	/**
	 * 获取子目录，非完整路径
	 * @param path 目录
	 * @return 子目录
	 * @throws Exception
	 */
	public static List<String> getChildrenPath(String path) throws Exception{
        String trimPath = formatPath(path);
		return zkClient.getChildren().forPath(trimPath);
	}
	
	/**
	 * 获取子目录，完整路径
	 * @param path 目录
	 * @return 子目录
	 * @throws Exception
	 */
	public static List<String> getChildrenFullPath(String path) throws Exception{
        String trimPath = formatPath(path);
		List<String> names = getChildrenPath(trimPath);
		names.replaceAll(new UnaryOperator<String>(){
			@Override
			public String apply(String t) {
				return  trimPath + "/" +t;
			}
		});
		
		return names;
    }


    
    /**
     * 监控子目录变化
     * @param path 监听目录 
     * @param listener  监听事件
     * @return
     * @throws Exception
     */
    public static PathChildrenCache addPathListener(String path,PathChildrenCacheListener listener) throws Exception {
    	return addPathListener(path, listener, false);
    }
    
    /**
     * 监控子目录变化
     * @param path 监听目录 
     * @param listener  监听事件
     * @param cacheNode 是否缓存节点数据
     * @return
     * @throws Exception
     */
    public static PathChildrenCache addPathListener(String path,PathChildrenCacheListener listener, Boolean cacheNode) throws Exception {
        String trimPath = formatPath(path);
        PathChildrenCache cache = new PathChildrenCache(zkClient, trimPath, cacheNode);
        cache.getListenable().addListener(listener);
    	//NORMAL：The cache will be primed (in the background) with initial values.Events for existing and new nodes will be posted.
    	//        已有的和新节点都会触发事件。
    	//BUILD_INITIAL_CACHE:The cache will be primed (in the foreground) with initial values. 
    	//				PathChildrenCache.rebuild() will be called before the PathChildrenCache.start(StartMode) method returns in order to get an initial view of the node.
        cache.start(StartMode.BUILD_INITIAL_CACHE);
    	//cached.start(StartMode.NORMAL);
    	//TODO
    	//创建监听后，立马往里面放数据，可能会有丢失监听的情况。是否需要sleep。
    	return cache;
    }

    /**
     * 增加node cache监听
     * @param nodeCache node cache
     * @param listener 监听
     * @return node cache
     * @throws Exception
     */
    public static NodeCache addNodeListener(String path, NodeCacheListener listener) throws Exception {
    	String trimPath = formatPath(path);
    	NodeCache cache = new NodeCache(zkClient, trimPath);
    	cache.getListenable().addListener(listener);
    	cache.start(true);
    	return cache;
    }
    
	
	/**
	 * 获取父路径
	 * @param path	路径
	 * @return	父路径
	 */
	public static String getParentPath(String path) {
        String trimPath = formatPath(path);
		int index = trimPath.lastIndexOf("/");
        String parentPath = trimPath.substring(0,index);
        return parentPath;
    }
	
	/**
	 * 获取节点名称
	 * @param path	路径
	 * @return 节点名称
	 */
	public static String getName(String path) {
        String trimPath = formatPath(path);
		int i = trimPath.lastIndexOf("/");
		return trimPath.substring(i+1);
	}
 
}