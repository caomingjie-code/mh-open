package com.javaoffers.base.db.router.sample.controller;

import com.javaoffers.base.db.router.sample.mapper.RouterMapper;
import com.javaoffers.mh.db.router.annotation.DataSourceRoute;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description: 读写分离测试
 * @Auther: create by cmj on 2021/2/2 16:30
 */
@Controller
@RequestMapping("/readWrite")
@ResponseBody
public class ReadWriteRouterController {

    @Resource
    RouterMapper routerMapper;

    /**
     * 读写分离测试: master -> extends master = read -> extends master -> extends master = read
     * @return
     */
    @RequestMapping("readWriteSep")
    @Transactional(rollbackFor = Exception.class)//同时支持多个数据源在同一个事务中

    public Object readWriteSep(){
        List<Map<String, Object>> base = routerMapper.defaultRouter();//base数据库，没有注解默认 datasource_master配置中的数据源, 自动切为读数据库

        routerMapper.defaultWrite();//base数据库，没有注解默认 datasource_master配置中的数据源, 自动切为读数据库,自动切换为写（master为写）

        base = routerMapper.defaultRouter();//base数据库，没有注解默认 datasource_master配置中的数据源, 自动切为读数据库(再次切换为读)

        return base;
    }

    /**
     * 读写分离测试: 不含事务
     * @return
     */
    @RequestMapping("readWriteSepNonTransaction")
    public Object readWriteSepNonTransaction(){
        List<Map<String, Object>> base = routerMapper.defaultRouter();//base数据库，没有注解默认 datasource_master配置中的数据源, 自动切为读数据库

        routerMapper.defaultWrite();//base数据库，没有注解默认 datasource_master配置中的数据源, 自动切为读数据库,自动切换为写（master为写）

        base = routerMapper.defaultRouter();//base数据库，没有注解默认 datasource_master配置中的数据源, 自动切为读数据库(再次切换为读)

        return base;
    }


    /**
     * 读写分离测试: 不含事务,强制性路由，
     * @return
     */
    @RequestMapping("readWriteSepForce")
    public Object readWriteSepForce(){
        List<Map<String, Object>> base = routerMapper.defaultRouter();//base数据库，没有注解默认 datasource_master配置中的数据源, 自动切为读数据库

        routerMapper.defaultWrite();//base数据库，没有注解默认 datasource_master配置中的数据源, 自动切为读数据库,自动切换为写（master为写）

        base = routerMapper.forceRouter();//base数据库，不会切为读，因为注解有强制路由

        return base;
    }

}
