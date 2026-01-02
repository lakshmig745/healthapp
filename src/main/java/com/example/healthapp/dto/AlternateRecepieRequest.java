package com.example.healthapp.dto;

import jakarta.validation.constraints.NotBlank;

public class AlternateRecepieRequest {

    @NotBlank
    private String dish;

    public String getDish() {
        return dish;
    }

    public void setDish(String dish) {
        this.dish = dish;
    }

}
