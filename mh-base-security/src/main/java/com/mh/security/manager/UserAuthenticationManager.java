package com.mh.security.manager;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.servlet.context.WebApplicationContextServletContextAwareProcessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.WebUtils;

import com.mh.security.service.UserService;

/**
 * @描述：  用户认证管理
 * @author cmj
 *
 */
@Component
public class UserAuthenticationManager implements AuthenticationManager{
	
	static final List<GrantedAuthority> AUTHORITIES = new ArrayList<GrantedAuthority>();//授予权限列表

	static {
	    AUTHORITIES.add(new SimpleGrantedAuthority("ROLE_USER"));
	    
	} 
	@Resource
	private UserService userService;
	
	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		
		  //如果用户名和密码都不为空
		  if(StringUtils.isNoneBlank(auth.getName())&&StringUtils.isNoneBlank(auth.getCredentials()+"")) {
			  
			 
			  
		  }
		  throw new BadCredentialsException("Bad Credentials");
		     
	}

	
	
	

}
