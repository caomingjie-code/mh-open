package com.mh.base.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.stereotype.Component;

import com.mh.base.utils.redis.JedisUtils;

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
		}
		
	}

}
