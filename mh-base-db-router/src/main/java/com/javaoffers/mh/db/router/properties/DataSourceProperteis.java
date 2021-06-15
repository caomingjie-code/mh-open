package com.javaoffers.mh.db.router.properties;

import com.mchange.v2.c3p0.cfg.C3P0Config;
import com.mchange.v2.c3p0.impl.C3P0Defaults;

import javax.sql.DataSource;

/**
 * create by cmj
 */
public class DataSourceProperteis {

	private String url;
    private String password;
    private String username;
    private String driver;
    private String slavename; //从数据库名成，依靠该名称可以进行切库
    private boolean readWriteSeparation ;//是否开启读写分离，该选项只能用在master配置项中，若使用该选项则不能使用readDataSources，二者在master中二选一
    private String readDataSources;//指定只允许读的数据源，该选项可以使用在任何一个数据源配置中。（多个用英文逗号隔开）

    private int acquireIncrement = C3P0Config.initializeIntPropertyVar("acquireIncrement", C3P0Defaults.acquireIncrement());
    private int acquireRetryAttempts = 100;
    private int acquireRetryDelay = C3P0Config.initializeIntPropertyVar("acquireRetryDelay", C3P0Defaults.acquireRetryDelay());
    private boolean autoCommitOnClose = C3P0Config.initializeBooleanPropertyVar("autoCommitOnClose", C3P0Defaults.autoCommitOnClose());
    private String automaticTestTable = C3P0Config.initializeStringPropertyVar("automaticTestTable", C3P0Defaults.automaticTestTable());
    private boolean breakAfterAcquireFailure = C3P0Config.initializeBooleanPropertyVar("breakAfterAcquireFailure", C3P0Defaults.breakAfterAcquireFailure());
    private int checkoutTimeout = 1000*30;//30秒检查超时
    private String connectionCustomizerClassName = C3P0Config.initializeStringPropertyVar("connectionCustomizerClassName", C3P0Defaults.connectionCustomizerClassName());
    private String connectionTesterClassName = C3P0Config.initializeStringPropertyVar("connectionTesterClassName", C3P0Defaults.connectionTesterClassName());
    private String contextClassLoaderSource = C3P0Config.initializeStringPropertyVar("contextClassLoaderSource", C3P0Defaults.contextClassLoaderSource());
    private boolean debugUnreturnedConnectionStackTraces = C3P0Config.initializeBooleanPropertyVar("debugUnreturnedConnectionStackTraces", C3P0Defaults.debugUnreturnedConnectionStackTraces());
    private String factoryClassLocation = C3P0Config.initializeStringPropertyVar("factoryClassLocation", C3P0Defaults.factoryClassLocation());
    private boolean forceIgnoreUnresolvedTransactions = C3P0Config.initializeBooleanPropertyVar("forceIgnoreUnresolvedTransactions", C3P0Defaults.forceIgnoreUnresolvedTransactions());
    private boolean forceSynchronousCheckins = C3P0Config.initializeBooleanPropertyVar("forceSynchronousCheckins", C3P0Defaults.forceSynchronousCheckins());
    private int idleConnectionTestPeriod = 1800;//每1800 秒检查所有连接池中的空闲连接
    private int initialPoolSize = C3P0Config.initializeIntPropertyVar("initialPoolSize", C3P0Defaults.initialPoolSize());
    private int maxAdministrativeTaskTime = C3P0Config.initializeIntPropertyVar("maxAdministrativeTaskTime", C3P0Defaults.maxAdministrativeTaskTime());
    private int maxConnectionAge = C3P0Config.initializeIntPropertyVar("maxConnectionAge", C3P0Defaults.maxConnectionAge());
    private int maxIdleTime = 1800;//最大空闲时间,1800 秒内未使用则连接被丢弃
    private int maxIdleTimeExcessConnections = C3P0Config.initializeIntPropertyVar("maxIdleTimeExcessConnections", C3P0Defaults.maxIdleTimeExcessConnections());
    private int maxPoolSize = C3P0Config.initializeIntPropertyVar("maxPoolSize", C3P0Defaults.maxPoolSize());
    private int maxStatements = C3P0Config.initializeIntPropertyVar("maxStatements", C3P0Defaults.maxStatements());
    private int maxStatementsPerConnection = C3P0Config.initializeIntPropertyVar("maxStatementsPerConnection", C3P0Defaults.maxStatementsPerConnection());
    private int minPoolSize = C3P0Config.initializeIntPropertyVar("minPoolSize", C3P0Defaults.minPoolSize());
    private String overrideDefaultPassword = C3P0Config.initializeStringPropertyVar("overrideDefaultPassword", C3P0Defaults.overrideDefaultPassword());
    private String overrideDefaultUser = C3P0Config.initializeStringPropertyVar("overrideDefaultUser", C3P0Defaults.overrideDefaultUser());
    private String preferredTestQuery = "select sysdate from dual";
    private boolean privilegeSpawnedThreads = C3P0Config.initializeBooleanPropertyVar("privilegeSpawnedThreads", C3P0Defaults.privilegeSpawnedThreads());
    private int propertyCycle = C3P0Config.initializeIntPropertyVar("propertyCycle", C3P0Defaults.propertyCycle());
    private int statementCacheNumDeferredCloseThreads = C3P0Config.initializeIntPropertyVar("statementCacheNumDeferredCloseThreads", C3P0Defaults.statementCacheNumDeferredCloseThreads());
    private boolean testConnectionOnCheckin = true;//获取connnection时测试是否有效
    private boolean testConnectionOnCheckout = C3P0Config.initializeBooleanPropertyVar("testConnectionOnCheckout", C3P0Defaults.testConnectionOnCheckout());
    private int unreturnedConnectionTimeout = C3P0Config.initializeIntPropertyVar("unreturnedConnectionTimeout", C3P0Defaults.unreturnedConnectionTimeout());
    private String userOverridesAsString = C3P0Config.initializeUserOverridesAsString();
    private boolean usesTraditionalReflectiveProxies = C3P0Config.initializeBooleanPropertyVar("usesTraditionalReflectiveProxies", C3P0Defaults.usesTraditionalReflectiveProxies());

