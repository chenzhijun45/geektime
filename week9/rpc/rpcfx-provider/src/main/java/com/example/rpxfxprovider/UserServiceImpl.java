package com.example.rpxfxprovider;

import com.example.rpcfxapi.User;
import com.example.rpcfxapi.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public User findById(int id) {
        return new User(1, "czj");
    }

}
