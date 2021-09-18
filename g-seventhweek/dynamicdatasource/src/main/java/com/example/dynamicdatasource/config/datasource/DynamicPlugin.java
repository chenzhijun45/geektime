//package com.example.dynamicdatasource.config.datasource;
//
//import org.apache.ibatis.executor.Executor;
//import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
//import org.apache.ibatis.mapping.BoundSql;
//import org.apache.ibatis.mapping.MappedStatement;
//import org.apache.ibatis.mapping.SqlCommandType;
//import org.apache.ibatis.plugin.*;
//import org.apache.ibatis.session.ResultHandler;
//import org.apache.ibatis.session.RowBounds;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.transaction.support.TransactionSynchronizationManager;
//
//import java.util.Locale;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Intercepts({
//        @Signature(type = Executor.class, method = "update", args = {
//                MappedStatement.class, Object.class}),
//        @Signature(type = Executor.class, method = "query", args = {
//                MappedStatement.class, Object.class, RowBounds.class,
//                ResultHandler.class})})
//public class DynamicPlugin implements Interceptor {
//
//    protected static final Logger logger = LoggerFactory.getLogger(DynamicPlugin.class);
//
//    private static final String REGEX = ".*insert\\u0020.*|.*delete\\u0020.*|.*update\\u0020.*";
//
//    private static final Map<String, DatasourceEnum> cacheMap = new ConcurrentHashMap<>();
//
//    @Override
//    public Object intercept(Invocation invocation) throws Throwable {
//
//        boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
//
//        if (!synchronizationActive) {
//            Object[] args = invocation.getArgs();
//            MappedStatement ms = (MappedStatement) args[0];
//
//            System.out.println("0==>>" + args[1]);
//
//            DatasourceEnum dataSourceEnum;
//
//            System.out.println("1==>>" + ms.getId());
//            //ms.getId()= Mapper + 方法 例如：com.example.database.mapper.UserMapper.insert、com.example.database.mapper.UserMapper.selectList
//            if ((dataSourceEnum = cacheMap.get(ms.getId())) == null) {
//                //读方法
//                if (ms.getSqlCommandType().equals(SqlCommandType.SELECT)) {
//                    //!selectKey 为自增id查询主键(SELECT LAST_INSERT_ID() )方法，使用主库
//                    if (ms.getId().contains(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
//                        dataSourceEnum = DatasourceEnum.WRITE;
//                    } else {
//                        BoundSql boundSql = ms.getSqlSource().getBoundSql(args[1]);
//                        System.out.println("3==>>" + boundSql.toString());
//                        System.out.println("3.5==>>" + boundSql.getSql());
//                        String sql = boundSql.getSql().toLowerCase(Locale.CHINA).replaceAll("[\\t\\n\\r]", " ");
//                        System.out.println("4.5==>>" + sql);
//                        if (sql.matches(REGEX)) {
//                            dataSourceEnum = DatasourceEnum.WRITE;
//                        } else {
//                            dataSourceEnum = DatasourceEnum.READ;
//                        }
//                    }
//                } else {
//                    //写方法
//                    dataSourceEnum = DatasourceEnum.WRITE;
//                }
//                logger.warn("设置方法[{}] use [{}] Strategy, SqlCommandType [{}]..", ms.getId(), dataSourceEnum.name(), ms.getSqlCommandType().name());
//                cacheMap.put(ms.getId(), dataSourceEnum);
//            }
//            DynamicDataSourceHolder.putDataSource(dataSourceEnum);
//        }
//        return invocation.proceed();
//    }
//
//
//    @Override
//    public Object plugin(Object target) {
//        if (target instanceof Executor) {
//            return Plugin.wrap(target, this);
//        } else {
//            return target;
//        }
//    }
//
//}
