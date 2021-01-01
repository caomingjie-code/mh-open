package com.javaoffers.security.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import com.javaoffers.security.MHRole;
import org.springframework.stereotype.Service;

import com.javaoffers.security.MHUser;

@Service
public class UserService {

	@Resource
	UserMapper us;
	
	/**
	 * 根据登录名查询
	 * @param loginName
	 * @return
	 */
	public MHUser queryUserByLoginName(String loginName) {
		HashMap<String,Object> params = new HashMap<String, Object>();
		params.put("loginName", loginName);
		List<MHUser> list = us.queryDataForT3("queryUserByLoginName",params,MHUser.class);
		if(list==null||list.size()!=1) {
			return new MHUser();
		}
		return list.get(0);
	}

	/**
	 * 查询当前的用户所拥有的角色
	 * @param userId
	 * @return
	 */
	public List<MHRole> queryRolesOfUserByUserId(String userId){
		ArrayList<MHRole> mhRoles = new ArrayList<>();
		List<MHRole> mhRoles1 = us.queryDataForT("select\n" +
				"\tDISTINCT c.*\n" +
				"from mh_user a inner join mh_user_role b  on b.user_id = a.user_id\n" +
				"     inner join mh_role c on b.role_id = c.role_id\n" +
				"where 1=1 and a.user_id = '"+userId+"'", MHRole.class);
		if(mhRoles1!=null) {
			mhRoles.addAll(mhRoles1);
		}
		return mhRoles;
	}
	
}
