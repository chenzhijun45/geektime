package com.example.dubboaccount;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@MapperScan({"com.example.dubbocommon.account.mapper"})
@ImportResource({"classpath:spring-dubbo-provider.xml"})
public class DubboAccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboAccountApplication.class, args);
    }

}
