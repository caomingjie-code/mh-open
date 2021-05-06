package com.javaoffers.mh.db.router.datasource;
import com.javaoffers.base.common.annotation.conditional.ConditionalOnPropertyMustExists;
import com.javaoffers.mh.db.router.exception.BaseDataSourceException;
import com.javaoffers.mh.db.router.properties.DataSourceMasterAndSlave;
import com.javaoffers.mh.db.router.properties.DataSourceProperteis;
import com.sun.imageio.plugins.common.I18N;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 数据源构造
 * create by cmj
 */
@ConditionalOnSingleCandidate(DataSourceMasterAndSlave.class)
@EnableConfigurationProperties({DataSourceMasterAndSlave.class})
@Configuration
@ConditionalOnPropertyMustExists("spring.datasource_master.url")
public class DataSourceSlaveConfig {

    @Bean("dataSource")
    @ConditionalOnPropertyMustExists("spring.datasource_master.url")
    public DataSource createDataSourceSlave(DataSourceMasterAndSlave dataSourceMasterAndSlave) throws Exception {
        //Master
    	BaseComboPooledDataSource dataSourceOfC3p0Master = getDataSourceOfC3p0(dataSourceMasterAndSlave);
    	//初始化slave
    	initSlave(dataSourceMasterAndSlave, dataSourceOfC3p0Master);
    	//发布
        dataSourceOfC3p0Master.finish();

    	return dataSourceOfC3p0Master;
    }

    /**
     * 初始化Slave
     * @param dataSourceMasterAndSlave 用于解析slave数据源
     * @param dataSourceOfC3p0Master master数据源
     * @throws PropertyVetoException
     * @throws SQLException 
     */
	private void initSlave(DataSourceMasterAndSlave dataSourceMasterAndSlave, BaseComboPooledDataSource dataSourceOfC3p0Master)
			throws PropertyVetoException, SQLException {
		
		List<BaseComboPooledDataSource> dataSourceSlavesOfC3p02 = getDataSourceSlaveOfC3p0(dataSourceMasterAndSlave);//Slave
    	for(BaseComboPooledDataSource slave : dataSourceSlavesOfC3p02){
            dataSourceOfC3p0Master.addDataSourceSlaves(slave.getRouterName(), slave);
        }
    	//初始化数据库配置
        DataSourceProperteis datasource_master = dataSourceMasterAndSlave.getDatasource_master();
        List<DataSourceProperteis> datasource_slaves = dataSourceMasterAndSlave.getDatasource_slaves();
        dataSourceOfC3p0Master.putDataSourcesProperties(dataSourceOfC3p0Master.DEFAULT_ROUTER,datasource_master);
        if (datasource_slaves!=null&&datasource_slaves.size()>0){
            for(DataSourceProperteis dataSourceProperteis : datasource_slaves ){
                dataSourceOfC3p0Master.putDataSourcesProperties(dataSourceProperteis.getSlavename(),dataSourceProperteis);
            }
        }
    }

    /**
     * 获取c3p0的数据源
     * @return
     * @throws PropertyVetoException
     */
    private BaseComboPooledDataSource getDataSourceOfC3p0(DataSourceMasterAndSlave dataSourceMaster) throws PropertyVetoException {
        DataSourceProperteis datasourceMaster = dataSourceMaster.getDatasource_master();
        BaseComboPooledDataSource cpds = initDatabases(datasourceMaster);


        return cpds;
    }

    private BaseComboPooledDataSource initDatabases(DataSourceProperteis dataSourceProperteis) throws PropertyVetoException {
        String url = dataSourceProperteis.getUrl();
        url = initUrl(url);
        String username = dataSourceProperteis.getUsername();
        String password = dataSourceProperteis.getPassword();
        String driver = dataSourceProperteis.getDriver();
        Integer maxPoolSize = dataSourceProperteis.getMaxPoolSize();
        maxPoolSize = maxPoolSize==null||maxPoolSize<50?50:maxPoolSize;//默认最大50
        Integer minPoolSize = dataSourceProperteis.getMinPoolSize();
        minPoolSize = minPoolSize==null||minPoolSize<10?10:minPoolSize; //默认最小10
        Integer initialPoolSize = minPoolSize;//最小线程池为初始化线程池的数量

        BaseComboPooledDataSource cpds = new BaseComboPooledDataSource();
        cpds.setDriverClass( driver); //loads the jdbc driver
        cpds.setJdbcUrl( url );
        cpds.setUser(username);
        cpds.setPassword(password);
        cpds.setMaxPoolSize(maxPoolSize);
        cpds.setMinPoolSize(minPoolSize);
        cpds.setInitialPoolSize(initialPoolSize);
        cpds.setIdleConnectionTestPeriod(1800);//每1800 秒检查所有连接池中的空闲连接
        cpds.setMaxIdleTime(1800);//最大空闲时间,1800 秒内未使用则连接被丢弃
        cpds.setAcquireRetryAttempts(100);//获取链接失败后重连次数
        cpds.setPreferredTestQuery("select sysdate from dual");//测死语句在执行sql时
        return cpds;
    }

    private String initUrl(String url) {
        //?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false&allowMultiQueries=true
        if(!url.contains("?")){
            url = url+"?";
        }
        url = addPropertiesForUrl(url,"useUnicode","true");
        url = addPropertiesForUrl(url,"characterEncoding","utf8");
        url = addPropertiesForUrl(url,"serverTimezone","GMT%2B8");
        url = addPropertiesForUrl(url,"useSSL","false");
        url = addPropertiesForUrl(url,"allowMultiQueries","true");
        return url;
    }

    private String addPropertiesForUrl(String url,String proper,String value){
        if(!url.contains(proper)){
            String endChar = url.substring(url.length() - 1, url.length());
            if("&".equalsIgnoreCase(endChar)){
                url = url+proper+"=" +value;
            }else{
                url = url+"&"+proper+"=" +value;
            }
        }
        return url;
    }

    /**
     * 获取c3p0的数据源
     * @return
     * @throws PropertyVetoException
     */
    private List<BaseComboPooledDataSource> getDataSourceSlaveOfC3p0(DataSourceMasterAndSlave dataSourceSlaves) throws PropertyVetoException,SQLException {
        LinkedList<BaseComboPooledDataSource> slaves = new LinkedList<>();
        if(dataSourceSlaves!=null&&dataSourceSlaves.getDatasource_slaves()!=null){
            for(DataSourceProperteis dataSourceSlave : dataSourceSlaves.getDatasource_slaves()){
                if(dataSourceSlave==null||StringUtils.isBlank(dataSourceSlave.getUrl())){return null;}

                String slavename = dataSourceSlave.getSlavename();
                if(!StringUtils.isNoneBlank(slavename)) {
                    throw BaseDataSourceException.getException("slave name is null");
                }
                BaseComboPooledDataSource cpds = initDatabases(dataSourceSlave);
                cpds.setRouterName(dataSourceSlave.getSlavename());
                slaves.add(cpds);
            }
        }
        return Collections.unmodifiableList(slaves);
    }
}
