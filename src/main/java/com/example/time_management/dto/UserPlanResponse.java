package com.example.time_management.dto;

import java.time.LocalDateTime;

import com.example.time_management.Util.JwtTokenUtil;
import com.example.time_management.models.UserPlan;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class UserPlanResponse {
    private Integer id;
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
        this.id = userPlan.getPlanId();
        this.title = userPlan.getTitle();
        this.startTime = userPlan.getStartTime();
        this.endTime = userPlan.getEndTime();
        this.isCompleted = userPlan.isCompleted();
        this.createAt = userPlan.getCreateAt();
        this.description = userPlan.getDescription();
    }

    public UserPlanResponse(Integer id, String title, LocalDateTime startTime, LocalDateTime endTime,
            Boolean isCompleted, LocalDateTime createAt, String description) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isCompleted = isCompleted;
        this.createAt = createAt;
        this.description = description;
    }


}
