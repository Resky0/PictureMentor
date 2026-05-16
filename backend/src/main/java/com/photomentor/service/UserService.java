package com.photomentor.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photomentor.model.entity.User;
import com.photomentor.mapper.UserMapper;
import com.photomentor.util.PasswordUtil;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    public User login(String username, String password) {
        User user = lambdaQuery().eq(User::getUsername, username).one();
        if (user == null || !PasswordUtil.matches(password, user.getPassword())) {
            return null;
        }
        return user;
    }

    public User register(String username, String password) {
        User existing = lambdaQuery().eq(User::getUsername, username).one();
        if (existing != null) {
            return null;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.encrypt(password));
        user.setEmail(username + "@example.com");
        save(user);
        return user;
    }

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
