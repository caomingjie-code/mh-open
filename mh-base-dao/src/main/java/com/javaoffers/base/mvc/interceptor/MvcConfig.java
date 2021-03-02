package com.javaoffers.base.mvc.interceptor;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * mvc 拦截构造
 * @author cmj
 */
public class MvcConfig implements WebMvcConfigurer{

	@Resource
	public MvcHandlerInterceptorAdapter mhi;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(mhi)
		.addPathPatterns("/**");
		WebMvcConfigurer.super.addInterceptors(registry);
	}
	
	
	
	

	
}
