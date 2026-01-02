package com.example.healthapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class UserRegistrationRequest {

    //  Basic Profile

    @NotBlank
    private String fullName;

    @NotBlank
    private String userName;

    @NotBlank
    @Size(min = 6)
    private String password;

    private LocalDate dob;

    private Double heightCm;
    private Double weightKg;

    @NotNull
    private String gender;

    private String race;

    // -------- Health Conditions --------

    // Diagnosed conditions (Diabetes, Thyroid, etc.)
    private List<String> knownConditions;

    // Preventive / family-history based concerns
    private List<String> riskBasedConcerns;

    // -------- Surgical History --------

    private List<SurgeryRequest> surgeries;

    // getters and setters


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {return userName;}

    public void setUserName(String userName) {this.userName = userName;}

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public List<String> getKnownConditions() {
        return knownConditions;
    }

    public void setKnownConditions(List<String> knownConditions) {
        this.knownConditions = knownConditions;
    }

    public List<String> getRiskBasedConcerns() {
        return riskBasedConcerns;
    }

    public void setRiskBasedConcerns(List<String> riskBasedConcerns) {
        this.riskBasedConcerns = riskBasedConcerns;
    }

    public List<SurgeryRequest> getSurgeries() {
        return surgeries;
    }

    public void setSurgeries(List<SurgeryRequest> surgeries) {
        this.surgeries = surgeries;
    }
}
