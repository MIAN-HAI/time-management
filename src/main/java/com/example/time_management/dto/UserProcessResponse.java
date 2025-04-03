package com.example.time_management.dto;

import java.time.LocalDateTime;

import com.example.time_management.models.UserProcess;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class UserProcessResponse {
    private Integer id;
    private Integer planId;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime completedTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime createdAt;

    public UserProcessResponse() {

    }

    public UserProcessResponse(Integer processId, Integer planId, String content, LocalDateTime completedTime,
            LocalDateTime createdAt) {
        this.id = processId;
        this.planId = planId;
        this.content = content;
        this.completedTime = completedTime;
        this.createdAt = createdAt;
    }

    public UserProcessResponse(UserProcess userProcess) {
        this.id = userProcess.getProcessId();
        this.planId = userProcess.getPlanId();
        this.content = userProcess.getContent();
        this.completedTime = userProcess.getCompletedTime();
        this.createdAt = userProcess.getCreateAt();
    }

}
