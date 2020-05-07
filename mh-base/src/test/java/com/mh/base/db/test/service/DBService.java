package com.mh.base.db.test.service;

import java.io.Serializable;

import org.springframework.data.repository.NoRepositoryBean;

import com.mh.base.dao.BaseJPA;
import com.mh.base.dao.BaseBatis;
import com.mh.base.db.test.mapping.TestData;

public interface DBService extends BaseJPA<TestData,Serializable>,BaseBatis<TestData,Serializable> {
   String BEANNAME="com.mh.base.db.test.service.DBService";
}
