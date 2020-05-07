package com.mh.base.db.test.model;

import com.mh.base.annotation.model.BaseModel;
import com.mh.base.annotation.model.BaseUnique;

@BaseModel
public class CModelTest extends ParentsModel {

	@BaseUnique
	private Integer aid;
	
	private String ad1;
	
	

	public Integer getAid() {
		return aid;
	}

	public void setAid(Integer aid) {
		this.aid = aid;
	}

	public String getAd1() {
		return ad1;
	}

	public void setAd1(String ad1) {
		this.ad1 = ad1;
	}

	
	
}
