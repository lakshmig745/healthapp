package com.example.healthapp.dto;

import jakarta.validation.constraints.NotBlank;

public class SurgeryRequest {

    @NotBlank
    private String surgeryName;

    private Integer year; // optional

    // getters and setters

    public String getSurgeryName() {
        return surgeryName;
    }

    public void setSurgeryName(String surgeryName) {
        this.surgeryName = surgeryName;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
