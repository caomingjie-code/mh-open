package com.javaoffers.base.db.router.sample.controller;

import com.javaoffers.base.db.router.sample.mapper.NoneRouterMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
@Controller
@ResponseBody
@RequestMapping("/noneRouter/")
public class NoneRouterController {

    @Resource
    NoneRouterMapper noneRouterMapper;
    /**
     * 默认数据源,即datasource_master配置中的数据源
     */
    @RequestMapping("defaultRouterWhile")
    public Object defaultRouterWhile(int num){
        LinkedList<Object> objects = new LinkedList<>();
        long start = System.currentTimeMillis();
        for(int i=0;i<num; i++){
            List<Map<String, Object>> maps = noneRouterMapper.defaultRouter();
            objects.addAll(maps);
        }
        long end = System.currentTimeMillis();

        return "耗时： "+ TimeUnit.MILLISECONDS.toSeconds(end-start)+" 秒";
    }

}
