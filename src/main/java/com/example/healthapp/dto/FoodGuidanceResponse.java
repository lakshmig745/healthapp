package com.example.healthapp.dto;

import java.util.List;

public class FoodGuidanceResponse {

        private List<String> avoid;
        private List<String> recommended;
        private String notes;

    public FoodGuidanceResponse() {
    }

    public FoodGuidanceResponse(List<String> avoid, List<String> recommended, String notes) {
        this.avoid = avoid;
        this.recommended = recommended;
        this.notes = notes;
    }

    public List<String> getAvoid() {
        return avoid;
    }

    public List<String> getRecommended() {
        return recommended;
    }

    public String getNotes() {
        return notes;
    }
}
