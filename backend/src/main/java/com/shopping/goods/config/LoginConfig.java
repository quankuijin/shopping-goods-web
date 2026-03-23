package com.shopping.goods.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app.login")
public class LoginConfig {
    private String username;
    private String password;
}
