package com.mh.test.eslog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description:
 * @Auther: create by cmj on 2020/8/19 22:41
 */
@Controller
public class EslogController {
    Logger logger = LoggerFactory.getLogger(EslogController.class);

    @RequestMapping("/eslogError")
    @ResponseBody
    public String eslogError(){
        int i = 1/0;
        return "success";
    }

    @RequestMapping("/eslogInfo")
    @ResponseBody
    public String eslogInfo(){
        logger.info(" es log test info");
        return "success";
    }

}