    public int getAcquireIncrement() {
        return acquireIncrement;
    }

    public void setAcquireIncrement(int acquireIncrement) {
        this.acquireIncrement = acquireIncrement;
    }

    public int getAcquireRetryAttempts() {
        return acquireRetryAttempts;
    }

    public void setAcquireRetryAttempts(int acquireRetryAttempts) {
        this.acquireRetryAttempts = acquireRetryAttempts;
    }

    public int getAcquireRetryDelay() {
        return acquireRetryDelay;
    }

    public void setAcquireRetryDelay(int acquireRetryDelay) {
        this.acquireRetryDelay = acquireRetryDelay;
    }

    public boolean isAutoCommitOnClose() {
        return autoCommitOnClose;
    }

    public void setAutoCommitOnClose(boolean autoCommitOnClose) {
        this.autoCommitOnClose = autoCommitOnClose;
    }

    public String getAutomaticTestTable() {
        return automaticTestTable;
    }

    public void setAutomaticTestTable(String automaticTestTable) {
        this.automaticTestTable = automaticTestTable;
    }

    public boolean isBreakAfterAcquireFailure() {
        return breakAfterAcquireFailure;
    }

    public void setBreakAfterAcquireFailure(boolean breakAfterAcquireFailure) {
        this.breakAfterAcquireFailure = breakAfterAcquireFailure;
    }

    public int getCheckoutTimeout() {
        return checkoutTimeout;
    }

    public void setCheckoutTimeout(int checkoutTimeout) {
        this.checkoutTimeout = checkoutTimeout;
    }

    public String getConnectionCustomizerClassName() {
        return connectionCustomizerClassName;
    }

    public void setConnectionCustomizerClassName(String connectionCustomizerClassName) {
        this.connectionCustomizerClassName = connectionCustomizerClassName;
    }

    public String getConnectionTesterClassName() {
        return connectionTesterClassName;
    }

    public void setConnectionTesterClassName(String connectionTesterClassName) {
        this.connectionTesterClassName = connectionTesterClassName;
    }

    public String getContextClassLoaderSource() {
        return contextClassLoaderSource;
    }

    public void setContextClassLoaderSource(String contextClassLoaderSource) {
        this.contextClassLoaderSource = contextClassLoaderSource;
    }

    public boolean isDebugUnreturnedConnectionStackTraces() {
        return debugUnreturnedConnectionStackTraces;
    }

    public void setDebugUnreturnedConnectionStackTraces(boolean debugUnreturnedConnectionStackTraces) {
        this.debugUnreturnedConnectionStackTraces = debugUnreturnedConnectionStackTraces;
    }

    public String getFactoryClassLocation() {
        return factoryClassLocation;
    }

    public void setFactoryClassLocation(String factoryClassLocation) {
        this.factoryClassLocation = factoryClassLocation;
    }

    public boolean isForceIgnoreUnresolvedTransactions() {
        return forceIgnoreUnresolvedTransactions;
    }

    public void setForceIgnoreUnresolvedTransactions(boolean forceIgnoreUnresolvedTransactions) {
        this.forceIgnoreUnresolvedTransactions = forceIgnoreUnresolvedTransactions;
    }

    public boolean isForceSynchronousCheckins() {
        return forceSynchronousCheckins;
    }

    public void setForceSynchronousCheckins(boolean forceSynchronousCheckins) {
        this.forceSynchronousCheckins = forceSynchronousCheckins;
    }


    public int getIdleConnectionTestPeriod() {
        return idleConnectionTestPeriod;
    }

