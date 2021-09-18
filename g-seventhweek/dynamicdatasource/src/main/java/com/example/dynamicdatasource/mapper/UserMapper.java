package com.example.dynamicdatasource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dynamicdatasource.model.User;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    List<User> queryTest();
}
