package com.javaoffers.base.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.javaoffers.base.utils.datasource.BaseComboPooledDataSource;
import org.springframework.stereotype.Component;

import com.javaoffers.base.utils.redis.JedisUtils;

/**
 * 资源回收过滤器
 * @author mj
 *
 */

@Component
public class ReturnResourceFilter implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} catch (Exception e) {
			throw e;
		}finally {
			//回收Redis资源
			JedisUtils.returnResource();
			BaseComboPooledDataSource.clean();//清楚陆游数据
		}
		
	}



}
