package com.javaoffers.security;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="mh_user")
@Getter
@Setter
public class MHUser  implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="user_id",columnDefinition="int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id'")
	private Integer userId;

	@Column(name="username",columnDefinition="varchar(30) DEFAULT NULL COMMENT '用户名'")
	private String userName;

	@Column(name="login_name",columnDefinition="varchar(30) DEFAULT NULL COMMENT '登录名（登录账号）'")
	private String loginName;

	@Column(name="password",columnDefinition="varchar(300) DEFAULT NULL COMMENT '登录密码加密后'")
	private String passWd;

	@Column(name="salt",columnDefinition="varchar(30) DEFAULT NULL COMMENT '盐加密'")
	private String salt;

	@Column(name="create_user_id",columnDefinition="varchar(30) DEFAULT NULL COMMENT '创建者id'")
	private String createUserId;
	@Column(name = "create_time",columnDefinition="datetime comment '创建时间'")
	private Date createTime;//创建时间

	@Column(name = "update_time", columnDefinition = "datetime comment '更新时间'")
	private Date updateTime;//更新时间


	public Integer getUserId() {
		return userId; 
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getPassWd() {
		return passWd;
	}
	public void setPassWd(String passWd) {
		this.passWd = passWd;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	
	
}
