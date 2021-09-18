package com.example.dynamicdatasource.controller;

import com.example.dynamicdatasource.config.datasource.ChooseDataSource;
import com.example.dynamicdatasource.config.datasource.DatasourceEnum;
import com.example.dynamicdatasource.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 */
@RestController
public class TestController {

    @Autowired
    private UserService userService;

    /**
     * 查询测试类
     */
    @ChooseDataSource(name = DatasourceEnum.READ)
    @GetMapping("queryTest")
    public Object queryTest() {
        return userService.list();
    }

}
