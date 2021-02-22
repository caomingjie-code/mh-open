package com.javaoffers.base.dao;

import com.javaoffers.base.pojo.NoneEntry;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * @Description: 整合 jpa batis. 但是jpa的功能基本是无效的，主要突出batis的功能
 * @Auther: create by cmj on 2021/2/22 09:50
 */
@NoRepositoryBean
public interface BaseBatisJpa  extends  BaseJPA<NoneEntry, Serializable>,BaseBatis<NoneEntry, Serializable>{

}
