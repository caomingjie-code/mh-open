package com.mh.base.utils.rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.juli.logging.Log;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mh.base.utils.log.LogUtils;

public class HttpClientUtils {

	private static final Log logger = new LogUtils().logger;

	/******************************
	 * httpclient 使用方法
	 *********************************/
	/*
	 * 使用HttpClient发送请求、接收响应很简单，一般需要如下几步即可。 1. 创建HttpClient对象。 2.
	 * 创建请求方法的实例，并指定请求URL。如果需要发送GET请求，创建HttpGet对象；如果需要发送POST请求，创建HttpPost对象。 3.
	 * 如果需要发送请求参数，可调用HttpGet、HttpPost共同的setParams(HetpParams
	 * params)方法来添加请求参数；对于HttpPost对象而言，也可调用setEntity(HttpEntity entity)方法来设置请求参数。 4.
	 * 调用HttpClient对象的execute(HttpUriRequest request)发送请求，该方法返回一个HttpResponse。 5.
	 * 调用HttpResponse的getAllHeaders()、getHeaders(String
	 * name)等方法可获取服务器的响应头；调用HttpResponse的getEntity()方法可获取HttpEntity对象，该对象包装了服务器的响应内容
	 * 。程序可通过该对象获取服务器的响应内容。 6. 释放连接。无论执行方法是否成功，都必须释放连接
	 */

	public static JSONObject postData(String targetUrl, List<NameValuePair> formparams) {
		JSONObject jsonResult = null;
		HttpPost httppost = new HttpPost(targetUrl);
		CloseableHttpClient httpclient = null;
		try {
			httpclient = HttpClients.createDefault();
			CloseableHttpResponse response = null;

			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httppost.setEntity(uefEntity);
			response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				logger.info("------------------httppost start--------------------");
				String result = EntityUtils.toString(entity, "UTF-8");
				jsonResult = JSON.parseObject(result);
				logger.info("------------------目标系统返回内容为:" + jsonResult + "-------------------");
				logger.info("------------------httppost end--------------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonResult = null; // --出现异常强制返回null
			logger.info("-----------------httppost出现异常：---------------------" + e.getMessage());
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return jsonResult;
	}

	public static JSONObject postHttpsData(String targetUrl, Map<String, String> bodyParam) {
		return postHttpsData(targetUrl,bodyParam,new HashMap<String, String>());
	}

	public static JSONObject postHttpsData(String targetUrl, Map<String, String> bodyParam,
			Map<String, String> headerParam) {
		JSONObject jsonResult = null;
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		try {
			httpClient = new SSLClient();
			httpPost = new HttpPost(targetUrl);
			// 设置参数
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			Iterator iterator = bodyParam.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> elem = (Entry<String, String>) iterator.next();
				list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
			}

			Iterator iterator2 = headerParam.entrySet().iterator();
			while (iterator2.hasNext()) {
				Entry<String, String> elem = (Entry<String, String>) iterator2.next();
				httpPost.addHeader(elem.getKey(), elem.getValue());
			}

			if (list.size() > 0) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
				httpPost.setEntity(entity);
			}

			long startTime = System.currentTimeMillis(); // 获取开始时间
			HttpResponse response = httpClient.execute(httpPost);
			long endTime = System.currentTimeMillis(); // 获取结束时间
			StatusLine statusLine = response.getStatusLine();
			System.out.println("相应状态-------------- " + statusLine);
			logger.info("------------------连接耗时：" + (endTime - startTime) + "ms*--------------------");
			System.out.println(response.toString());
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					String result = EntityUtils.toString(resEntity, "UTF-8");
					jsonResult = JSON.parseObject(result);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			jsonResult = null; // --出现异常强制返回null
		}
		return jsonResult;
	}

	public static JSONObject getData(String url) {
		JSONObject jsonResult = null;
		HttpGet httpget = new HttpGet(url);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			CloseableHttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				logger.info("------------------httpget start--------------------");
				String result = EntityUtils.toString(entity, "UTF-8");
				jsonResult = JSON.parseObject(result);
				logger.info("------------------目标系统返回内容为:" + jsonResult + "-------------------");
				logger.info("------------------httpget end--------------------");
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("-----------------httpget出现异常：---------------------" + e.getMessage());
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return jsonResult;
	}

	/**
	 * 发送xml数据
	 * 
	 * @param url
	 * @param xml
	 * @return
	 */
	public static String postXML(String url, String xml) {
		CloseableHttpClient client = null;
		CloseableHttpResponse resp = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
			client = HttpClients.createDefault();
			StringEntity entityParams = new StringEntity(xml, "utf-8");
			httpPost.setEntity(entityParams);
			client = HttpClients.createDefault();
			resp = client.execute(httpPost);
			String resultMsg = EntityUtils.toString(resp.getEntity(), "utf-8");
			return resultMsg;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (client != null) {
					client.close();
				}
				if (resp != null) {
					resp.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
