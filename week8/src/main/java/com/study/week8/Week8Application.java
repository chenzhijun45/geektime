package com.study.week8;

import com.study.week8.config.shardingjbdc.TransactionConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@MapperScan({"com.study.**.mapper"})
@SpringBootApplication
@Import(TransactionConfiguration.class)
public class Week8Application {

    public static void main(String[] args) {
        SpringApplication.run(Week8Application.class, args);
    }

}
