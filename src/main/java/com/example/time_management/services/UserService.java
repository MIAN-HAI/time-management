package com.example.time_management.services;

import com.example.time_management.Util.JwtTokenUtil;
import com.example.time_management.dto.LoginResponse;
import com.example.time_management.exceptions.InvalidCredentialsException;
import com.example.time_management.exceptions.PhoneInvalid;
import com.example.time_management.exceptions.UserAlreadyExistsException;
import com.example.time_management.exceptions.UserNeverExsits;
import com.example.time_management.exceptions.VerificationCodeError;
import com.example.time_management.exceptions.VerificationCodeInvalid;
import com.example.time_management.models.User;
import com.example.time_management.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.springframework.data.redis.core.ValueOperations;

//这个注解会让spring自动管理这个类，使其成为业务逻辑层
//UserService用于处理用户注册，而不是直接操作数据库
@Service
public class UserService {
    // userRepository用于操作数据库，passwordEncoder用于加密密码
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final StringRedisTemplate redisTemplate; // 用 Redis 存验证码
    // 存放为静态的常量表达式模式
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    // 这个注解表示：spring会自动注入一个UserRepository实例，不需要手动创建对象
    @Autowired
    public UserService(UserRepository userRepository, StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        // BCryptPasswordEncoder是Spring Security提供的加密算法，用于加密密码
        // 好处：不会显示明文，并且每次加密的结果不同
        this.passwordEncoder = new BCryptPasswordEncoder(); // 初始化密码加密器
    }

    // 注册新用户
    public User registerUser(String username, String phone, String password) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("用户名已存在");
        }

        // 检查手机是否已注册
        if (userRepository.findByPhone(phone).isPresent()) {
            throw new UserAlreadyExistsException("手机已注册");
        }

        // // 检查验证码是否正确
        // // 取出redis中的手机号
        // String storedVerificationCode = redisTemplate.opsForValue().get("verification:" + phone);
        // if (storedVerificationCode == null) {
        //     throw new VerificationCodeInvalid("验证码无效");
        // }
        // // 取出redis中的验证码
        // if (!storedVerificationCode.equals(verificationCode)) {
        //     throw new VerificationCodeError("验证码错误");
        // }

        // 加密密码
        String hashedPassword = passwordEncoder.encode(password);

        // 创建用户对象
        User user = new User(username, phone, hashedPassword);

        // 保存用户到数据库
        return userRepository.save(user);
    }

    // 发送验证码的方法
    public void sendVerificationCode(String phone) {
        // 判断手机号码是否规范
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new PhoneInvalid("手机号码格式不正确");
        }

        // 生成 6 位验证码
        String verificationCode = String.valueOf(new Random().nextInt(900000) + 100000);
        System.out.println("验证码：" + verificationCode);
        // 将验证码存入 Redis，5 分钟后过期
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("verification:" + phone, verificationCode, 5, TimeUnit.MINUTES);

        // 这里可以调用短信 API 发送验证码
        
    }

    // 登录的验证服务
    public LoginResponse login(String username, String password) {
        // 根据 identifier 查找用户（这里假设 identifier 可能是用户名或手机号）
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNeverExsits("用户不存在");
        }
        // 比对密码（假设数据库中存储的是 BCrypt 加密后的密码）
        if (!BCrypt.checkpw(password, user.get().getPassword())) {
            throw new InvalidCredentialsException("密码错误");
        }
        // 后序可以对用户状态信息进行确认，但是目前登录就不查看用户的状态信息了
        user.get().setVerified(true);
        userRepository.save(user.get());

        String token = JwtTokenUtil.generateToken(user.get().getId());
        return new LoginResponse(token, user.get().getUsername());
    }
}
