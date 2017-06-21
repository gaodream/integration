package com.integration.cloud.marathon;

import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.integration.common.Pair;
import com.integration.common.http.HttpClient;


public class MarathonClient {
	
    /**
     * 注册事件订阅
     *
     * @param callbackUrl 回调url
     * @return 返回调用post请求之后的结果
     */
    public static Pair<Integer,String> registerEventSubscription(String marathonAddress,String marathonName,String marathonPass,String callback) {
    	  StringBuilder buffer = new StringBuilder();
    	  buffer.append(marathonAddress).append("/v2/eventSubscriptions").append("?callbackUrl=").append(callback);
          if(StringUtils.isEmpty(marathonName)||StringUtils.isEmpty(marathonPass)){
          	return HttpClient.doPost(buffer.toString(),null);
          }else{
          	return HttpClient.doPostWithAuth(buffer.toString(), marathonName,marathonPass);
          }
    }
    
    /**
     * 注销事件订阅
     *
     * @param callbackUrl 回调url
     * @return 返回调用delete请求之后的结果
     */
    public static Pair<Integer,String> unRegisterEventSubscription(String marathonAddress,String marathonName,String marathonPass,String callback) {
    	  StringBuilder buffer = new StringBuilder();
    	  buffer.append(marathonAddress).append("/v2/eventSubscriptions").append("?callbackUrl=").append(callback);
          if(StringUtils.isEmpty(marathonName)||StringUtils.isEmpty(marathonPass)){
          	return HttpClient.doDelete(buffer.toString());
          }else{
          	return HttpClient.doDeleteWithAuth(buffer.toString(), marathonName,marathonPass);
          }
    }
    
    /**
     * 
     * @param json
     * @return 
     */
    public static Pair<Integer,String> deploy(String marathonUrl,String json){
    	 return  HttpClient.doPut(marathonUrl + "/v2/groups", json);
    }
    
    public static Pair<Integer,String> deployWithAuth(String marathonUrl,String json,String marathonName,String marathonPass){
    	 return  HttpClient.doPutWithAuth(marathonUrl + "/v2/groups", json,
    			 marathonName,marathonPass);
    }
    
   
    /**
     * 获取所有正在部署的部署id及相关信息
     * @return 返回部署信息列表
     */
    public static Pair<Integer,String> getDeploymentsList(String marathonAddress,String marathonName,String marathonPass) {
        String url = marathonAddress + "/v2/deployments";
        if(StringUtils.isEmpty(marathonName)||StringUtils.isEmpty(marathonPass)){
        	return HttpClient.doGet(url);
        }else{
        	return HttpClient.doGetWithAuth(url, marathonName,marathonPass);
        }
    }
    
    /**
     * 调用marathon的 /v2/queue 接口获取队列内容
     * @return 返回队列内容
     */
    public static Pair<Integer,String> getQueue(String marathonAddress,String marathonName,String marathonPass) {
        String url = marathonAddress + "/v2/queue";
        if(StringUtils.isEmpty(marathonName)||StringUtils.isEmpty(marathonPass)){
        	return HttpClient.doGet(url);
        }else{
        	return HttpClient.doGetWithAuth(url, marathonName,marathonPass);
        }
    }
    
    

    /**
     * 测试marathon连通性
     * @param addr
     * @return
     */
    public static Pair<Integer,String> pingMarathon(String marathonAddress,String marathonName,String marathonPass) {
        String url = marathonAddress+"/ping";
        if(StringUtils.isEmpty(marathonName)||StringUtils.isEmpty(marathonPass)){
        	return HttpClient.doGet(url);
        }else{
        	return HttpClient.doGetWithAuth(url, marathonName,marathonPass);
        }
    }
    
    
    /**
     * 删除指定的deployment
     *
     * @param deploymentId 部署id
     * @return 返回通用相应消息
     */
    public static Pair<Integer,String> deleteDeployment(String marathonAddress,String marathonName,String marathonPass,String deploymentId) {
        String url = marathonAddress + "/v2/deployments/" + deploymentId /*+ "?force=true"*/;
        if(StringUtils.isEmpty(marathonName)||StringUtils.isEmpty(marathonPass)){
            return HttpClient.doDelete(url);
        } else {
            return HttpClient.doDeleteWithAuth(url,marathonName,marathonPass);
        }
    }
    
    

    /**
     * 获取指定appId的marathon应用
     * @param appId 应用id
     * @return 返回指定app内容
     */
    public static Pair<Integer,String> getMarathonApp(String marathonAddress,String marathonName,String marathonPass,String appId) {
        String url = marathonAddress + "/v2/apps/" + appId;
        if(StringUtils.isEmpty(marathonName)||StringUtils.isEmpty(marathonPass)){
            return HttpClient.doGet(url);
        } else {
            return HttpClient.doGetWithAuth(url,marathonName,marathonPass);
        }
    }
    

