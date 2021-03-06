package com.javaoffers.base.db.router.sample.controller;

import com.alibaba.fastjson.JSON;
import com.javaoffers.base.db.router.sample.mapper.RouterMapper;
import com.javaoffers.mh.db.router.annotation.DataSourceRoute;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Auther: create by cmj on 2021/1/11 00:36
 */
@Controller
@RequestMapping("/router/")
@ResponseBody
public class RouterController {
    @Resource
    RouterMapper routerMapper;

    /**
     * 默认数据源,即datasource_master配置中的数据源
     */
    @RequestMapping("defaultRouter")
    public Object defaultRouter(){
        List<Map<String, Object>> maps = routerMapper.defaultRouter();
        return maps;
    }

    /**
     * 默认数据源,即datasource_master配置中的数据源
     */
    @RequestMapping("defaultRouterWhile")
    public Object defaultRouterWhile(int num){
        LinkedList<Object> objects = new LinkedList<>();
        long start = System.currentTimeMillis();
        for(int i=0;i<num; i++){
            List<Map<String, Object>> maps = routerMapper.defaultRouter();
            if(maps!=null){
                objects.addAll(maps);
            }

        }
        long end = System.currentTimeMillis();

        return "耗时： "+TimeUnit.MILLISECONDS.toSeconds(end-start)+" 秒";
    }

    /**
     * 单路由
     */
    @RequestMapping("uniqueFitRouter")
    public Object uniqueFitRouter(){
        List<Map<String, Object>> maps = routerMapper.uniqueFitRouter();//路由到fit数据源，因该方法标有@DataSourceRoute("fit")
        return maps;
    }

    /**
     * 多重路由（非嵌套）
     */
    @RequestMapping("mulRouter")
    public Object mulRouter(){
        List<Map<String, Object>> base = routerMapper.defaultRouter();//base数据库，没有注解默认 datasource_master配置中的数据源
        List<Map<String, Object>> fit = routerMapper.uniqueFitRouter();//fit 数据库，标有注解 @DataSourceRoute("fit")
        List<Map<String, Object>> exam = routerMapper.examRouter();//exam数据库，标有注解 @DataSourceRoute("exam")

        HashMap<String, Object> map = new HashMap<>();
        map.put("base数据库",base);
        map.put("fit 数据库",fit);
        map.put("exam数据库",exam);

        return map;
    }

    @DataSourceRoute("fit")//进入此方法时默认使用fit数据源
    @RequestMapping("mulRouter2")
    public Object mulRouter2(String ok){

        HashMap<String, Object> fitUser = new HashMap<>();
        fitUser.put("name","xiao wang");
        routerMapper.saveFitData(fitUser);//  向fit 数据库插入一条数据， 因为该方法没有路由注解所以使用此方法的默认路由

        HashMap<String, Object> examUser = new HashMap<>();
        examUser.put("data","exam test data");
        routerMapper.saveExamData(examUser);// 向exam数据库插入一条数据，标有@DataSourceRoute("exam")

        List<Map<String, Object>> fit = routerMapper.queryFitData();// fit 查询 数据库， 因为该方法没有路由注解所以使用此方法的默认路由
        List<Map<String, Object>> exam = routerMapper.examRouter();//exam数据库，标有  @DataSourceRoute("exam")
        if(StringUtils.isNotBlank(ok)){
            if("error".equalsIgnoreCase(ok)){
                int e = 1/0;//模拟错误
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("fit 数据库 ",fit);
        map.put("exam数据库",exam);
        return JSON.toJSONString(map);
    }



    /**
     * 多重路由（嵌套路由）fit -> exam -> fit
     */
    @RequestMapping("nestingRouter")
    @DataSourceRoute("fit")//进入此方法时默认使用fit数据源
    @Transactional(rollbackFor = Exception.class)//同时支持多个数据源在同一个事务中
    public Object nestingRouter(String ok){

        HashMap<String, Object> fitUser = new HashMap<>();
        fitUser.put("name","xiao wang");
         routerMapper.saveFitData(fitUser);//  向fit 数据库插入一条数据， 因为该方法没有路由注解所以使用此方法的默认路由

        HashMap<String, Object> examUser = new HashMap<>();
        examUser.put("data","exam test data");
         routerMapper.saveExamData(examUser);// 向exam数据库插入一条数据，标有@DataSourceRoute("exam")

        List<Map<String, Object>> fit = routerMapper.queryFitData();// fit 查询 数据库， 因为该方法没有路由注解所以使用此方法的默认路由
        List<Map<String, Object>> exam = routerMapper.examRouter();//exam数据库，标有  @DataSourceRoute("exam")
        if(StringUtils.isNotBlank(ok)){
            if("error".equalsIgnoreCase(ok)){
                int e = 1/0;//模拟错误
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("fit 数据库 ",fit);
        map.put("exam数据库",exam);
        return JSON.toJSONString(map);
    }



}
class PersonTest{
    private String name;
    private String age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public PersonTest(String name, String age) {
        this.name = name;
        this.age = age;
    }

    public PersonTest() {
    }
}