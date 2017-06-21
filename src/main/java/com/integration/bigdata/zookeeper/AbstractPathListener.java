package com.integration.bigdata.zookeeper;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractPathListener implements PathChildrenCacheListener {

	private static Logger LOG = LoggerFactory.getLogger(AbstractPathListener.class);
			
	@Override
	public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
			throws Exception {

		String data = "";
		switch (event.getType()) {
			case CHILD_ADDED:
				data = ZKClient.getData(event.getData().getPath());
				childAdded(event.getData().getPath(), data, event);
				break;
			case CHILD_REMOVED:
				childRemoved(event.getData().getPath(), data, event);
				break;
			case CHILD_UPDATED:
				data = ZKClient.getData(event.getData().getPath());
				childUpdated(event.getData().getPath(), data, event);
				break;
			case CONNECTION_RECONNECTED:
				LOG.warn("CONNECTION_RECONNECTED....");
				//TODO need rebuild?
	            break;
	          case CONNECTION_SUSPENDED:
	          case CONNECTION_LOST:
	        	  LOG.warn("Connection error,waiting...");
	            break;	
			default:
				LOG.info(" default Type {}", event.getType().name());
				break;
		}
	}
	
    public void childAdded(String path, String data, PathChildrenCacheEvent event) {
        LOG.info("[MarathonLeaderWatchHandler] : event is child add");
        changeEvent();
    }

    public void childRemoved(String path, String data, PathChildrenCacheEvent event) {
        LOG.info("[MarathonLeaderWatchHandler] : event is child remove");
        changeEvent();
    }

    public void childUpdated(String path, String data, PathChildrenCacheEvent event) {
        LOG.info("[MarathonLeaderWatchHandler] : event is child update");
        changeEvent();
    }
    
    public  abstract  void changeEvent();
}
