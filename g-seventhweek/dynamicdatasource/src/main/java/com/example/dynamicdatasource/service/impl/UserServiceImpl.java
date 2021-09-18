package com.example.dynamicdatasource.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dynamicdatasource.mapper.UserMapper;
import com.example.dynamicdatasource.model.User;
import com.example.dynamicdatasource.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
