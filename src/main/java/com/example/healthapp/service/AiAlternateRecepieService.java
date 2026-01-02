package com.example.healthapp.service;

import com.example.healthapp.dto.AlternateRecepieRequest;
import com.example.healthapp.dto.AlternateRecepieResponse;
import com.example.healthapp.entity.User;

public interface AiAlternateRecepieService {
    AlternateRecepieResponse getAlternateRecepieForUser(User user, AlternateRecepieRequest recepiesrequest);
}
