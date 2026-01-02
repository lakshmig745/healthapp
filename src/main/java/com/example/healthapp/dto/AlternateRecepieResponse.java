package com.example.healthapp.dto;

import java.util.List;

public class AlternateRecepieResponse {

    private List<String> ingredients;
    private List<String> steps;


    public AlternateRecepieResponse() {
    }

    public AlternateRecepieResponse(List<String> ingredients, List<String> steps) {
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public List<String> getSteps() {
        return steps;
    }
}
