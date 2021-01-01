package com.javaoffers.base.mvc.interceptor;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * mvc 拦截构造
 * @author cmj
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer{

	@Resource
	public MvcHandlerInterceptorAdapter mhi;
	@Resource
	public DataSourceRouteInterceptorAdapter dsri;
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(mhi)
		.addPathPatterns("/**");
		
		registry.addInterceptor(dsri)
		.addPathPatterns("/**");
		WebMvcConfigurer.super.addInterceptors(registry);
	}
	
	
	
	

	
}
