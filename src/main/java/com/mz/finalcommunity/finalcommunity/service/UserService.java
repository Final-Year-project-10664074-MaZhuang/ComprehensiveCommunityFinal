package com.mz.finalcommunity.finalcommunity.service;

import com.mz.finalcommunity.finalcommunity.dao.UserMapper;
import com.mz.finalcommunity.finalcommunity.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    public User findUserById(int id){
        return userMapper.selectById(id);
    }
}
