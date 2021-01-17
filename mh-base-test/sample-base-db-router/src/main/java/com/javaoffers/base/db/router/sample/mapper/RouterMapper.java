package com.javaoffers.base.db.router.sample.mapper;

import com.javaoffers.mh.db.router.annotation.DataSourceRoute;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Auther: create by cmj on 2021/1/10 16:19
 */
@Transactional(rollbackFor = Exception.class)
public interface RouterMapper {

    @DataSourceRoute("fit")
    public List<Map<String,Object>> uniqueFitRouter();

    List<Map<String, Object>> defaultRouter();

    @DataSourceRoute("exam")
    List<Map<String, Object>> examRouter();

    public List<Map<String,Object>> saveFitData(Map<String,Object> param);

    @DataSourceRoute("exam")
    List<Map<String, Object>> saveExamData( HashMap<String, Object> examUser);

    public List<Map<String,Object>> queryFitData();

}
