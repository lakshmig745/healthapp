package com.example.healthapp.service;

import com.example.healthapp.dto.FoodGuidanceResponse;
import com.example.healthapp.entity.User;

public interface AiFoodGuidanceService {
    FoodGuidanceResponse getGuidanceForUser(User user);
}
