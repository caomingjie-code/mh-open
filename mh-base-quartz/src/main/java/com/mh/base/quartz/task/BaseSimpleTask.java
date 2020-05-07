package com.mh.base.quartz.task;

import java.lang.reflect.Method;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class BaseSimpleTask implements Job {
	Class className;
	Method method;
	String param;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			 JobDataMap map = context.getJobDetail().getJobDataMap();
			 Class className = (Class) map.get("className");
		     Method method = (Method) map.get("method");
		     Object param = map.get("param");
		     //保证线程安全，每次都会新创建一task
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
