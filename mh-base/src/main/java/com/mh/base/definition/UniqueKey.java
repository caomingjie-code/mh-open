package com.mh.base.definition;

import java.util.Map;

import org.apache.xerces.impl.dv.util.Base64;

import com.mh.base.utils.protostuff.ProtostuffUtils;

/**
 * 
 * @author mj
 *
 */
public class UniqueKey {
	String id;
	String requestURI;
	Map<String, String[]> parameterMap;
	public UniqueKey(String id, String requestURI, Map<String, String[]> parameterMap) {
		super();
		this.id = id;
		this.requestURI = requestURI;
		this.parameterMap = parameterMap;
	}
	/**
	 * 返回unique Key
	 * @return
	 */
	public  String getUniqueKeyStr() {
		byte[] encode = ProtostuffUtils.encode(this);
		String uniqueKey = Base64.encode(encode);
		return uniqueKey;
	}
}
