package com.javaoffers.base.quartz.task;

import java.lang.reflect.Method;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

public class BaseStateTask implements StatefulJob {

	Class className;
	Method method;
	String param;
	
	@SuppressWarnings("rawtypes")
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			/**
			 *  map.put("className", className);
			    map.put("method", method);
			    map.put("param", param);
			 */
			 JobDataMap map = context.getJobDetail().getJobDataMap();
		     Class className = (Class) map.get("className");
		     Method method = (Method) map.get("method");
		     String param = (String)map.get("param");
		     
			 Object newInstance = className.newInstance();
			method.setAccessible(true);
			method.invoke(newInstance, param);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Class getClassName() {
		return className;
	}

	public void setClassName(Class className) {
		this.className = className;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}


	
}
 