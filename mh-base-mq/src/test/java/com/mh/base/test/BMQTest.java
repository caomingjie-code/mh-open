package com.mh.base.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mh.base.mq.utils.MHMQUtilds;

@Controller
@RequestMapping("/BMQTest")
public class BMQTest {
	
	

	@RequestMapping("/sendMsg")
	@ResponseBody
	public void sendMsg(String data) {
		MHMQUtilds.sendMsg(data);
	}
	
	@RequestMapping("/recive")
	@ResponseBody
	public String recive() {
		Object processMsg = MHMQUtilds.processMsg();
		System.out.println(processMsg);
		return processMsg==null?"":processMsg.toString();
	}
	
	@RequestMapping("/sendMsgCustom")
	@ResponseBody
	public void sendMsgCustom(String data) {
		MHMQUtilds.sendMsg(data, "testQueue");
	}
	
}
