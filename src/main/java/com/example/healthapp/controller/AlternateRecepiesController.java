package com.example.healthapp.controller;

import com.example.healthapp.dto.AlternateRecepieRequest;
import com.example.healthapp.dto.AlternateRecepieResponse;
import com.example.healthapp.entity.User;
import com.example.healthapp.repository.UserRepo;
import com.example.healthapp.service.AiAlternateRecepieService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/food")
public class AlternateRecepiesController {

    private final UserRepo userRepo;
    private final AiAlternateRecepieService aiAlternateRecepieService;

    public AlternateRecepiesController(UserRepo userRepo, AiAlternateRecepieService aiAlternateRecepieService) {
        this.userRepo = userRepo;
        this.aiAlternateRecepieService = aiAlternateRecepieService;
    }


    @PostMapping("/recepies")
    public ResponseEntity<AlternateRecepieResponse> getAlternateRecepie(@Valid @RequestBody AlternateRecepieRequest recepiesrequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User user = userRepo.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(aiAlternateRecepieService.getAlternateRecepieForUser(user, recepiesrequest));


    }
}

