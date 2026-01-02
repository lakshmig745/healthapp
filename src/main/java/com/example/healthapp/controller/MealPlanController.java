package com.example.healthapp.controller;

import com.example.healthapp.dto.MealPlanResponse;
import com.example.healthapp.entity.User;
import com.example.healthapp.repository.UserRepo;
import com.example.healthapp.service.AiMealPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/food")
public class MealPlanController {

    private final UserRepo userRepo;
    private final AiMealPlanService aiMealPlanService;

    public MealPlanController(UserRepo userRepo, AiMealPlanService aiMealPlanService) {
        this.userRepo = userRepo;
        this.aiMealPlanService = aiMealPlanService;
    }

    @GetMapping("/mealplan")
    public ResponseEntity<MealPlanResponse> getMealPlan(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User user = userRepo.findByUserName(userName)
                .orElseThrow(()-> new RuntimeException("User not found"));

        return ResponseEntity.ok(aiMealPlanService.getMealPlanForUser(user));

    }

}
