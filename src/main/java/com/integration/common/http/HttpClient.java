package com.integration.common.http;

import java.io.IOException;
import java.nio.charset.CodingErrorAction;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.integration.common.Pair;


public class HttpClient {

	private static Logger LOG = LoggerFactory.getLogger(HttpClient.class);

	private static CloseableHttpClient httpClient;
	private static int SOCKET_TIME_OUT = 5000;
	private static int CONNECTION_TIME_OUT = 5000;
	private static int CONNECTION_REQUEST_TIME_OUT = 5000;
	static {
		PoolingHttpClientConnectionManager connectPoolManager = new PoolingHttpClientConnectionManager();

		ConnectionConfig config = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE)
				.setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).build();

		connectPoolManager.setDefaultConnectionConfig(config);

		RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT)
				.setExpectContinueEnabled(true).setSocketTimeout(SOCKET_TIME_OUT).setConnectTimeout(CONNECTION_TIME_OUT)
				.setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT).build();

		httpClient = HttpClients.custom().setConnectionManager(connectPoolManager)
				.setDefaultRequestConfig(requestConfig).build();
	}

	/**
	 * 执行http get请求
	 */
	public static Pair<Integer, String> doGet(String uri) {
		HttpGet request = new HttpGet(uri);
		return execute(request, null);
	}

	/**
	 * 执行http get请求
	 */
	public static Pair<Integer, String> doGet(String uri, String acceptValue) {
		HttpGet request = new HttpGet(uri);
		request.setHeader("Accept", acceptValue);
		return execute(request, null);
	}

	/**
	 * 执行http get请求
	 *
	 * @param uri
	 *            要访问的uri
	 * @return 返回响应消息
	 */
	public static Pair<Integer, String> doGetWithAuth(String uri, String username, String password) {
		HttpClientContext context = assembleHttpClientContext(username, password);
		HttpGet request = new HttpGet(uri);
		return execute(request, context);
	}

	/**
	 * 执行http get请求
	 *
	 * @param uri
	 *            要访问的uri
	 * @return 返回响应消息
	 */
	public static Pair<Integer, String> doGetWithAuth(String uri, String acceptValue, String username,
			String password) {
		HttpClientContext context = assembleHttpClientContext(username, password);
		HttpGet request = new HttpGet(uri);
		request.setHeader("Accept", acceptValue);
		return execute(request, context);
	}

	/**
	 * 执行http put请求
	 * 
	 * @param uri
	 *            要访问的uri
	 * @param body
	 *            要访问的消息体部分
	 * @return 返回响应消息
	 */
	public static Pair<Integer, String> doPut(String uri, String body) {
		HttpPut request = new HttpPut(uri);
		if (!StringUtils.isEmpty(body)) {
			request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON.withCharset("UTF-8")));
		}
		return execute(request, null);
	}

	/**
	 * 执行http put请求
	 *
	 * @param uri
	 *            要访问的uri
	 * @param body
	 *            要访问的消息体部分
	 * @return 返回响应消息
	 */
	public static Pair<Integer, String> doPutWithAuth(String uri, String body, String username, String password) {
		HttpClientContext context = assembleHttpClientContext(username, password);
		HttpPut request = new HttpPut(uri);
		if (!StringUtils.isEmpty(body)) {
			request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON.withCharset("UTF-8")));
		}
		return execute(request, context);
	}

	/**
	 * 执行http delete请求
	 * 
	 * @param uri
	 *            要访问的uri
	 * @return 返回响应消息
	 */
	public static Pair<Integer, String> doDelete(String uri) {
		HttpDelete request = new HttpDelete(uri);
		return execute(request, null);
	}

	/**
	 * 执行http delete请求
	 * 
	 * @param uri
	 *            要访问的uri
	 * @return 返回响应消息
	 */
	public static Pair<Integer, String> doDeleteWithAuth(String uri, String username, String password) {
		HttpClientContext context = assembleHttpClientContext(username, password);
		HttpDelete request = new HttpDelete(uri);
		return execute(request, context);
	}

	/**
	 * 执行http post请求
	 * 
	 * @param uri
	 *            要访问的uri
	 * @return 返回响应消息
	 */
	public static Pair<Integer, String> doPost(String uri, String body) {
		HttpPost request = new HttpPost(uri);
		if (!StringUtils.isEmpty(body)) {
			request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON.withCharset("UTF-8")));
		}
		return execute(request, null);
	}

	/**
	 * 执行http post请求
	 *
	 * @param uri
	 * @return 返回响应消息
	 */
	public static Pair<Integer, String> doPostWithAuth(String uri, String username, String password) {
		HttpClientContext context = assembleHttpClientContext(username, password);
		HttpPost request = new HttpPost(uri);
		return execute(request, context);
	}

	/**
	 * 执行http post请求
	 *
	 * @param uri
	 * @return 返回响应消息
	 */
	public static Pair<Integer, String> doPostWithAuth(String uri, String body, String username, String password) {
		HttpClientContext context = assembleHttpClientContext(username, password);
		HttpPost request = new HttpPost(uri);
		if (!StringUtils.isEmpty(body)) {
			request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON.withCharset("UTF-8")));
		}
		return execute(request, context);
	}

	/**
	 * 构造httpClient上下文，为执行http操作时配置鉴权信息
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	private static HttpClientContext assembleHttpClientContext(String username, String password) {
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(username, password));
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credsProvider);
		return context;
	}

	/**
	 * 执行http请求
	 *
	 * @param request
	 *            http请求
	 * @return 返回通用响应消息
	 */
	private static Pair<Integer, String> execute(HttpRequestBase request, HttpClientContext context) {
		try {
			HttpResponse response = httpClient.execute(request, context);
			return new Pair<Integer, String>(response.getStatusLine().getStatusCode(),
					EntityUtils.toString(response.getEntity()));
		} catch (IOException e) {
			LOG.warn("{}", e);
		} finally {
			request.releaseConnection();
		}
		return new Pair<Integer, String>(-1, "execute request failed");
	}
}
