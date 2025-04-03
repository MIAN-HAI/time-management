package com.example.time_management.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "UserProcess") // 指定数据库表名
public class UserProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自动递增 ID
    private Integer processId;

    @Column(nullable =false)
    private Integer planId;

    private String content;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime completedTime;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createAt;

    public UserProcess(){}

    public UserProcess(Integer processId, Integer planId, String content, LocalDateTime completedTime,
            LocalDateTime createAt) {
                this.processId = processId;
                this.planId = planId;
                this.content = content;
                this.completedTime = completedTime;
                this.createAt = createAt;
    }

}
