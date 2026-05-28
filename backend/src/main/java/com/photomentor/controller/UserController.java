package com.photomentor.controller;

import com.photomentor.common.Result;
import com.photomentor.model.dto.LoginRequest;
import com.photomentor.model.entity.User;
import com.photomentor.service.UserService;
import com.photomentor.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request.getUsername(), request.getPassword());
            if (user == null) {
                return Result.error("用户名或密码错误");
            }
            String token = JwtUtil.generateToken(user.getId());
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", user);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("登录失败: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody LoginRequest request) {
        try {
            User user = userService.register(request.getUsername(), request.getPassword());
            if (user == null) {
                return Result.error("用户名已存在");
            }
            String token = JwtUtil.generateToken(user.getId());
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", user);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("注册失败: " + e.getMessage());
        }
    }

    @GetMapping("/profile")
    public Result<User> getProfile(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            User user = userService.getById(userId);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody User user, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            user.setId(userId);
            userService.updateById(user);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("更新用户信息失败: " + e.getMessage());
        }
    }
}
