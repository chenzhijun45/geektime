package com.example.dynamicdatasource.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源配置类 支持权重
 * 与不支持权重相比，感觉写的很难看，主要是不知道如何给数据源添加自定义 weight（权重） 属性
 * 导致需要从各个地方获取信息
 * DynamicDatasourceWithWeight + DatasourceConfigWithWeight = 支持权重
 */
@Component
@Configuration
public class DatasourceConfigWithWeight {

    //读数据源名称统一前缀
    private static final String SLAVER = "slaver";
    //写数据源名称
    public static final String WRITE = "masterDataSource";

    /**
     * 主数据源（写库）
     */
    @Bean(name = WRITE)
    @ConfigurationProperties(prefix = "spring.datasource.druid.master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create()
                .type(com.alibaba.druid.pool.DruidDataSource.class)
                .build();
    }

    //该map的key为读数据源名称 value为读数据源的权重
    public static final Map<String, Integer> weightMap = new HashMap<>(8);
    //读1数据源名称 单独拎出来 是为了防止拼写错误以及方便修改
    private static final String slaver01DataSourceName = SLAVER + 0;
    //读2数据源名称 单独拎出来 是为了防止拼写错误以及方便修改
    private static final String slaver02DataSourceName = SLAVER + 1;

    /**
     * 说明：最好的办法应该是
     */
    @Value("${spring.datasource.druid.slaver01.weight}")
    private Integer salver01Weight;
    @Value("${spring.datasource.druid.slaver02.weight}")
    private Integer salver02Weight;

    /**
     * 从数据源1（读库1）
     */
    @Bean(name = slaver01DataSourceName)
    @ConfigurationProperties(prefix = "spring.datasource.druid.slaver01")
    public DataSource slaver01DataSource() {
        DruidDataSource slaverDataSource = DataSourceBuilder.create()
                .type(DruidDataSource.class)
                .build();
        //假如这里可以直接把 salver01Weight 赋值到DataSource就好了 就不用如此麻烦了
        weightMap.put(slaver01DataSourceName, salver01Weight);
        return slaverDataSource;
    }

    /**
     * 从数据源2（读库2）
     */
    @Bean(name = slaver02DataSourceName)
    @ConfigurationProperties(prefix = "spring.datasource.druid.slaver02")
    public DataSource slaver02DataSource() {
        DruidDataSource slaverDataSource = DataSourceBuilder.create()
                .type(DruidDataSource.class)
                .build();
        //假如这里可以直接把 salver01Weight 赋值到DataSource就好了 就不用如此麻烦了
        weightMap.put(slaver02DataSourceName, salver02Weight);
        return slaverDataSource;
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @Primary//默认
    public DynamicDatasourceWithWeight dynamicDatasource() {
        //map保存所有数据源 写 + 读
        Map<Object, Object> map = new HashMap<>(8);
        //获取所有数据源
        map.putAll(applicationContext.getBeansOfType(DataSource.class));
        return new DynamicDatasourceWithWeight(masterDataSource(), map);
    }

}
