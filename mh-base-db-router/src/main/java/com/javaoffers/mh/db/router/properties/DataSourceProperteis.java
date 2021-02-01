package com.javaoffers.mh.db.router.properties;

/**
 * create by cmj
 */
public class DataSourceProperteis {
	private String url;
    private String password;
    private String username;
    private String driver;
    private String slavename; //从数据库名成，依靠该名称可以进行切库
    private Integer maxPoolSize;
    private Integer minPoolSize;
    private boolean readWriteSeparation;//是否开启读写分离，该选项只能用在master配置项中，若使用该选项则不能使用readDataSources，二者在master中二选一
    private String readDataSources;//指定只允许读的数据源，该选项可以使用在任何一个数据源配置中。（多个用英文逗号隔开）

    public boolean isReadWriteSeparation() {
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
