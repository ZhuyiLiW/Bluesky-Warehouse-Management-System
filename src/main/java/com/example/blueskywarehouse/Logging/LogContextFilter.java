package com.example.blueskywarehouse.Logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class LogContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // 设置 traceId
            MDC.put("traceId", UUID.randomUUID().toString());

            // 从 Spring Security 中获取 userId
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                MDC.put("userId", auth.getName());  // 你可以根据实际需要改成 ID 或用户名
            }

            filterChain.doFilter(request, response);  // 继续请求链
        } finally {
            MDC.clear();  // 请求结束清除，防止线程污染
        }
    }
}
