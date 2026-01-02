package com.example.healthapp.controller;


import com.example.healthapp.dto.FoodGuidanceResponse;
import com.example.healthapp.entity.User;
import com.example.healthapp.repository.UserRepo;
import com.example.healthapp.service.AiFoodGuidanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/food")
public class FoodGuidanceController {
    private final UserRepo userRepo;
    private final AiFoodGuidanceService aiFoodGuidanceService;

    public FoodGuidanceController(UserRepo userRepo, AiFoodGuidanceService aiFoodGuidanceService) {
        this.userRepo = userRepo;
        this.aiFoodGuidanceService = aiFoodGuidanceService;
    }

    @GetMapping("/guidance")
    public ResponseEntity<FoodGuidanceResponse> getGuidance(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User user = userRepo.findByUserName(userName)
                            .orElseThrow(()-> new RuntimeException("User not found"));

        return ResponseEntity.ok(aiFoodGuidanceService.getGuidanceForUser(user));

    }


}
