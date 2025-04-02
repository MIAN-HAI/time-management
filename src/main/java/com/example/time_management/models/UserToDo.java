package com.example.time_management.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "UserToDo")
public class UserToDo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自动递增 ID
    private Integer todoId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false,columnDefinition = "varchar(10) default \"medium\" check (priority in (\"low\", \"medium\", \"high\"))")
    private String priority;

    private String title;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime deadline;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime reminderTime;

    @Column(columnDefinition = "BOOLEAN default false")
    private Boolean isCompleted;
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createAt;

    private String description;

    public UserToDo() {
    }

    public UserToDo(Integer todoId, Integer userId, String priority, String title, 
            LocalDateTime deadline,LocalDateTime reminderTime,
            Boolean isCompleted,
            LocalDateTime updatedAt,LocalDateTime createAt, String description) {
                this.todoId = todoId;
                this.userId = userId;
                this.priority = priority;
                this.title = title;
                this.deadline = deadline;
                this.reminderTime = reminderTime;
                this.isCompleted = isCompleted;
                this.updatedAt = updatedAt;
                this.createAt = createAt;
                this.description = description;
    }

    public Integer getTodoId() {
        return todoId;
    }

    public void setTodoId(Integer todoId) {
        this.todoId = todoId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
    

    public Boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public LocalDateTime getUpdatedAt() {

        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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

    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalDateTime reminderTime) {
        this.reminderTime = reminderTime;
    }
}
