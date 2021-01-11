package com.javaoffers.base.db.router.sample.mapper;

import com.javaoffers.mh.db.router.annotation.DataSourceRoute;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Auther: create by cmj on 2021/1/10 16:19
 */
@Transactional(rollbackFor = Exception.class)
public interface RouterMapper {

    @DataSourceRoute("slaveDS")
    public List<Map<String,Object>> uniqueRouter();

    List<Map<String, Object>> defaultRouter();

    @DataSourceRoute("exam")
    List<Map<String, Object>> examRouter();

    public List<Map<String,Object>> uniqueRouter2();

    @DataSourceRoute("exam")
    List<Map<String, Object>> examRouter2();

    public List<Map<String,Object>> uniqueRouter3();

}
