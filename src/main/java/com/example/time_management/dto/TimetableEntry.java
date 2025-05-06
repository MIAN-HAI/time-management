package com.example.time_management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class TimetableEntry {
    @JsonProperty("时间段")
    private String timeSlot;

    @JsonProperty("节次")
    private String section;

    @JsonProperty("courses")
    private Map<String, List<Course>> courses;

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    public Map<String, List<Course>> getCourses() { return courses; }
    public void setCourses(Map<String, List<Course>> courses) { this.courses = courses; }
}