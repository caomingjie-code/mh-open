package com.mh.base.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.mh.base.dao.impl.FPage;

/**
 * @author cmj
 * 该项目的顶级接口
 * @param <T>
 * @param <ID>
 */
public interface BaseBatis<T,ID>{
	
    /*****************************************************************************/ 
	public void saveData(String sql);

	public void saveData(String sql,Map<String,String> map);
	
	public void deleteData(String sql);

	public void deleteData(String sql,Map<String,String> map);
	
	public void updateData(String sql);

	public void updateData(String sql,Map<String,String> map);
	
	public List<Map<String,Object>> queryData(String sql);

	public List<Map<String,Object>> queryData(String sql,Map<String,String> map);
	
	/*****************************************************************************/ 
	public <E> List<E> queryDataForT(String sql,Class<E> clazz);
	
	public <E> List<E> queryDataForT2(String sqlId,Class<E> clazz);
	
	public <E> List<E> queryDataForT3(String sqlId,Map<String,Object> map,Class<E> clazz);
	
	public <E> List<E> queryDataForTAndSort(String sqlId,Map<String,Object> map,Class<E> clazz,String mainModelFieldName);
	
	public <E> List<E> queryDataForTAndSort(String sql,Class<E> clazz,String mainModelFieldName);
	
	
    
	/*****************************************************************************/ 
	public Integer batchUpdate(String sql,List<Map<String,String>> paramMap );

	
	/*****************************************************************************/ 
	public  List<Map<String,Object>> executorSQL(String sqlId);
	
	public  List<Map<String,Object>> executorSQL(String sqlId,Map<String,Object> paramMap);
	
	/*****************************************************************************/ 
	public void insertSql(String sqlId);
	
	public void deleteSql(String sqlId);
	
	public void updateSql(String sqlId);
	
	public List<Map<String,Object>> selectSql(String sqlId);
	
	/*****************************************************************************/ 
    public void insertSql(String sqlId,Map<String,Object> paramMap);
	
	public void deleteSql(String sqlId,Map<String,Object> paramMap);
	
	public void updateSql(String sqlId,Map<String,Object> paramMap);
	
	public List<Map<String,Object>> selectSql(String sqlId,Map<String,Object> paramMap);
	
	/*****************************************************************************/ 
    public void insertSqlByPojo(String sqlId,Object pojo);
	
	public void deleteSqlByPojo(String sqlId,Object pojo);
	
	public void updateSqlByPojo(String sqlId,Object pojo);
	
	public List<Map<String,Object>> selectSqlByPojo(String sqlId,Object pojo);
	
	/*****************************************************************************/ 
//    public  List<T> executorSQL2ListPOJO(String sqlId);
//	
//	public  List<T> executorSQL2ListPOJO(String sqlId,Map<String,Object> paramMap);
	
	List<Map<String, Object>> executorSQLByPojo(String sqlId, Object pojo);
	/**分页**/
	
	public FPage selectSqlForFPage(String sqlId,FPage fpage);



	
	
}
