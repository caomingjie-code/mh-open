package com.javaoffers.mh.db.router.config;

import com.javaoffers.mh.db.router.aop.datasource.AdvisorDataSource;
import com.javaoffers.mh.db.router.datasource.DataSourceSlaveConfig;
import com.javaoffers.mh.db.router.properties.DataSourceMasterAndSlave;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: router config
 * @Auther: create by cmj on 2021/1/9 15:19
 */
@Configuration
public class DBRouterConfig {

    @Bean
    public AdvisorDataSource advisorDataSource(){
       return  new AdvisorDataSource();
    }

    @Bean
    public DataSourceSlaveConfig dataSourceSlaveConfig(){
       return new DataSourceSlaveConfig();
    }

    @Bean
    public DataSourceMasterAndSlave dataSourceMasterAndSlave(){
        return new DataSourceMasterAndSlave();
    }

}
