package com.mh.base.pojo;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.*;


/**
 * 实体类的基类，提供基础字段
 * @author cmj
 * 
 */
@Inheritance
public class BasePojo {
    
	@Column(name = "create_time",columnDefinition="datetime comment '创建时间'")
    private Date createTime;//创建时间
	
	@Column(name = "update_time", columnDefinition = "datetime comment '更新时间'")
    private Date updateTime;//更新时间
	
	@Column(name = "base_user1",columnDefinition = "varchar(300) comment '备用字段1'")
    private String baseUse1;

    @Column(name = "base_user2",columnDefinition = "varchar(300) comment '备用字段2'")
    private String baseUse2;

    @Column(name = "base_user3",columnDefinition = "varchar(300) comment '备用字段3'")
    private String baseUse3;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getBaseUse1() {
		return baseUse1;
	}

	public void setBaseUse1(String baseUse1) {
		this.baseUse1 = baseUse1;
	}

	public String getBaseUse2() {
		return baseUse2;
	}

	public void setBaseUse2(String baseUse2) {
		this.baseUse2 = baseUse2;
	}

	public String getBaseUse3() {
		return baseUse3;
	}

	public void setBaseUse3(String baseUse3) {
		this.baseUse3 = baseUse3;
	}
    
}
