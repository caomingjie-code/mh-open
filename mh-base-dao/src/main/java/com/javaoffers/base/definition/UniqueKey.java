package com.javaoffers.base.definition;

import java.util.*;

import org.apache.xerces.impl.dv.util.Base64;

import com.javaoffers.base.common.protostuff.ProtostuffUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 
 * @author mj
 *
 */
public class UniqueKey {

	static List<Class> excludes = new ArrayList<>();
	static {
		excludes.add(ServletRequest.class);
		excludes.add(ServletResponse.class);
	}

	String id;
	String requestURI;
	Map<String, String[]> parameterMap;
	Set methodArgs = new TreeSet();
	public UniqueKey(Set methodArgs,String id, String requestURI, Map<String, String[]> parameterMap,Object[] args) {
		super();
		this.id = id;
		this.requestURI = requestURI;
		this.parameterMap = parameterMap;
		this.methodArgs = methodArgs;
		if(args!=null&&args.length>0){
			for(Object oj : args){
				if(!isExcludes(oj)){
					methodArgs.add(oj);
				}
			}
		}

	}
	public boolean isExcludes(Object obj){
		boolean isExclude = false;
		if(obj != null){
			for(Class clazz : excludes){
				if(clazz.isAssignableFrom(obj.getClass())){
					isExclude = true;
					break;
				}
			}
		}
		return isExclude;
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
