package com.example.time_management.Util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import com.example.time_management.exceptions.TokenInvalid;

public class JwtTokenUtil {
    // 建议：密钥至少32字节，符合 HMAC-SHA256 要求
    private static final String SECRET_KEY = "ThisIsASecretKeyThatIsAtLeast32Bytes!";

    // Token 有效期（1小时）
    private static final long EXPIRATION_TIME = 36000000;

    // 使用 Keys 工具类生成 Key 对象
    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成 JWT Token
     * 
     * @param id planId或者userId
     * @return token 字符串
     */
    public static String generateToken(Integer id) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256) //  新写法
                .compact();
    }

    /**
     * 解析 Token，返回 Claims
     * 
     * @param token JWT token
     * @return claims 对象
     */
    public static Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key) // ✅ 新写法（parserBuilder）
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new TokenInvalid("Token 无效或已过期：" + e.getMessage());
        }
    }

    /**
     * 从 Token 中获取用户或计划id（subject）
     */
    public static Integer getIdFromToken(String token) {
        return Integer.valueOf(getClaims(token).getSubject());
    }
}
