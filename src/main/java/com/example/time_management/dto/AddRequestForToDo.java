package com.example.time_management.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class AddRequestForToDo {
    private String title;
    private String priority;
    private LocalDateTime deadline;
    private boolean isCompleted;
    private LocalDateTime reminderTime;
    private String description;
}