    public void setIdleConnectionTestPeriod(int idleConnectionTestPeriod) {
        this.idleConnectionTestPeriod = idleConnectionTestPeriod;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }

    public int getMaxAdministrativeTaskTime() {
        return maxAdministrativeTaskTime;
    }

    public void setMaxAdministrativeTaskTime(int maxAdministrativeTaskTime) {
        this.maxAdministrativeTaskTime = maxAdministrativeTaskTime;
    }

    public int getMaxConnectionAge() {
        return maxConnectionAge;
    }

    public void setMaxConnectionAge(int maxConnectionAge) {
        this.maxConnectionAge = maxConnectionAge;
    }

    public int getMaxIdleTime() {
        return maxIdleTime;
    }

    public void setMaxIdleTime(int maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public int getMaxIdleTimeExcessConnections() {
        return maxIdleTimeExcessConnections;
    }

    public void setMaxIdleTimeExcessConnections(int maxIdleTimeExcessConnections) {
        this.maxIdleTimeExcessConnections = maxIdleTimeExcessConnections;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getMaxStatements() {
        return maxStatements;
    }

    public void setMaxStatements(int maxStatements) {
        this.maxStatements = maxStatements;
    }

    public int getMaxStatementsPerConnection() {
        return maxStatementsPerConnection;
    }

    public void setMaxStatementsPerConnection(int maxStatementsPerConnection) {
        this.maxStatementsPerConnection = maxStatementsPerConnection;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public String getOverrideDefaultPassword() {
        return overrideDefaultPassword;
    }

    public void setOverrideDefaultPassword(String overrideDefaultPassword) {
        this.overrideDefaultPassword = overrideDefaultPassword;
    }

    public String getOverrideDefaultUser() {
        return overrideDefaultUser;
    }

    public void setOverrideDefaultUser(String overrideDefaultUser) {
        this.overrideDefaultUser = overrideDefaultUser;
    }

    public String getPreferredTestQuery() {
        return preferredTestQuery;
    }

    public void setPreferredTestQuery(String preferredTestQuery) {
        this.preferredTestQuery = preferredTestQuery;
    }

    public boolean isPrivilegeSpawnedThreads() {
        return privilegeSpawnedThreads;
    }

    public void setPrivilegeSpawnedThreads(boolean privilegeSpawnedThreads) {
        this.privilegeSpawnedThreads = privilegeSpawnedThreads;
    }

    public int getPropertyCycle() {
        return propertyCycle;
    }

    public void setPropertyCycle(int propertyCycle) {
        this.propertyCycle = propertyCycle;
    }

    public int getStatementCacheNumDeferredCloseThreads() {
        return statementCacheNumDeferredCloseThreads;
    }

    public void setStatementCacheNumDeferredCloseThreads(int statementCacheNumDeferredCloseThreads) {
        this.statementCacheNumDeferredCloseThreads = statementCacheNumDeferredCloseThreads;
    }

    public boolean isTestConnectionOnCheckin() {
        return testConnectionOnCheckin;
    }

    public void setTestConnectionOnCheckin(boolean testConnectionOnCheckin) {
        this.testConnectionOnCheckin = testConnectionOnCheckin;
    }

    public boolean isTestConnectionOnCheckout() {
        return testConnectionOnCheckout;
    }

    public void setTestConnectionOnCheckout(boolean testConnectionOnCheckout) {
        this.testConnectionOnCheckout = testConnectionOnCheckout;
    }

    public int getUnreturnedConnectionTimeout() {
        return unreturnedConnectionTimeout;
    }

    public void setUnreturnedConnectionTimeout(int unreturnedConnectionTimeout) {
        this.unreturnedConnectionTimeout = unreturnedConnectionTimeout;
    }

    public String getUserOverridesAsString() {
        return userOverridesAsString;
    }

    public void setUserOverridesAsString(String userOverridesAsString) {
        this.userOverridesAsString = userOverridesAsString;
    }

    public boolean isUsesTraditionalReflectiveProxies() {
        return usesTraditionalReflectiveProxies;
    }

    public void setUsesTraditionalReflectiveProxies(boolean usesTraditionalReflectiveProxies) {
        this.usesTraditionalReflectiveProxies = usesTraditionalReflectiveProxies;
    }

    public boolean isReadWriteSeparation() {
        return readWriteSeparation;
    }

    public boolean getReadWriteSeparation() {
        return readWriteSeparation;
    }

    public void setReadWriteSeparation(boolean readWriteSeparation) {
        this.readWriteSeparation = readWriteSeparation;
    }

    public String getReadDataSources() {
        return readDataSources;
    }

    public void setReadDataSources(String readDataSources) {
        this.readDataSources = readDataSources;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Integer getMinPoolSize() {
        return minPoolSize;
    }

    public void setMinPoolSize(Integer minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public String getSlavename() {
		return slavename;
	}

	public void setSlavename(String slavename) {
		this.slavename = slavename;
	}

	public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
