package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.service.LoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Resource
    private LoginService loginService;

    @PostMapping("/login")
    public Result<Boolean> login(@RequestBody Map<String, String> loginInfo) {
        String username = loginInfo.get("username");
        String password = loginInfo.get("password");
        boolean success = loginService.login(username, password);
        if (success) {
            return Result.success(true);
        } else {
            return Result.error(401, "用户名或密码错误");
        }
    }
}
