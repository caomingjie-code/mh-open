package com.mh.base.db.test.model;

import java.util.List;
import java.util.Set;

import com.mh.base.annotation.model.BaseModel;
import com.mh.base.annotation.model.BaseUnique;
/**
 *  select 
 *  a.id aid 
 *  ,a.data1 ad1 
 *  ,a.data2 ad2 
 *  ,b.id bid 
 *  ,b.data1 bd1 
 *  ,b.data2 bd2 
 *  from test_data a left join mh_data b on b.data2=a.id
 */
@BaseModel
public class AModelTest {

	@BaseUnique
	private Integer aid;
	
	private String ad1;
	
	private String ad2;
	
	private CModelTest ct;
	
	private List<BModelTest> listB;
	
	private Set<BModelTest> setB;
	
	private BModelTest[] btArray;
	
	private Long[]   bid;
	
	

	public Long[] getBid() {
		return bid;
	}

	public void setBid(Long[] bid) {
		this.bid = bid;
	}

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

	public String getAd2() {
		return ad2;
	}

	public void setAd2(String ad2) {
		this.ad2 = ad2;
	}

	
	public CModelTest getCt() {
		return ct;
	}

	public void setCt(CModelTest ct) {
		this.ct = ct;
	}

	public List<BModelTest> getListB() {
		return listB;
	}

	public void setListB(List<BModelTest> listB) {
		this.listB = listB;
	}

	public Set<BModelTest> getSetB() {
		return setB;
	}

	public void setSetB(Set<BModelTest> setB) {
		this.setB = setB;
	}

	public BModelTest[] getBtArray() {
		return btArray;
	}

	public void setBtArray(BModelTest[] btArray) {
		this.btArray = btArray;
	}
	
	
	
}
