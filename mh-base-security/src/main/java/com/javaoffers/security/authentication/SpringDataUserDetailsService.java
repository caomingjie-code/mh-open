package com.javaoffers.security.authentication;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.javaoffers.base.common.json.JsonUtils;
import com.javaoffers.base.common.log.LogUtils;
import com.javaoffers.security.MHRole;
import com.javaoffers.security.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.javaoffers.security.MHUser;
import com.javaoffers.security.service.UserService;

/**
 * 自定义认证服务
 * @author cmj
 *
 */
@Component
public class SpringDataUserDetailsService implements UserDetailsService{

	private String MD5="MD5";//指定解密算法
	@Resource
	UserService userService; //查询用户信息
	/**
	 * username : 前台传来的用户名,
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//查询用户
		MHUser user = userService.queryUserByLoginName(username);
		//将用户放入session
		SecurityUtils.getHttpSession().setAttribute("user",user);
		//如果存在
		if(user!= null&&user.getLoginName()!=null) {
			try {
				String json = JsonUtils.toJSONString(user);
				LogUtils.printLog(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//查询加密后的密码（密文）
			String passwdEncode = user.getPassWd();
			String salt = user.getSalt();
			//拼接盐加密格式: {md5}{salt}passwdEncode 代表用md5进行加密，盐为slat, passwdEncode 为密码加密后的密文
			/**
			 * 匹配原理：根据{md5}找到对应的MD5算法，然后根据{salt}获取盐,
			 * 然后再使slat+password(web端传来的密码)进行MD5加密后的字符与 passwdEncode 进行匹配
			 * 匹配原理springSecurity 已经实现了，所以我们主要掌握他这种格式即可，如下：
			 */
			passwdEncode = "{"+MD5+"}"+"{"+salt+"}"+passwdEncode;
			//查询角色,并放入集合中
			List<MHRole> mhRoles = userService.queryRolesOfUserByUserId(user.getUserId() + "");
            boolean isAdmin = false;//是否是admin

			ArrayList<GrantedAuthority> ga = new ArrayList<GrantedAuthority>();
			for(int index=0;   mhRoles!=null&&mhRoles.size()>index;  index++){
				MHRole mhRole = mhRoles.get(index);
				String role = mhRole.getRoleId() + "_" + mhRole.getRoleName();//角色是由roleId_roleName 组成
				//判断是否是admin
				if(StringUtils.isNoneBlank(role)&&"1_admin".equalsIgnoreCase(role)){
			    	isAdmin = true;
				}
				SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
				ga.add(authority);
			}

			//查询role对应的menu菜单



			//返回User交给Security框架去认证
			return new User(username, passwdEncode, ga);
		}else{
			throw  new UsernameNotFoundException("用户账户或密码错误！！！");
		}
	}

}
