package test.controller;

import com.javaoffers.base.common.json.JsonUtils;
import com.javaoffers.base.common.log.LogUtils;
import com.javaoffers.security.MHUser;
import com.javaoffers.security.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/security/")
public class LoginController {
	private static Logger logger = LoggerFactory.getLogger(LoginController.class);// log4j记录日志

	/**
	 * 登录页面
	 * @return
	 */
	@RequestMapping("login")
	public String login() {
		return "login";
	}
	
	/**
	 * 登录认证成功后跳转
	 * @return
	 */
	@RequestMapping("main")
	public String main() throws Exception {
		//查询所有的权限
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();//账号
		//封装登陆名和权限
		User principal = (User)authentication.getPrincipal(); //User(org.springframework.security.core.userdetails.User)
		//封装ip地址和SessionId
		WebAuthenticationDetails details = (WebAuthenticationDetails)authentication.getDetails();//org.springframework.security.web.authentication.WebAuthenticationDetails: RemoteIpAddress: 127.0.0.1; SessionId: CD62A9530B7976AF72CAA68ECDE28334

		//获取用户对象
		MHUser mhUser = SecurityUtils.getMHUser();
		LogUtils.printLog(JsonUtils.toJSONString(mhUser).toString());

		//打印授予的权限
		List<String> roles = SecurityUtils.getRoles();
		LogUtils.printLog(roles.toString());



		return "main";
	}

	public LoginController() {
		super();
		logger.info("  LoginController  ");
		// TODO Auto-generated constructor stub
	}
	
	
	
}
