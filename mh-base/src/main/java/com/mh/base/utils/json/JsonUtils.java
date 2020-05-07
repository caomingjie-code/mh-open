/**   
 * Copyright © 2019 mh615 Info.  All rights reserved.
 * 
 * 功能描述：
 * @Package: com.mh.others.json 
 * @author: cmj   
 * @date: 2019-1-12 下午5:35:03 
 */
package com.mh.base.utils.json;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Copyright: Copyright (c) 2019 LanRu-Caifu
 * 
 * @ClassName: JSONUtils.java
 * @Description: 该类的功能描述: object - > json
 *
 * @version: v1.0.0
 * @author: 曹明杰
 * @date: 2019-1-12 下午5:35:03
 *
 *        Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2019-1-12
 *        cmj v1.0.0 修改原因
 */
public class JsonUtils {

	private static Map<String, Object> convertMap(Object childObj, Object parentChild) throws Exception {
		Object obj = childObj;// 当前迭代的对象
		if (obj == null) {
			return null;
		}
		if (childObj.equals(parentChild)) {
			return null;
		}
		if (obj instanceof Map) { // map
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			Map map = (Map) obj;
			if (!map.isEmpty()) {
				Set keySet = map.keySet();
				Iterator iterator = keySet.iterator();
				while (iterator.hasNext()) {
					Object next = iterator.next();
					Object value = map.get(next);
					if (value instanceof String || value instanceof Integer || value instanceof Long
							|| value instanceof Boolean || value instanceof Float || value instanceof Double) {
						hashMap.put(next.toString(), value);
					} else {
						Map<String, Object> convertMap = convertMap(value, obj);
						if (convertMap != null)
							hashMap.put(next.toString(), convertMap);
					}
				}
			}
			return hashMap;
		}

		if (obj instanceof Object) { // bean
			HashMap<String, Object> map = new HashMap<String, Object>();
			Field[] declaredFields = obj.getClass().getDeclaredFields();
			if (declaredFields != null && declaredFields.length > 0) {
				for (int i = 0; i < declaredFields.length; i++) {
					Field field = declaredFields[i];
					field.setAccessible(true);
					String name = field.getName(); // 字段名
					Object value = field.get(obj); // 字段值
					if (value == null)
						continue;
					if (value instanceof CharSequence ||  value instanceof  Number) { //字符和数字
						map.put(name, value.toString());
					}else if(value instanceof Date) { //日期
						map.put(name, DateFormatUtils.format((Date)value, "yyyy-MM-dd HH-mm-ss"));
					}
					else {

						if (value instanceof Collection || value.getClass().isArray()) {
							List<Map<String, Object>> list_map = convertMapforCollect(value, parentChild);
							if (list_map != null)
								map.put(name, list_map);
						}
						if (value instanceof Map) {
							Map<String, Object> mapv = convertMap(value, parentChild);
							if (mapv != null) {
								map.put(name, mapv);
							}
						}

					}
				}
			}
			return map;
		}
		return null;
	}

	/**
	 * @throws IllegalAccessException @throws IllegalArgumentException @Title:
	 * convertMapforCollect @Description: TODO(作用：) @param: @param
	 * value @param: @param parentChild @param: @return @return:
	 * List<Map<String,Object>> @Auther: cmj @throws
	 */
	private static List<Map<String, Object>> convertMapforCollect(Object obj, Object parentChild) throws Exception {
		if (obj instanceof Collection) { // collection
			ArrayList<Map<String, Object>> list_map = new ArrayList<Map<String, Object>>();
			Map<String, Object> convertMap = null;
			Collection ll = (Collection) obj;
			if (!ll.isEmpty()) {
				Iterator iterator = ll.iterator();
				while (iterator.hasNext()) {
					Object value = iterator.next();
					// 如果集合中存放的对象是JDK基础对象 则过滤掉
					if (value instanceof String || value instanceof Integer || value instanceof Long
							|| value instanceof Boolean || value instanceof Float || value instanceof Double) {
						continue;
					} else {
						if (value instanceof Collection || value.getClass().isArray()) {
							List<Map<String, Object>> list_map1 = convertMapforCollect(value, parentChild);
							if (list_map1 != null) {
								Map nmap = new HashMap();
								nmap.put("list", value);
								list_map.add(nmap);
							}

						} else {
							convertMap = convertMap(value, obj);
							if (convertMap != null)
								list_map.add(convertMap);
						}

					}
				}
			}
			return list_map;
		}
		if (obj.getClass().isArray()) { // array
			ArrayList<Map<String, Object>> list_map = new ArrayList<Map<String, Object>>();
			Map<String, Object> convertMap = null;
			int len = Array.getLength(obj);
			for (int i = 0; i < len; i++) {
				Object value = Array.get(obj, i);
				if (value instanceof String || value instanceof Integer || value instanceof Long
						|| value instanceof Boolean || value instanceof Float || value instanceof Double) {
					continue;
				} else {
					if (value instanceof Collection || value.getClass().isArray()) {
						List<Map<String, Object>> list_map1 = convertMapforCollect(value, parentChild);
						if (list_map1 != null) {
							Map nmap = new HashMap();
							nmap.put("list", value);
							list_map.add(nmap);
						}

					} else {
						convertMap = convertMap(value, obj);
						if (convertMap != null)
							list_map.add(convertMap);
					}
				}
			}
			return list_map;
		}
		return null;
	}

	/**
	 * @throws Exception @Title: MKListMap @Description: TODO(作用： 将obj
	 * 转换为json) @param: @param arrayList @param: @return @return:
	 * List<Map<String,Object>> @Auther: cmj @throws
	 */
	public static Json makeJSON(Object obj) throws Exception {

		if (obj instanceof Collection || obj.getClass().isArray()) {
			HashMap<String, Object> hashMap = new HashMap<>();
			List<Map<String, Object>> list = convertMapforCollect(obj, null);
			JSONArray jsonArray = new JSONArray(list);
			Json json = new Json(null, jsonArray, null);
			return json;
		}
		Map<String, Object> map = convertMap(obj, null);
		JSONObject jsonObject = new org.json.JSONObject(map);
		Json json = new Json(jsonObject, null, null);
		return json;
	}

	/**
	 * 利用 jackJson 实现json转化
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static Json makeJackJSON(Object obj) throws Exception {
		JsonFactory jsonFactory = new JsonFactory();// 创建工厂
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		JsonGenerator generator = jsonFactory.createGenerator(byteArrayOutputStream, JsonEncoding.UTF8);
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectWriter writer = objectMapper.writer();
		writer.writeValue(generator, obj);
		String jsonStr = byteArrayOutputStream.toString();
		byteArrayOutputStream.close();
		return new Json(null, null, jsonStr);
	}

	/**
	 * Json 封装
	 */
	public static class Json {
		private JSONObject jsonObject = null;
		private JSONArray jsonArray = null;
		private String jsonStr = null;

		public Json(JSONObject jsonObject, JSONArray jsonArray, String jsonStr) {
			this.jsonObject = jsonObject;
			this.jsonArray = jsonArray;
			this.jsonStr = jsonStr;
		}

		@Override
		public String toString() {
			if (jsonObject != null) {
				return jsonObject.toString();
			}
			if (jsonArray != null) {
				return jsonArray.toString();
			}
			if (jsonStr != null) {
				return jsonStr;
			}
			return "";
		}
	}

}
