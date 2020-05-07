package com.mh.security.filter;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.mh.security.manager.UserAuthenticationManager;


public class MHSecurityFilter implements Filter{
	
	//注入自己的管理者
	
	private UserAuthenticationManager uam  ;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		chain.doFilter(request, response); 
	}

	public MHSecurityFilter(UserAuthenticationManager uam) {
		super();
		this.uam = uam;
	}

	

	
}
