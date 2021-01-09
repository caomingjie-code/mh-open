package com.javaoffers.base.db.test.service;

import java.io.Serializable;

import com.javaoffers.base.dao.BaseJPA;
import com.javaoffers.base.dao.BaseBatis;
import com.javaoffers.base.db.test.mapping.TestData;

public interface DBService extends BaseJPA<TestData,Serializable>,BaseBatis<TestData,Serializable> {
   String BEANNAME="com.javaoffers.base.db.test.service.DBService";
}
