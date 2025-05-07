package com.example.time_management.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class UpdateRequestForToDo {
    private String title;
    private String priority;
    private Boolean isCompleted;
    private LocalDateTime updatedAt;
    private LocalDateTime deadline;
    private LocalDateTime reminderTime;
    private String description;
}
