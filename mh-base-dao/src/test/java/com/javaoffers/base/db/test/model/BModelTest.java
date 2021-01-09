package com.javaoffers.base.db.test.model;

import com.javaoffers.base.common.annotation.model.BaseModel;
import com.javaoffers.base.common.annotation.model.BaseUnique;

/**
 *  select 
 *  a.id aid 
 *  ,a.data1 ad1 
 *  ,a.data2 ad2 
 *  ,b.id bid 
 *  ,b.data1 bd1 
 *  ,b.data2 bd2 
 *  from test_data a left join mh_data b on b.data2=a.id
 * @author cmj
 *
 */
@BaseModel
public class BModelTest extends ParentsModel{

	@BaseUnique
	private Integer bid;
	
	private String bd1;
	
	private String bd2;

	public Integer getBid() {
		return bid;
	}

	public void setBid(Integer bid) {
		this.bid = bid;
	}

	public String getBd1() {
		return bd1;
	}

	public void setBd1(String bd1) {
		this.bd1 = bd1;
	}

	public String getBd2() {
		return bd2;
	}

	public void setBd2(String bd2) {
		this.bd2 = bd2;
	}
	
	
}
