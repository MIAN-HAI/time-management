package com.example.time_management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TimetableResponse {
    @JsonProperty("term_start_date")
    private String termStartDate;

    @JsonProperty("timetable")
    private List<TimetableEntry> timetable;

    public TimetableResponse() {}
    public TimetableResponse(String termStartDate, List<TimetableEntry> timetable) {
        this.termStartDate = termStartDate;
        this.timetable = timetable;
    }

    public String getTermStartDate() { return termStartDate; }
    public void setTermStartDate(String termStartDate) { this.termStartDate = termStartDate; }
    public List<TimetableEntry> getTimetable() { return timetable; }
    public void setTimetable(List<TimetableEntry> timetable) { this.timetable = timetable; }
}