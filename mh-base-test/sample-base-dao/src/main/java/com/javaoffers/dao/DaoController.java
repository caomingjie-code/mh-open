package com.javaoffers.dao;
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
 * @Auther: create by cmj on 2020/8/28 15:31
 */
@Controller
@RequestMapping
@Transactional(rollbackFor = Exception.class)
public class DaoController {

    @Resource
    DaoService daoService;

    @RequestMapping("/testRuteDataBases")
    @ResponseBody
    public List testRuteDataBases(){
        List<Map<String, Object>> maps = daoService.queryData("select * from test limit 2");
        maps = daoService.queryData("select * from test limit 2,2");

        return maps;
    }

    @RequestMapping("/testqueryDataForT4")
    @ResponseBody
    public List testqueryDataForT4(){
        HashMap<String, Object> params = new HashMap<String, Object>();
        List<EntryTest> maps = daoService.queryDataForT4("select * from test limit 10", params, EntryTest.class);
        return maps;
    }

    @RequestMapping("/update")
    @ResponseBody
    public String update(){
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("id","2");
        daoService.updateData("update  test set `value` = '2' where id= #{id}", params);
        return "ok";
    }

}