    /**
     * 获取指定appId的marathon应用的task信息
     *
     * @param appId 应用id
     * @return 返回指定app内容
     */
    public static Pair<Integer,String> getMarathonAppTasks(String marathonAddress,String marathonName,String marathonPass,String appId) {
        String url = marathonAddress + "/v2/apps/" + appId + "/tasks";
        if(StringUtils.isEmpty(marathonName)||StringUtils.isEmpty(marathonPass)){
        	return HttpClient.doGet(url);
        } else {
            return HttpClient.doGetWithAuth(url,marathonName,marathonPass);
        }
    }
    
    
    /**
     * 获取marathon Group 列表
     * @param appId 应用id
     * @return 返回指定app内容
     */
    public static Pair<Integer,String> getMarathonGroupList(String marathonAddress,String marathonName,String marathonPass,String json) {
        String url = marathonAddress + "/v2/groups";
        if(StringUtils.isEmpty(marathonName)||StringUtils.isEmpty(marathonPass)){
        	   return HttpClient.doPut(url,json);
        } else {
            return HttpClient.doPutWithAuth(url,json,marathonName,marathonPass);
        }
    }
   
    
    /**
     * 销毁 Group
     * @param appId 应用id
     * @return 返回指定app内容
     */
    public static Pair<Integer,String> destroyGroup(String marathonAddress,String marathonName,String marathonPass,String groupId) {
    	String url = marathonAddress + "/v2/groups/"+groupId+"?force=true";
    	if(StringUtils.isEmpty(marathonName)||StringUtils.isEmpty(marathonPass)){
    		return HttpClient.doDelete(url);
    	} else {
    		return HttpClient.doDeleteWithAuth(url,marathonName,marathonPass);
    	}
    }
    
    /**
     * 更新或者创建APP
     * @param appId 应用id
     * @return 返回指定app内容
     */
    public static Pair<Integer,String> updateOrCreateApp(String marathonAddress,String marathonName,String marathonPass,String appId,String json) {
    	String url = marathonAddress + "/v2/apps/"+appId+"?force=true";
    	if(StringUtils.isEmpty(marathonName)||StringUtils.isEmpty(marathonPass)){
    		return HttpClient.doPut(url,json);
    	} else {
    		return HttpClient.doPutWithAuth(url,json,marathonName,marathonPass);
    	}
    }
   
    /**
     * 更新或者创建APP
     * @param appId 应用id
     * @return 返回指定app内容
     */
    public static Pair<Integer,String> updateOrCreateGroup(String marathonAddress,String marathonName,String marathonPass,String json) {
    	String url = marathonAddress + "/v2/groups?force=true";
    	if(StringUtils.isEmpty(marathonName)||StringUtils.isEmpty(marathonPass)){
    		return HttpClient.doPost(url,json);
    	} else {
    		return HttpClient.doPostWithAuth(url,json,marathonName,marathonPass);
    	}
    }
    

    /**
     * 获取task的数目
     * @param marathonAppId marathon的应用id
     * @return 返回活跃task数目
     */
    public static Integer getActiveTaskNumByAppId(String marathonAddress,String marathonName,String marathonPass,String appId) {
        String uri = marathonAddress + "/v2/apps/" + appId + "/tasks";
        Pair<Integer,String> result = null;
		if(StringUtils.isEmpty(marathonName)||StringUtils.isEmpty(marathonPass)){
        	result = HttpClient.doGet(uri,"application/json");
        }else{
        	result = HttpClient.doGetWithAuth(uri,"application/json",marathonName,marathonPass);
        }

        if (null!=result&&result.getFirst() == 200) {
        	JSONObject obj = JSON.parseObject(result.getSecond());
            JSONArray appList = obj.getJSONArray("tasks");
            return appList == null ? 0 : appList.size();
        }
        return 0;
    }
    

    /**
     * 获取task信息
     *
     * @param appId 应用id
     * @return 返回task信息列表
     */
	public static Pair<Integer,String> killTaskList(String marathonAddress,String marathonName,String marathonPass,String ids) {
        String uri = marathonAddress + "/v2/tasks/delete?force=true&scale=true";
 		if(StringUtils.isEmpty(marathonName)||StringUtils.isEmpty(marathonPass)){
         	return HttpClient.doPut(uri,ids);
        }else{
        	return HttpClient.doGetWithAuth(uri,ids,marathonName,marathonPass);
        }
    }
    
    
    /**
     * 获取task信息
     * TODO :根据需要修改
     * @param appId 应用id
     * @return 返回task信息列表
     */
	public static List<?> getTaskList(String marathonAddress,String marathonName,String marathonPass,String appId) {
        String uri = marathonAddress + "/v2/apps/" + appId + "/tasks";
        Pair<Integer,String> result = null;
 		if(StringUtils.isEmpty(marathonName)||StringUtils.isEmpty(marathonPass)){
         	result = HttpClient.doGet(uri,"application/json");
        }else{
         	result = HttpClient.doGetWithAuth(uri,"application/json",marathonName,marathonPass);
        }
 		if (null!=result&&result.getFirst()==200) {

            return null;
        } else {
            return Collections.emptyList();
        }
    }


}
