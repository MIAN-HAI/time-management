package com.example.time_management.Util;

import com.example.time_management.exceptions.TokenInvalid;

public class TokenUtil {

    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 从 Authorization 请求头中提取 JWT Token
     * 
     * @param authHeader 请求头中的 Authorization 字符串
     * @return 提取出来的 token（无 Bearer 前缀）
     * @throws IllegalArgumentException 如果格式错误或为空
     */
    public static String extractToken(String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            throw new TokenInvalid("请求头中缺少 Authorization");
        }

        if (!authHeader.startsWith(BEARER_PREFIX)) {
            throw new TokenInvalid("Token 格式错误，必须以 Bearer 开头");
        }

        return authHeader.substring(BEARER_PREFIX.length());
    }
}
