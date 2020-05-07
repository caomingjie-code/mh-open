package com.mh.security.service;

import java.io.Serializable;

import com.mh.base.dao.BaseBatis;
import com.mh.base.dao.BaseJPA;
import com.mh.security.MHUser;

/**
 * 用户service
 * @author aa
 *
 */
public interface UserMapper extends BaseJPA<MHUser, Serializable>,BaseBatis<MHUser, Serializable>{

}
