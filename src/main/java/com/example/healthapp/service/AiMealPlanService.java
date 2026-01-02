package com.example.healthapp.service;

import com.example.healthapp.dto.MealPlanResponse;
import com.example.healthapp.entity.User;

public interface AiMealPlanService {
    MealPlanResponse getMealPlanForUser(User user);
}
