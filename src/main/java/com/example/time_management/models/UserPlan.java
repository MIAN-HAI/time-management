package com.example.time_management.models;

import java.time.LocalDateTime;


//导入JPA相关注解，JPA让我们可以用java操作数据库
import jakarta.persistence.*;

@Entity
@Table(name = "UserPlans") // 指定数据库表名
public class UserPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自动递增 ID
    private Integer planId;

    @Column(nullable = false)
    private Integer userId;

    private String title;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime startTime;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime endTime;

    @Column(nullable = false,columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isCompleted;

    @Column(nullable = false, columnDefinition = "DATETIME default current_timestamp")
    private LocalDateTime createAt;

    private String description;

    public UserPlan() {
    }

    public UserPlan(Integer planId, Integer userId, String title, LocalDateTime startTime, LocalDateTime endTime,
            Boolean isCompleted,
            LocalDateTime createAt, String description) {
        this.planId = planId;
        this.userId = userId;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isCompleted = isCompleted;
        this.createAt = createAt;
        this.description = description;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
