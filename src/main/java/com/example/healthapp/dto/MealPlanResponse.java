package com.example.healthapp.dto;

import java.util.Map;

public class MealPlanResponse {

    private Map<String, Map<String,String>> weekPlan;

    public MealPlanResponse() {
    }

    public MealPlanResponse(Map<String, Map<String, String>> weekPlan) {
        this.weekPlan = weekPlan;
    }

    public Map<String, Map<String, String>> getWeekPlan() {
        return weekPlan;
    }
}
