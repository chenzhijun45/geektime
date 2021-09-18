package com.example.shardingjdbc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.shardingjdbc.model.User;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    List<User> queryTest();
}
