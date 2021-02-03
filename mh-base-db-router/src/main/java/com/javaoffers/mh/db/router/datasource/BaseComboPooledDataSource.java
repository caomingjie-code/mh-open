package com.javaoffers.mh.db.router.datasource;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;
import javax.naming.Referenceable;
import javax.sql.DataSource;

import com.javaoffers.mh.db.router.aop.datasource.RouterConnection;
import com.javaoffers.mh.db.router.common.ConcurrentMapSet;
import com.javaoffers.mh.db.router.exception.BaseDataSourceException;
import com.javaoffers.mh.db.router.properties.DataSourceProperteis;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.mchange.v2.c3p0.AbstractComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据源Master , 该master包含slave
 * crete by cmj
 */
final public class BaseComboPooledDataSource extends AbstractComboPooledDataSource implements Serializable, Referenceable {

    public static final String DEFAULT_ROUTER = "MASTER_ROUTER";
    //路由的名称
    private String routerName;
    //数据源的路由
    private static ThreadLocalNaming<ConcurrentLinkedDeque<String>> dataSourceRoute = new ThreadLocalNaming<ConcurrentLinkedDeque<String>>("DataSourceRouteThreadLocal");
    //将要clean，具体在close链接时使用
    private static ThreadLocalNaming<String> meanClean = new ThreadLocalNaming<>("DataSourceRouteThreadLocal"); //如果该值存在则说明此链接属于一路由链接并且数据源管理是JpaTransactionManager
    //存放子数据源
    private static ConcurrentHashMap<String, DataSource> dataSourceSlaves = new ConcurrentHashMap<String, DataSource>();
    //存放数据库的配置文件
    private static ConcurrentHashMap<String, DataSourceProperteis> dataSourcesProperteis = new ConcurrentHashMap<String, DataSourceProperteis>();
    //存放主从关系
    private static ConcurrentHashMap<String,String[]> masterMappingSlaves = null;
    //存放从主关系
    private static ConcurrentMapSet<String, String> slavesMappingMaster = new ConcurrentMapSet<>();

    Logger logger = LoggerFactory.getLogger(BaseComboPooledDataSource.class);

    private static AtomicLong ai = new AtomicLong(0);

    /**
     * 设置route
     *
     * @param dataSourceSlaveName
     * @throws SQLException
     */
    public static void pushStackRouter(String dataSourceSlaveName) throws SQLException {
        if (!StringUtils.isNoneBlank(dataSourceSlaveName)) {
            throw BaseDataSourceException.getException("DataSourceSlave Is Null");
        }
        ConcurrentLinkedDeque stack = dataSourceRoute.get();
        if (stack == null) {
            stack = new ConcurrentLinkedDeque();
            dataSourceRoute.set(stack);
        }
        stack.push(dataSourceSlaveName); //放入stack 顶部

    }

    /**
     * 获取栈顶元素，如果没有则返回null
     * @return
     */
    public static String getRouterSourceName_() {
        ConcurrentLinkedDeque<String> stackRouter = dataSourceRoute.get();
        if (stackRouter != null && stackRouter.size() > 0) {
            return stackRouter.peekFirst();
        }
        return null;
    }

    /**
     * 获取栈顶元素如果没有则返回master(默认)
     * @return
     */
    public static String getRouterSourceName() {
        String routerSourceName = BaseComboPooledDataSource.getRouterSourceName_();
        //判断是否属于路由链接，并且数据源管理为JpaTransactionManager
        if(StringUtils.isBlank(routerSourceName)){
            if(BaseComboPooledDataSource.isRouterConnection()){
                routerSourceName = BaseComboPooledDataSource.getMeanClean();
            }else {
                routerSourceName = BaseComboPooledDataSource.DEFAULT_ROUTER;
            }
        }
        return routerSourceName;
    }

    /**
     * 设置即将路由的数据源名称
     *
     * @param routerSourceName
     */
    public static void meanClean(String routerSourceName) {
        meanClean.set(routerSourceName);
    }

    /**
     * 判断是否是路由链接并且数据源管理为JpaTransactionManager
     *
     * @return
     */
    public static boolean isRouterConnection() {
        return meanClean.get() != null;
    }

    /**
     * 获取当前即将清除的链接名称，并且数据源管理为JpaTransactionManager
     *
     * @return
     */
    public static String getMeanClean() {
        return meanClean.get();
    }

    public BaseComboPooledDataSource() {
        super();
    }

    public BaseComboPooledDataSource(boolean autoregister) {
        super(autoregister);
    }

    public BaseComboPooledDataSource(String configName) {
        super(configName);
    }

    // serialization stuff -- set up bound/constrained property event handlers on deserialization
    private static final long serialVersionUID = 1;
    private static final short VERSION = 0x0002;

