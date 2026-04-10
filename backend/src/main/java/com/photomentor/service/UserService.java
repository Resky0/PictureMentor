package com.photomentor.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photomentor.entity.User;
import com.photomentor.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    public User getOrCreateUser(String username) {
        User user = lambdaQuery().eq(User::getUsername, username).one();
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setEmail(username + "@example.com");
            save(user);
        }
        return user;
    }
}
