package com.javaoffers.security.utils;

import com.javaoffers.security.MHUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * create by cmj on 2020-04-04
 */
public class SecurityUtils {
    /**
     * 获取登陆名
     * @return
     */
    public static String getUserName(){
        return getAuthentication().getName();
    }

    /**
     * 获取登陆用户
     * @return
     */
    public static MHUser getMHUser(){
        HttpSession session = getHttpSession();
        MHUser user = (MHUser)session.getAttribute("user");
        return user;
    }

    /**
     * 获取Role
     * @return  String:  ID_ROLENAME 每一个Role是以 "角色Id"和"角色名称"中间用下划线分割
     */
    public static List<String> getRoles(){
        ArrayList<String> roles = new ArrayList<>();
        Collection<? extends GrantedAuthority> authorities = getAuthentication().getAuthorities();
        if(authorities!=null && authorities.size()>0 ){
            for(GrantedAuthority authority : authorities){
                String role = authority.getAuthority();
                roles.add(role);
            }
        }
        return  roles;
    }

    public static HttpSession getHttpSession() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getSession();
    }


    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
