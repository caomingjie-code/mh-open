package com.javaoffers.base.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.javaoffers.base.annotation.datasource.DataSourceRoute;
import com.javaoffers.base.utils.datasource.BaseComboPooledDataSource;

/**
 * 事务route
 * @author cmj
 *
 */
@Component
public class DataSourceRouteInterceptorAdapter extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(handler instanceof HandlerMethod) {
			HandlerMethod hm = (HandlerMethod)handler;
			DataSourceRoute methodAnnotation = hm.getMethodAnnotation(DataSourceRoute.class);
			if(methodAnnotation!=null) {
				String value = methodAnnotation.value();
				BaseComboPooledDataSource.setDataSourceRoute(value);
			}
		}
		return true;
	}

}
