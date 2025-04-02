package com.example.time_management.dto;

public class LoginResponse {
    private String token;
    private Integer userId;
    private String userName;

    public LoginResponse() {
    }

    public LoginResponse(String token, Integer userId, String userName) {
        this.token = token;
        this.userId = userId;
        this.userName = userName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
