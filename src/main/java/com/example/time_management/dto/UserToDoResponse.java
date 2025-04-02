package com.example.time_management.dto;

import java.time.LocalDateTime;

import com.example.time_management.Util.JwtTokenUtil;
import com.example.time_management.models.UserPlan;
import com.example.time_management.models.UserToDo;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;


@Data
public class UserToDoResponse {
    private String token;
    private String title;
    private String priority;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime deadline;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime reminderTime;

    private Boolean isCompleted;
    private String description;

    public UserToDoResponse() {
    }

    //生成有参构造函数，不是userplan为参数的
    public UserToDoResponse(String token,String title,String priority,LocalDateTime updatedAt,LocalDateTime deadline,LocalDateTime reminderTime,Boolean isCompleted,String description) {
        this.token = token;
        this.title = title;
        this.priority = priority;
        this.updatedAt = updatedAt;
        this.deadline = deadline;
        this.reminderTime = reminderTime;
        this.isCompleted = isCompleted;
        this.description = description;
    }


    public UserToDoResponse(UserPlan userPlan) {
    }

    public UserToDoResponse(UserToDo userToDo) {
        this.token = JwtTokenUtil.generateToken(userToDo.getTodoId());
        this.title = userToDo.getTitle();
        this.priority = userToDo.getPriority();
        this.updatedAt = userToDo.getUpdatedAt();
        this.deadline = userToDo.getDeadline();
        this.reminderTime = userToDo.getReminderTime();
        this.isCompleted = userToDo.isCompleted();
        this.description = userToDo.getDescription();
    }

}
