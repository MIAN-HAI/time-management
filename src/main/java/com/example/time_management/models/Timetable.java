package com.example.time_management.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "timetables")
@Data
@Entity
public class Timetable {
  @Id @GeneratedValue 
  Long id;
  Long userId;

  String termStartDate;
  
  @Column(columnDefinition = "json")
  private String jsonData;  // 直接存 TimetableResponse 的 JSON
}