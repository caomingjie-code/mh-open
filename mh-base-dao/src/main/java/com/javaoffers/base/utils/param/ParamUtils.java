package com.javaoffers.base.utils.param;

import com.javaoffers.base.common.json.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public class ParamUtils {
	
	public static HashMap<String, Object> buildParamsMap(Object obj) {
		HashMap<String, Object> pm = new HashMap<String, Object>();
		pm.put("PM", obj);
		pm.putAll(JsonUtils.parseObject(JsonUtils.toJSONString(obj),Map.class));//支持原生传递参数
		if (obj == null) {
			pm.put("begeinIndex", 0);
			pm.put("pageSize", 20);
		} else {
			if (obj instanceof Map) {
				Map map = (Map) obj;
				String pageNum = (map.get("begeinIndex") == null ? "0" : map.get("begeinIndex")).toString();
				String pageSize = (map.get("pageSize") == null ? "20" : map.get("pageSize")).toString();
				pm.put("begeinIndex", pageNum);
				pm.put("pageSize", pageSize);
			} else {
				pm.put("begeinIndex", 0);
				pm.put("pageSize", 20);
			}
		}
		// 获取视图的名字
		String view = System.nanoTime() + "timp";
		pm.put("view", view);
		return pm;
	}

	
}
