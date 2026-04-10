package com.photomentor.controller;

import com.photomentor.common.Result;
import com.photomentor.entity.User;
import com.photomentor.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public Result<User> getProfile() {
        try {
            User user = userService.getOrCreateUser("demo_user");
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody User user) {
        try {
            Long userId = 1L;
            user.setId(userId);
            userService.updateById(user);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("更新用户信息失败: " + e.getMessage());
        }
    }
}
