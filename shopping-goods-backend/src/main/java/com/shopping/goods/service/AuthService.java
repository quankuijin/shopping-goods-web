package com.shopping.goods.service;

import com.shopping.goods.config.AuthProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    @Autowired
    private AuthProperties authProperties;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String SESSION_PREFIX = "session:";
    private static final String USER_SESSION_KEY = "user:session";

    public String login(String username, String password) {
        if (!authProperties.getUsername().equals(username)) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (!authProperties.getPassword().equals(password)) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 生成token
        String token = UUID.randomUUID().toString().replace("-", "");

        // 存储session到Redis
        String sessionKey = SESSION_PREFIX + token;
        redisTemplate.opsForValue().set(sessionKey, username, authProperties.getSessionTimeout(), TimeUnit.SECONDS);

        return token;
    }

    public void logout(String token) {
        if (token != null && !token.isEmpty()) {
            String sessionKey = SESSION_PREFIX + token;
            redisTemplate.delete(sessionKey);
        }
    }

    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        String sessionKey = SESSION_PREFIX + token;
        Boolean exists = redisTemplate.hasKey(sessionKey);
        return Boolean.TRUE.equals(exists);
    }

    public String getUsername(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        String sessionKey = SESSION_PREFIX + token;
        Object username = redisTemplate.opsForValue().get(sessionKey);
        return username != null ? username.toString() : null;
    }
}
