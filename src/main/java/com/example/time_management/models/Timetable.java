package com.example.time_management.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "timetables")
@Data
public class Timetable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "term_start_date")
    private String termStartDate;

    @Lob
    @Column(name = "json_data")
    private String jsonData;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}