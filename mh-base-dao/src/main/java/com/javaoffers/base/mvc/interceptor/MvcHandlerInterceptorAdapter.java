package com.javaoffers.base.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * 请求拦截
 * @author mj
 *
 */
@SuppressWarnings("restriction")
@Component
public class MvcHandlerInterceptorAdapter extends HandlerInterceptorAdapter{
	
	
    private static final String  ENABLE_CACHE = "com.javaoffers.base.cache.annotation.EnableCache";
    private static final String  UNABLE_CACHE = "com.javaoffers.base.cache.annotation.UnableCache";
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//logger.info("preHandle=========================================================");
		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		//logger.info("postHandle=========================================================");
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		//logger.info("afterCompletion=========================================================");
		super.afterCompletion(request, response, handler, ex);
	}

	@Override
	public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//logger.info("afterConcurrentHandlingStarted=========================================================");
		super.afterConcurrentHandlingStarted(request, response, handler);
	}
	

	

	
	
}
