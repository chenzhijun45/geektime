package com.example.dynamicdatasource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = {"com.example.dynamicdatasource.mapper"})
public class DynamicdatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicdatasourceApplication.class, args);
    }

}
