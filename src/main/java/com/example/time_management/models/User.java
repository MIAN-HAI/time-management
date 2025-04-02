package com.example.time_management.models;

//导入JPA相关注解，JPA让我们可以用java操作数据库
import jakarta.persistence.*;

@Entity
@Table(name = "users") // 指定数据库表名
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自动递增 ID
    private Integer id;

    @Column(unique = true, nullable = false) // 用户名唯一且不能为空，需要有唯一性
    private String username;

    @Column(unique = true, nullable = false) // 电话唯一且不能为空，需要有唯一性
    private String phone;

    @Column(nullable = false) // 密码不能为空
    private String password;

    @Column(nullable = false,columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isVerified;

    public User() {
    }

    public User(String username, String phone, String password) {
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.isVerified = false;
    }

    public User(String username, String phone, String password, Boolean isVerified) {
        this(username, phone, password);
        this.isVerified = isVerified;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }

    // Getter 和 Setter 方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setEmail(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
