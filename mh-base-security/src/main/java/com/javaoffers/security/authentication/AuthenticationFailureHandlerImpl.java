package com.javaoffers.security.authentication;

import com.javaoffers.base.common.json.JsonUtils;
import lombok.SneakyThrows;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * 登陆认证失败后的处理： springSecurity默认是重定向到"/login?error"，
 * 如果不想重定向，则自己实现AuthenticationFailureHandler 进行处理
 */
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {
    @SneakyThrows
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //认证失败响应数据
        HashMap<String, String> map = new HashMap<>();
        map.put("msg","500");
        String json = JsonUtils.toJSONString(map);
        //现在支持Ajax 失败响应
        response.getWriter().println(json);
        return;
    }
}
