package com.javaoffers.security.service;

import java.io.Serializable;

import com.javaoffers.base.dao.BaseBatis;
import com.javaoffers.base.dao.BaseJPA;
import com.javaoffers.security.MHUser;

/**
 * 用户service
 * @author aa
 *
 */
public interface UserMapper extends BaseJPA<MHUser, Serializable>,BaseBatis<MHUser, Serializable>{

}
