package com.javaoffers.security.authentication;

import com.javaoffers.base.common.json.JsonUtils;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;

/**
 * 登陆认证成功后的处理，springSecurity 默认是重定向到successForwardUrl 指定的页面，现在我们
 * 不需要重定向，而是响应信息给浏览器，使浏览器收到认证成功的消息后自动触发跳转
 */
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    /**
     * 认证成功后跳转
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @SneakyThrows
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Object details = authentication.getDetails();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Object principal = authentication.getPrincipal();
        Object credentials = authentication.getCredentials();
        String name = authentication.getName();
        PrintWriter writer = response.getWriter();
        //支持ajax响应
        HashMap<String, String> map = new HashMap<>();
        map.put("msg","200");
        String s = JsonUtils.toJSONString(map).toString();
        writer.println(s);
    }
}
