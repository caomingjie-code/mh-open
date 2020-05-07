package com.mh.base.db.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.mh.base.db.test.mapping.StudyMapperProxy;
import org.junit.Before;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.mh.base.annotation.cache.EnableCache;
import com.mh.base.annotation.cache.UnableCache;
import com.mh.base.annotation.datasource.DataSourceRoute;
import com.mh.base.dao.impl.FPage;
import com.mh.base.db.test.mapping.TestDaoI;
import com.mh.base.db.test.mapping.TestData;
import com.mh.base.db.test.model.AModelTest;
import com.mh.base.db.test.service.DBService;

@Controller
@RequestMapping("/")
@Transactional(rollbackOn = Exception.class)
public class BaseTest<E> {

	@Resource
	DBService bd;
	
	@Resource
	TestDaoI ti;

	@Resource
	StudyMapperProxy studyMapperProxy;
	
	@RequestMapping("getSpringBeans")
	@ResponseBody
	@EnableCache("getSpringBeans")
	public List<String> getSpringBeans(HttpServletRequest req) {
		ServletContext sc = req.getServletContext();
		ArrayList<String> list = new ArrayList<String>();
		WebApplicationContext spring = WebApplicationContextUtils.getWebApplicationContext(sc);
		String[] definitionNames = spring.getBeanDefinitionNames();
		for(String beanName : definitionNames) {
			System.out.println(beanName);
			list.add(beanName);
		}
		
		return list;
	}

	/*****************************************************************************************************************/
	/*****************************************************************************************************************/
	/************************************************************test*************************************************/
	@RequestMapping("testDb")
	@UnableCache("getSpringBeans")
	@ResponseBody
	public List<Map> testDb() {
		List<Map> queryDataFor = ti.queryDataFor();
		return queryDataFor;
	}
	
	@RequestMapping("testDb2")
	@ResponseBody
	public Object testDb2() {
		return bd.executorSQL("queryDataFor2");
	}
	
	//queryDataFor2
	
	@RequestMapping("executorSQL")
	@ResponseBody
	@EnableCache("executorSQL")
	public List<Map<String,Object>> testDb3() {
		HashMap<String,Object> param = new HashMap<String, Object>();
		param.put("id", "1");
		List<Map<String,Object>> list = bd.executorSQL("queryDataFor3",param);
		return list;
	}

	@RequestMapping("insertSql")
	@ResponseBody
	public void insertSql(String id) {
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("data","test");
		bd.insertSql("insertSql", param);
	}
	
	
//	@RequestMapping("testDeleteDb4")
//	@ResponseBody
//	public List<TestData> testDeleteDb4() {
//		List<TestData> list = bd.executorSQL2ListPOJO("selectAllTestData");
//		return list;
//	}
	
	@RequestMapping("testDeleteDb5")
	@ResponseBody
	public List<TestData> testDeleteDb5() {
		List<TestData> list = bd.findAll();
		return list;
	}
	@RequestMapping("testSql")
	@ResponseBody
	public List<TestData> testSql() {
		List<TestData> queryData = bd.queryDataForT("select * from test_data",TestData.class);
		return queryData;
	}
	@RequestMapping("testSql2")
	@ResponseBody
	public FPage testSql2(Integer pageNum) {
		HashMap<String,Object> param = new HashMap<String, Object>();
		FPage fPage = new FPage(pageNum,param);
		FPage page = bd.selectSqlForFPage("testSql2", fPage);
		return page;
	}
	@RequestMapping("testSql3")
	@ResponseBody
	public List testSql3() {
		String sql = "select a.data_id aid ,a.data1 ad1 ,a.data2 ad2 ,b.id bid ,b.data1 bd1 ,b.data2 bd2 from test_data a left join mh_data b on b.data2=a.data_id";
		List<AModelTest> queryData = bd.queryDataForT(sql,AModelTest.class);
		return queryData;
	}
	
	@RequestMapping("testSql4")
	@ResponseBody
	public String testSql4() {
		TestData testData = new TestData();
		testData.setData1("test");
		testData.setData2("test2");
		
		TestData testData2 = new TestData();
		testData2.setData1("2");
		testData2.setData2("2");
		testData2.setData3("2");
		testData2.setData4("2");
		
		bd.save(testData);
		bd.insertSqlByPojo("saveTestData", testData2);
		int i=1/0; //模拟错误异常
		return "success";
	}
	
	@RequestMapping("testSqlForSort")
	@ResponseBody
	public List testSqlForSort() {
		String sql = "select a.data_id aid ,a.data1 ad1 ,a.data2 ad2 ,b.id bid ,b.data1 bd1 ,b.data2 bd2 from test_data a left join mh_data b on b.data2=a.data_id";
		List<AModelTest> queryData = bd.queryDataForTAndSort(sql, AModelTest.class, "");
		return queryData;
	}
	
	@RequestMapping("testDataSourceRoute")
	@ResponseBody
	@DataSourceRoute("slaveDS")
	public List testDataSourceRoute() {
		List<Map<String,Object>> queryData = bd.queryData("select * from oper_assess_option limit 10");
		return queryData;
	}
	
	
}
