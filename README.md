的# mh-open自己的一个开源架构项目

# MH-OPEN
#### 项目规范
- 项目名称： 必须以mh-base-xx 来标识项目的意思
- 包名名称： 必须以 com.javaoffers.mh.xx.xx..


## MH-BASE-DAO  为核心架构

- 发布版本：
```
1.0.1.2：
  添加sql 占位符号 和类型转换功，添加新的方法queryDataForT4(String sql, Map<String, Object> map, Class<E> clazz)
```





## MH-BASE-MQ 消息队列给予raabbitMQ


## MH-BASE-DB-ROUTER 数据路由

- 入门简单使用

- 高级使用 
    1. readWriteSeparation: ture  开启读写分离
    
    2. readDataSources: slavename1, slavename2, slavename3  指定读数据库，多个默认使用轮寻策略

-版本发布：
```
1.0.3.1：
  修改none connection
1.0.3.2:
  防止 (in deadlocked PoolThread) failed to complete in maximum time 60000ms. Trying interrupt()  
  补全所有数据库参数
```    
   