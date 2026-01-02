package com.example.healthapp.controller;

import com.example.healthapp.dto.UserProfileResponse;
import com.example.healthapp.entity.SurgeryHistory;
import com.example.healthapp.entity.User;
import com.example.healthapp.entity.UsersHealth;
import com.example.healthapp.repository.SurgeryHistoryRepo;
import com.example.healthapp.repository.UserRepo;
import com.example.healthapp.repository.UsersHealthRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserRepo userRepo;
    private final UsersHealthRepo usersHealthRepo;


    public UserController(UserRepo userRepo, UsersHealthRepo usersHealthRepo) {
        this.userRepo = userRepo;
        this.usersHealthRepo = usersHealthRepo;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(){

        // this authentication was set by JwtAuthenticationFilter, it gets logged in username from JWT Filter
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userName = authentication.getName();

        // fetch user
        User user = userRepo.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // fetch health conditions
        List<UsersHealth> conditions =
                usersHealthRepo.findByUserId(user.getId());

        List<String> knownConditions = conditions.stream()
                .filter(c -> c.getConditionsource().name().equals("KNOWN"))
                .map(c -> c.getHealthcondition().getName())
                .collect(Collectors.toList());

        List<String> riskConcerns = conditions.stream()
                .filter(c -> c.getConditionsource().name().equals("RISK_BASED"))
                .map(c -> c.getHealthcondition().getName())
                .collect(Collectors.toList());


        //  Build response DTO
        UserProfileResponse response = new UserProfileResponse(
                user.getUserName(),
                user.getFullName(),
                user.getGender(),
                user.getRace(),
                user.getDob(),
                user.getHeightCm(),
                user.getWeightKg(),
                knownConditions,
                riskConcerns

        );

        return ResponseEntity.ok(response);

    }


}
