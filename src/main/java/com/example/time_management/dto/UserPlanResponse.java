package com.example.time_management.dto;

import java.time.LocalDateTime;

import com.example.time_management.Util.JwtTokenUtil;
import com.example.time_management.models.UserPlan;
import com.fasterxml.jackson.annotation.JsonFormat;

public class UserPlanResponse {
    private String token;
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;

    private Boolean isCompleted;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime createAt;
    private String description;

    public UserPlanResponse() {
    }

    public UserPlanResponse(UserPlan userPlan) {
        this.token = JwtTokenUtil.generateToken(userPlan.getPlanId());
        this.title = userPlan.getTitle();
        this.startTime = userPlan.getStartTime();
        this.endTime = userPlan.getEndTime();
        this.isCompleted = userPlan.isCompleted();
        this.createAt = userPlan.getCreateAt();
        this.description = userPlan.getDescription();
    }

    public UserPlanResponse(String token, String title, LocalDateTime startTime, LocalDateTime endTime,
            Boolean isCompleted, LocalDateTime createAt, String description) {
        this.token = token;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isCompleted = isCompleted;
        this.createAt = createAt;
        this.description = description;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public void setIsCompleted(Boolean completed) {
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
