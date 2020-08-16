package com.mh.test.dao;

import com.mh.base.dao.BaseBatis;
import com.mh.base.dao.BaseJPA;

import java.io.Serializable;

/**
 * @Description:
 * @Auther: create by cmj on 2020/8/16 02:55
 */
public interface DaoService extends BaseBatis<EntryTest, Serializable>, BaseJPA<EntryTest, Serializable> {
}
