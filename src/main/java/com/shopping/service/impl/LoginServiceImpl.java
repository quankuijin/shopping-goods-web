package com.shopping.service.impl;

import com.shopping.config.LoginConfig;
import com.shopping.service.LoginService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private LoginConfig loginConfig;

    @Override
    public boolean login(String username, String password) {
        return loginConfig.getUsername().equals(username) && loginConfig.getPassword().equals(password);
    }
}
