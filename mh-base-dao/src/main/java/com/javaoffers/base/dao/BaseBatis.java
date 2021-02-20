package com.javaoffers.base.dao;

import java.util.List;
import java.util.Map;

import com.javaoffers.base.dao.impl.FPage;

/**
 * @author cmj
 * 该项目的顶级接口
 * @param <T>
 * @param <ID>
 */
public interface BaseBatis<T,ID>{
	
    /*****************************************************************************/ 
	public int saveData(String sql);

	public int saveData(String sql,Map<String,Object> map);
	
	public int deleteData(String sql);

	public int deleteData(String sql,Map<String,Object> map);
	
	public int updateData(String sql);

	public int updateData(String sql,Map<String,Object> map);
	
	public List<Map<String,Object>> queryData(String sql);

	public List<Map<String,Object>> queryData(String sql,Map<String,Object> map);
	
	/*****************************************************************************/ 
	public <E> List<E> queryDataForT(String sql,Class<E> clazz);
	
	public <E> List<E> queryDataForT2(String sqlId,Class<E> clazz);
	
	public <E> List<E> queryDataForT3(String sqlId,Map<String,Object> map,Class<E> clazz);

	public <E> List<E> queryDataForT4(String sql,Map<String,Object> map,Class<E> clazz);

	public <E> List<E> queryDataForTAndSort(String sqlId,Map<String,Object> map,Class<E> clazz,String mainModelFieldName);
	
	public <E> List<E> queryDataForTAndSort(String sql,Class<E> clazz,String mainModelFieldName);

	/*****************************************************************************/ 
	public Integer batchUpdate(String sql,List<Map<String,Object>> paramMap );

	/*****************************************************************************/ 
	public  List<Map<String,Object>> executorSQL(String sqlId);
	
	public  List<Map<String,Object>> executorSQL(String sqlId,Map<String,Object> paramMap);
	
	/*****************************************************************************/ 
	public int insertSql(String sqlId);
	
	public int deleteSql(String sqlId);
	
	public int updateSql(String sqlId);
	
	public List<Map<String,Object>> selectSql(String sqlId);
	
	/*****************************************************************************/ 
    public int insertSql(String sqlId,Map<String,Object> paramMap);
	
	public int deleteSql(String sqlId,Map<String,Object> paramMap);
	
	public int updateSql(String sqlId,Map<String,Object> paramMap);
	
	public List<Map<String,Object>> selectSql(String sqlId,Map<String,Object> paramMap);
	
	/*****************************************************************************/ 
    public int insertSqlByPojo(String sqlId,Object pojo);
	
	public int deleteSqlByPojo(String sqlId,Object pojo);
	
	public int updateSqlByPojo(String sqlId,Object pojo);
	
	public List<Map<String,Object>> selectSqlByPojo(String sqlId,Object pojo);
	
	/*****************************************************************************/
	
	List<Map<String, Object>> executorSQLByPojo(String sqlId, Object pojo);

	/**分页**/
	public FPage selectSqlForFPage(String sqlId,FPage fpage);



	
	
}
