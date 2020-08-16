package com.mh.test.mq.controller;

import com.mh.base.common.rpc.HttpClientUtils;
import com.mh.base.common.utils.Utils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mh.base.mq.utils.MHMQUtilds;

import java.util.HashMap;

@Controller
@RequestMapping("/BMQTest")
public class BMQTest {

	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);


	@RequestMapping("/sendMsg")
	@ResponseBody
	public void sendMsg(String data) {
		MHMQUtilds.sendMsg(data);
	}
	
	@RequestMapping("/recive")
	@ResponseBody
	public String recive() {
		Object processMsg = MHMQUtilds.processMsg();
		logger.info(processMsg.toString());
		return processMsg==null?"":processMsg.toString();
	}
	
	@RequestMapping("/sendMsgCustom")
	@ResponseBody
	public void sendMsgCustom(String data) {
		MHMQUtilds.sendMsg(data, "testQueue");
	}

	@Test
	public void test() throws Exception {
		String url = "http://localhost:8080/BMQTest/sendMsgCustom";
		for(int i=0;i<100;i++){
			HashMap<String, String> map = Utils.startBuildParam("data", i).endBuildStringParam();
			HttpClientUtils.postParamData(url,map);
		}

	}
}
