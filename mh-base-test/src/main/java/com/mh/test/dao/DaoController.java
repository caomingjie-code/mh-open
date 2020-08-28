package com.mh.test.dao;

import com.mh.base.annotation.datasource.DataSourceRoute;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Auther: create by cmj on 2020/8/28 15:31
 */
@Controller
@RequestMapping
public class DaoController {

    @Resource
    DaoService daoService;

    @RequestMapping("/testRuteDataBases")
    @ResponseBody
    public List testRuteDataBases(){
        List<Map<String, Object>> maps = daoService.queryData("select * from test limit 2");
        return maps;
    }

    @RequestMapping("/testRuteDataBases2")
    @ResponseBody
    @DataSourceRoute("slaveDS")
    public List testRuteDataBases2(){
        List<Map<String, Object>> maps = daoService.queryData("select * from oper_cargo limit 2");
        return maps;
    }

}
