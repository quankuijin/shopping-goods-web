package com.shopping.goods.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    private String username;
    private String password;
    private Long sessionTimeout;
}
