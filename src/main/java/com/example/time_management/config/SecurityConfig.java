package com.example.time_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // ❌ 关闭 CSRF 保护（POST/PUT/DELETE 请求会被拦截，必须禁用）
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // ✅ 允许所有请求，无需认证
                )
                .formLogin(form -> form.disable()) // ❌ 禁用 Spring Security 默认的表单登录
                .httpBasic(httpBasic -> httpBasic.disable()); // ❌ 禁用 Basic 认证

        return http.build();
    }
}
