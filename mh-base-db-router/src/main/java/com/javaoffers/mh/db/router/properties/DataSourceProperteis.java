package com.javaoffers.mh.db.router.properties;

public class DataSourceProperteis {
	private String url;
    private String password;
    private String username;
    private String driver;
    private String slavename; //从数据库名成，依靠该名称可以进行切库
    private Integer maxPoolSize;
    private Integer minPoolSize;

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
