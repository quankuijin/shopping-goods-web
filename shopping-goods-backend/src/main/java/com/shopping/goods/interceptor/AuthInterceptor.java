package com.shopping.goods.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.goods.pojo.dto.Result;
import com.shopping.goods.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证token
        if (!authService.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            Result<?> result = Result.error(401, "未登录或登录已过期");
            response.getWriter().write(objectMapper.writeValueAsString(result));
            return false;
        }

        // 刷新token过期时间
        String username = authService.getUsername(token);
        request.setAttribute("currentUser", username);

        return true;
    }
}
