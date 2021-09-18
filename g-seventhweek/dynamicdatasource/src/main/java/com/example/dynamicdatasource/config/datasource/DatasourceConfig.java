//package com.example.dynamicdatasource.config.datasource;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 数据源配置类 不支持权重
// * DatasourceConfig + DynamicDatasource = 不支持权重
// */
//@Component
//@Configuration
//public class DatasourceConfig {
//
//    //读数据源名称统一前缀
//    private static final String SLAVER = "slaver";
//
//    /**
//     * 主数据源（写库）
//     */
//    @Bean(name = "masterDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.druid.master")
//    public DataSource masterDataSource() {
//        return DataSourceBuilder.create()
//                .type(com.alibaba.druid.pool.DruidDataSource.class)
//                .build();
//    }
//
//    /**
//     * 从数据源1（读库1）
//     */
//    @Bean(name = SLAVER + 0)
//    @ConfigurationProperties(prefix = "spring.datasource.druid.slaver01")
//    public DataSource slaver01DataSource() {
//        return DataSourceBuilder.create()
//                .type(com.alibaba.druid.pool.DruidDataSource.class)
//                .build();
//    }
//
//    /**
//     * 从数据源2（读库2）
//     */
//    @Bean(name = SLAVER + 1)
//    @ConfigurationProperties(prefix = "spring.datasource.druid.slaver02")
//    public DataSource slaver02DataSource() {
//        return DataSourceBuilder.create()
//                .type(com.alibaba.druid.pool.DruidDataSource.class)
//                .build();
//    }
//
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    @Bean
//    @Primary//默认
//    public DynamicDatasource dynamicDatasource() {
//        //map保存所有数据源 写 + 读
//        Map<Object, Object> map = new HashMap<>(8);
//        map.put(DatasourceEnum.WRITE.name(), masterDataSource());
//        //获取所有数据源
//        Map<String, DataSource> dataSourceMap = applicationContext.getBeansOfType(DataSource.class);
//        int read = 0;
//        for (Map.Entry<String, DataSource> entry : dataSourceMap.entrySet()) {
//            if (entry.getKey().startsWith(SLAVER)) {
//                //获取所有读数据源
//                map.put(DatasourceEnum.READ.name() + read++, entry.getValue());
//            }
//        }
//
//        return new DynamicDatasource(masterDataSource(), map);
//    }
//
//}
