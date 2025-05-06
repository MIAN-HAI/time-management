package com.example.time_management.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class Course {
    @JsonProperty("课程名")
    private String courseName;

    @JsonProperty("节次")
    private String slot;

    @JsonProperty("周次")
    private String weeks;

    @JsonIgnore
    private Map<String, String> extras;

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }
    public String getWeeks() { return weeks; }
    public void setWeeks(String weeks) { this.weeks = weeks; }
    public Map<String, String> getExtras() { return extras; }
    public void setExtras(Map<String, String> extras) { this.extras = extras; }

    @JsonAnyGetter
    public Map<String, String> any() {
        return extras;
    }
}