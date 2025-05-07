package com.example.time_management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在 3-20 之间")
    private String username;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少 6 位")
    private String password;

    // @NotBlank(message = "验证码不能为空")
    // @Size(min = 6, max = 6, message = "验证码至少 6 位")
    // private String verificationCode; // ✅ 新增验证码字段

}
