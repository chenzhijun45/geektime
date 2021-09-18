package com.example.shardingjdbc;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//排除druid数据源配置 因为和sharding-jdbc的配置存在冲突
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class})
@MapperScan(value = {"com.example.shardingjdbc.mapper"})
public class ShardingjdbcApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShardingjdbcApplication.class, args);
    }

}
