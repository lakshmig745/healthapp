package com.example.healthapp.dto;

import java.time.LocalDate;
import java.util.List;

public class UserProfileResponse {

    private String username;
    private String fullName;
    private String gender;
    private String race;
    private LocalDate dob;
    private Double heightCm;
    private Double weightKg;

    private List<String> knownConditions;
    private List<String> riskConcerns;

    public UserProfileResponse(String username, String fullName, String gender, String race, LocalDate dob, Double heightCm, Double weightKg, List<String> knownConditions, List<String> riskConcerns) {
        this.username = username;
        this.fullName = fullName;
        this.gender = gender;
        this.race = race;
        this.dob = dob;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.knownConditions = knownConditions;
        this.riskConcerns = riskConcerns;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public List<String> getKnownConditions() {
        return knownConditions;
    }

    public void setKnownConditions(List<String> knownConditions) {
        this.knownConditions = knownConditions;
    }

    public List<String> getRiskConcerns() {
        return riskConcerns;
    }

    public void setRiskConcerns(List<String> riskConcerns) {
        this.riskConcerns = riskConcerns;
    }

}
