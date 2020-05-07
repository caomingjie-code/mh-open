package com.mh.base.utils.datasource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mh.base.exception.BaseDataSourceException;
import com.mh.base.properties.DataSourceMaster;
import com.mh.base.properties.DataSourceSlave;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.SQLException;

/**
 * 数据源构造
 */
@ConditionalOnSingleCandidate(DataSourceMaster.class)
@EnableConfigurationProperties({DataSourceMaster.class,DataSourceSlave.class})
@Configuration
public class DataSourceSlaveConfig {

    @Bean("dataSourceMater")
    public DataSource createDataSourceSlave(DataSourceMaster dataSourceMaster,DataSourceSlave dataSourceSlave) throws Exception {
    	BaseComboPooledDataSource dataSourceOfC3p0 = getDataSourceOfC3p0(dataSourceMaster);//Master
    	//初始化slave
    	initSlave(dataSourceSlave, dataSourceOfC3p0);
    	
    	return dataSourceOfC3p0;
    }

    /**
     * 初始化Slave
     * @param dataSourceMaster
     * @param dataSourceOfC3p0
     * @throws PropertyVetoException
     * @throws SQLException 
     */
	private void initSlave(DataSourceSlave dataSourceMaster, BaseComboPooledDataSource dataSourceOfC3p0)
			throws PropertyVetoException, SQLException {
		
		BaseComboPooledDataSource dataSourceSlaveOfC3p02 = getDataSourceSlaveOfC3p0(dataSourceMaster);//Slave
    	if(dataSourceSlaveOfC3p02==null) {
    		return;
    	}
		dataSourceOfC3p0.addDataSourceSlaves(dataSourceSlaveOfC3p02.getSlavename(), dataSourceSlaveOfC3p02);
	}

    /**
     * 获取c3p0的数据源
     * @return
     * @throws PropertyVetoException
     */
    private BaseComboPooledDataSource getDataSourceOfC3p0(DataSourceMaster dataSourceMaster) throws PropertyVetoException {
        String url = dataSourceMaster.getDatasource_master().getUrl();
        String username = dataSourceMaster.getDatasource_master().getUsername();
        String password = dataSourceMaster.getDatasource_master().getPassword();
        String driver = dataSourceMaster.getDatasource_master().getDriver();
        BaseComboPooledDataSource cpds = new BaseComboPooledDataSource();
        cpds.setDriverClass( driver); //loads the jdbc driver
        cpds.setJdbcUrl( url );
        cpds.setUser(username);
        cpds.setPassword(password);
        cpds.setMaxPoolSize(100);
        cpds.setMinPoolSize(10);
        return cpds;
    }
    
    /**
     * 获取c3p0的数据源
     * @return
     * @throws PropertyVetoException
     */
    private BaseComboPooledDataSource getDataSourceSlaveOfC3p0(DataSourceSlave dataSourceSlave) throws PropertyVetoException,SQLException {
    	if(dataSourceSlave.getDatasource_slaves()==null||StringUtils.isBlank(dataSourceSlave.getDatasource_slaves().getUrl())){return null;} 
    	String url = dataSourceSlave.getDatasource_slaves().getUrl();
        String username = dataSourceSlave.getDatasource_slaves().getUsername();
        String password = dataSourceSlave.getDatasource_slaves().getPassword();
        String driver = dataSourceSlave.getDatasource_slaves().getDriver();
        String slavename = dataSourceSlave.getDatasource_slaves().getSlavename();
        if(!StringUtils.isNoneBlank(slavename)) {
        	throw BaseDataSourceException.getException("slave name is null");
        }
        BaseComboPooledDataSource cpds = new BaseComboPooledDataSource();
        cpds.setDriverClass( driver); //loads the jdbc driver
        cpds.setJdbcUrl( url );
        cpds.setUser(username);
        cpds.setPassword(password);
        cpds.setMaxPoolSize(100);
        cpds.setMinPoolSize(10);
        cpds.setSlavename(slavename);
        return cpds;
    }
}
