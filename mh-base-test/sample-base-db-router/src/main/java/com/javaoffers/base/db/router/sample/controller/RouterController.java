package com.javaoffers.base.db.router.sample.controller;

import com.javaoffers.base.db.router.sample.mapper.RouterMapper;
import com.javaoffers.mh.db.router.annotation.DataSourceRoute;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Auther: create by cmj on 2021/1/11 00:36
 */
@Controller("/router/")
@ResponseBody
public class RouterController {
    @Resource
    RouterMapper routerMapper;

    /**
     * 默认路由
     */
    @RequestMapping("defaultRouter")
    public Object defaultRouter(){
        List<Map<String, Object>> maps = routerMapper.defaultRouter();
        return maps;
    }

    /**
     * 单路由
     */
    @RequestMapping("uniqueRouter")
    public Object uniqueRouter(){
        List<Map<String, Object>> maps = routerMapper.uniqueRouter();
        return maps;
    }
}
