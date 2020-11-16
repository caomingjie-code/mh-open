package com.mh.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description:
 * @Auther: create by cmj on 2020/11/16 23:40
 */
@Controller
@RequestMapping("/security")
public class LoginPageController {

    /**
     * 跳转到登录页面
     * @return
     */
    @GetMapping("/login")
    public String loginPage(){
        return "/security/login";
    }

    /**
     * 跳转到登录完成后的菜单页面
     * @return
     */
    @RequestMapping("/main")
    public String main(){
        return "/security/main";
    }

}
