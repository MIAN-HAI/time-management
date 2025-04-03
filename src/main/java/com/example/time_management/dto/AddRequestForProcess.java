package com.example.time_management.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;


@Data
public class AddRequestForProcess {
    private String content;
    private LocalDateTime completedTime;
}
