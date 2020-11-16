package com.mh.security.config;

import javax.annotation.Resource;

import com.mh.security.authentication.AuthenticationFailureHandlerImpl;
import com.mh.security.authentication.AuthenticationSuccessHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import com.mh.security.filter.MHSecurityFilter;
import com.mh.security.manager.UserAuthenticationManager;

/**
 * 构造配置
 * @author cmj
 *
 */
@EnableWebSecurity
public class MHBaseSecurityConfig extends WebSecurityConfigurerAdapter{
   
	@Resource
	private UserAuthenticationManager uam  ;

	protected void configure(HttpSecurity http) throws Exception {
		 http
	        .authorizeRequests()//获取“请求URL认证授权表达式对象”
	        	//.antMatchers().permitAll()   //放行  动态请求
	        	.mvcMatchers("/js/**","/css/**","/picture/**","/more/**").permitAll() //放行  静态资源  第一个 / 就是代表static 目录下
	            .anyRequest().authenticated()//所有请求都需要认真
	            .and()
	        .formLogin()//获取“构造表单登录对象”
	            .loginPage("/security/login")//该url有两种含义,GET: 跳转到登录页面走Controller中的方法然后定位页面,Post 代表走认证逻辑由UserDetailsService的子类实现
	            //.successForwardUrl("/security/loginProcessing")//认证成功后跳转页面,现在改成ajax提交，所以登陆跳转要交给前端处理
				 .successHandler(new AuthenticationSuccessHandlerImpl()) //登陆成功的处理，默认是重定向到successForwardUrl，现在自己处理
				 .failureHandler(new AuthenticationFailureHandlerImpl()) //登陆失败的处理，默认是重定向到登陆页面，现在我们自己处理
				 .permitAll()//允许所有
	             .and()
	        .logout()
	        	.deleteCookies("SessionId")
	        	.logoutUrl("/security/logOut")
	        	.logoutSuccessUrl("/security/login")
	        	.and()
	        .addFilterBefore(new MHSecurityFilter(uam), FilterSecurityInterceptor.class)


		 ;
		        
	}

	  
}
