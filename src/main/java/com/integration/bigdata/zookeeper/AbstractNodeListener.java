package com.integration.bigdata.zookeeper;


import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractNodeListener implements NodeCacheListener {

	private static Logger LOG = LoggerFactory.getLogger(AbstractNodeListener.class);

	@Override
	public void nodeChanged() throws Exception {
		LOG.info("************nodeChanged*************");
		
	}
			
	
}