    public Connection getConnection() throws SQLException {
        //判断是否需要route
        Connection concurrentConnection = null;
        ConcurrentLinkedDeque<String> stackRouter = dataSourceRoute.get();
        String routerName = DEFAULT_ROUTER;
        if (stackRouter != null && stackRouter.size() > 0) {
            try {
                routerName = stackRouter.peekFirst();
                concurrentConnection = ((BaseComboPooledDataSource) dataSourceSlaves.get(routerName)).getSuperConnection();
            } catch (Exception e) {
                if(routerName.equals(stackRouter.peekFirst())){ //有可能是master.
                    //获取MasterConnection
                    concurrentConnection = super.getConnection();
                }else{
                    throw BaseDataSourceException.getException("config is error of data source slave [" + e.getMessage() + "]");
                }
            }
        } else {
            //获取MasterConnection，（开始事务时，不经过advisor,spring会直接获取一链接，因为在事务的场景下通常为写， 所以选取默认master.）
            concurrentConnection = super.getConnection();
        }
        //concurrentConnection.setAutoCommit(false);
        RouterConnection routerConnection = new RouterConnection(this);
        routerConnection.putConcurrentConnection(routerName, concurrentConnection);
        logger.info("opened jdbc connection id[" + concurrentConnection.hashCode() + "] counts : " + ai.addAndGet(1));
        return routerConnection;

    }



    /**
     * 清除陆游信息
     */
    public static String clean() {
        String pop = null;
        ConcurrentLinkedDeque<String> stack = dataSourceRoute.get();
        if (stack != null && stack.size() > 0) {
            pop = stack.pop();
        }
        if (stack == null || stack.size() == 0) {
            dataSourceRoute.set(null); // help gc
        }
        return pop;
    }

    public String getRouterName() {
        return routerName;
    }

    public void setRouterName(String routerName) {
        this.routerName = routerName;
    }

    public void putDataSourcesProperties(String dataSourceName, DataSourceProperteis sourceProperteis) throws BaseDataSourceException {
        if (StringUtils.isBlank(dataSourceName)) throw BaseDataSourceException.getException("dataSourceName is null");
        dataSourcesProperteis.put(dataSourceName, sourceProperteis);
    }

    /**
     * 获取读数据源
     * @param writeDataSourceName
     * @return
     */
    public static String[] getReadDataSource(String writeDataSourceName){

        String[] salves = masterMappingSlaves.get(writeDataSourceName);
        return salves==null?ArrayUtils.EMPTY_STRING_ARRAY:salves;
    }

    /**
     * 检查是否是是读数据源
     * @param dataSourceName
     * @return
     */
    public static boolean checkedIsReadDataSource(String dataSourceName){
        return slavesMappingMaster.containsKey(dataSourceName);
    }

    /**
     * 检查是否是写数据源
     * @param dataSourceName
     * @return
     */
    public static boolean checkedIsWriteDataSource(String dataSourceName){
        return  masterMappingSlaves.containsKey(dataSourceName);

    }

    /**
     * 获取写数据源
     * @param readDataSourceName
     * @return
     */
    public static String[] getWriteDataSource(String readDataSourceName){
        ConcurrentSkipListSet<String> wd = slavesMappingMaster.get(readDataSourceName);
        if(wd==null||wd.size()==0){
            return  ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return wd.toArray(new String[wd.size()]);
    }

    /**
     * 替换栈顶元素
     */
    public static String replaceFirstStackElement(String dataSourceName) throws SQLException {
        String topE = clean(); //弹出栈顶元素
        pushStackRouter(dataSourceName);//添加新元素
        return topE;
    }

    /**
     * 发布
     */
    public  void finish(){
        initCacheMasterAndSlaveMapper();
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeShort(VERSION);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        short version = ois.readShort();
        switch (version) {
            case VERSION:
                //ok
                break;
            default:
                throw new IOException("Unsupported Serialized Version: " + version);
        }
    }

    /**
     * 直接调取夫类方法获取连接,只给Slave调用
     *
     * @return
     * @throws SQLException
     */
    private Connection getSuperConnection() throws SQLException {
        Connection connection = super.getConnection();
        return connection;
    }

    /**
     * 初始化缓存
     */
    private void initCacheMasterAndSlaveMapper() {
        if(masterMappingSlaves==null){
            synchronized (BaseComboPooledDataSource.class){
                if(masterMappingSlaves==null){
                    masterMappingSlaves = new ConcurrentHashMap<>();
                    Set<Map.Entry<String, DataSourceProperteis>> entries = dataSourcesProperteis.entrySet();
                    for(Map.Entry<String, DataSourceProperteis> entry : entries){
                        String dataSourceName = entry.getKey();
                        DataSourceProperteis dp = entry.getValue();
                        if(dataSourceName!=null&&dp!=null){
                            String readDataSources = dp.getReadDataSources();
                            if(StringUtils.isNotBlank(readDataSources)){
                                String[] salves = readDataSources.replaceAll(" ", "").split(",");
                                if(salves!=null&&salves.length>0){
                                    masterMappingSlaves.put(dataSourceName,salves);
                                    for(String slave : salves){
                                        slavesMappingMaster.put(slave, dataSourceName);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * 解析并添加slave数据源
     *
     * @param dataSourceSlaveName
     * @param dataSourceSlave
     */
    protected void addDataSourceSlaves(String dataSourceSlaveName, DataSource dataSourceSlave) {
        dataSourceSlaves.put(dataSourceSlaveName, dataSourceSlave);
    }


}
