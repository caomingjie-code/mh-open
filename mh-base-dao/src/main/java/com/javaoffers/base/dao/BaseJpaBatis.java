package com.javaoffers.base.dao;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.config.JpaRepositoryCombination;

/**
 * @Description: 整合 jpa batis. 如果两者都使用的情况下，则子接口直接实现BaseJpaBatis
 * @Auther: create by cmj on 2021/2/22 09:45
 */
@NoRepositoryBean
@JpaRepositoryCombination
public interface BaseJpaBatis<T,ID> extends  BaseJPA<T,ID>,BaseBatis<T,ID> {
}
