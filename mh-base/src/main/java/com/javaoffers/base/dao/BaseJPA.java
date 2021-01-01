package com.javaoffers.base.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
@NoRepositoryBean
public interface BaseJPA<T,ID> extends JpaRepository<T,ID> {

}
