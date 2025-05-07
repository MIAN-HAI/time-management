package com.example.time_management.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.Map;

// 在Course类中确保包含所有字段，并添加缺失的字段
@Data
public class Course {
    private String 课程名;
    private String 节次;
    private String 周次;
    private String 校区;
    private String 场地;
    private String 教师;
    private String 教学班;
    private String 教学班组成;
    private String 考核方式;
    private String 选课备注;
    private String 课程学时组成;
    private String 周学时;
    private String 总学时;
    private String 学分;
    private String 科研实践; // 新增字段
}