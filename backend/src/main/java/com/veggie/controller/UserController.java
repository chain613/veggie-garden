package com.veggie.controller;

import com.veggie.model.User;
import com.veggie.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        if (phone == null || !phone.matches("1\\d{10}")) {
            return Map.of("success", false, "message", "手机号格式不正确");
        }
        User user = userService.loginOrRegister(phone);
        String token = userService.generateToken(user);
        return Map.of(
            "success", true,
            "token", token,
            "user", user
        );
    }

    @GetMapping("/profile")
    public User profile(@RequestHeader("Authorization") String auth) {
        Long userId = extractUserId(auth);
        return userService.findById(userId).orElse(null);
    }

    private Long extractUserId(String auth) {
        try {
            return Long.parseLong(auth.replace("Bearer ", "").split(":")[0]);
        } catch (Exception e) {
            throw new RuntimeException("无效的认证令牌");
        }
    }
}
