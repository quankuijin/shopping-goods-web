package com.shopping.goods.controller;

import com.shopping.goods.common.Result;
import com.shopping.goods.config.LoginConfig;
import com.shopping.goods.dto.LoginRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import cn.hutool.core.util.IdUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginConfig loginConfig;
    private final RedisTemplate<String, Object> redisTemplate;

    public AuthController(LoginConfig loginConfig, RedisTemplate<String, Object> redisTemplate) {
        this.loginConfig = loginConfig;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginRequest request) {
        if (!loginConfig.getUsername().equals(request.getUsername()) 
            || !loginConfig.getPassword().equals(request.getPassword())) {
            return Result.error(401, "用户名或密码错误");
        }
        
        String token = IdUtil.simpleUUID();
        String tokenKey = "token:" + token;
        redisTemplate.opsForValue().set(tokenKey, request.getUsername(), 24, TimeUnit.HOURS);
        
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("username", request.getUsername());
        return Result.success(data);
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && !token.isEmpty()) {
            redisTemplate.delete("token:" + token);
        }
        return Result.success();
    }
}
