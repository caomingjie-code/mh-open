package com.javaoffers.base.utils.spring;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.javaoffers.base.dao.impl.BaseBatisImpl;

@Component
@ConditionalOnSingleCandidate(BaseBatisImpl.class)
public class SpringContentUtils implements ApplicationContextAware{
	
	private static ApplicationContext springContent;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		springContent = applicationContext;
	}

	/**
	 * 
	 * @return
	 */
	public static ApplicationContext getSpringContent() {
		if(springContent==null) {
		   	//抛出Exception
		}
		return springContent;
	}
}
