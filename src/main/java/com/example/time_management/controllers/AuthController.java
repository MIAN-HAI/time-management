package com.example.time_management.controllers;
import com.example.time_management.services.UserService;

import jakarta.validation.Valid;

import com.example.time_management.dto.ApiResponse;
import com.example.time_management.dto.LoginRequest;
import com.example.time_management.dto.RegisterRequest;
import com.example.time_management.dto.LoginResponse;
import com.example.time_management.exceptions.InvalidCredentialsException;
import com.example.time_management.exceptions.PhoneInvalid;
import com.example.time_management.exceptions.UserAlreadyExistsException;
import com.example.time_management.exceptions.UserNeverExsits;
import com.example.time_management.exceptions.VerificationCodeError;
import com.example.time_management.exceptions.VerificationCodeInvalid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

//标记这个类是控制器，用于处理HTTP请求
@RestController
@RequestMapping("/auth") // 设置基础 URL /auth，所有 HTTP 请求都将以 /auth 开头
public class AuthController {
    // 注入依赖
    private final UserService userService;

    // 自动注入UserService实例，和UserRepository实例一样，都是由spring自动注入
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // 定义POST请求，处理用户注册
    // 用户注册接口
    @PostMapping("/register")
    // springboot自动解析前端传来的JSON数据，并封装到RegisterRequest对象中
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        try {
            userService.registerUser(request.getUsername(), request.getPhone(), request.getPassword(),
                    request.getVerificationCode());
            return ResponseEntity.ok(new ApiResponse<>(200, "注册成功", null));
        } catch (UserAlreadyExistsException e) {// 该函数中手动处理了UserAlreadyExistsException 异常，
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, e.getMessage(), null));
        } catch (VerificationCodeInvalid e) {// 该函数中手动处理了UserAlreadyExistsException 异常，
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, e.getMessage(), null));
        } catch (VerificationCodeError e) {// 该函数中手动处理了UserAlreadyExistsException 异常，
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    // 处理发送验证码的请求
    @PostMapping("/send-verificationcode")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        try {
            userService.sendVerificationCode(request.get("phone"));
            return ResponseEntity.ok(new ApiResponse<String>(200, "验证码已发送", request.get("phone")));
        } catch (PhoneInvalid e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        try {
            LoginResponse data = userService.login(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(new ApiResponse<LoginResponse>(200, "登录成功", data));
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, e.getMessage(), null));
        } catch (UserNeverExsits e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    // 这个注解告诉编译器不再报空指针的异常，我确定不会为空指针
    @SuppressWarnings("null")
    // 处理所有 MethodArgumentNotValidException 异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
