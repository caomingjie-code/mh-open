package com.javaoffers.base.db.router.sample.controller;

import com.javaoffers.base.db.router.sample.mapper.RouterMapper;
import com.javaoffers.mh.db.router.annotation.DataSourceRoute;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import java.util.HashMap;
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

    /**
     * 多重路由（非嵌套）
     */
    @RequestMapping("mulRouter")
    public Object mulRouter(){
        List<Map<String, Object>> base = routerMapper.defaultRouter();//base数据库
        List<Map<String, Object>> fit = routerMapper.uniqueRouter();//fit 数据库
        List<Map<String, Object>> exam = routerMapper.examRouter();//exam数据库

        HashMap<String, Object> map = new HashMap<>();
        map.put("base数据库",base);
        map.put("fit 数据库",fit);
        map.put("exam数据库",exam);

        return map;
    }

    /**
     * 多重路由（嵌套路由）fit -> exam -> fit
     */
    @RequestMapping("nestingRouter")
    @DataSourceRoute("slaveDS")
    @Transactional(rollbackFor = Exception.class)//同时支持多个数据源在同一个事务中，
    public Object nestingRouter(){
        List<Map<String, Object>> fit = routerMapper.uniqueRouter2();//fit 数据库
        List<Map<String, Object>> exam = routerMapper.examRouter2();//exam数据库
        List<Map<String, Object>> fit2 = routerMapper.uniqueRouter3();//fit 数据库
        HashMap<String, Object> map = new HashMap<>();
        map.put("fit 数据库 limit 1",fit);
        map.put("exam数据库",exam);
        map.put("fit 数据库 liimt 2,1",fit2);
        return map;
    }
}
