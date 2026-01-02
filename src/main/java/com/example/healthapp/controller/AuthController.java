package com.example.healthapp.controller;

import com.example.healthapp.dto.LoginRequest;
import com.example.healthapp.dto.LoginResponse;
import com.example.healthapp.dto.UserRegistrationRequest;
import com.example.healthapp.service.AuthService;
import com.example.healthapp.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthService authService;

    public AuthController(RegistrationService registrationService, AuthService authService) {
        this.registrationService = registrationService;
        this.authService = authService;
    }
    // ........................REGISTER.......................................
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest request){
        registrationService.register(request);
        System.out.println("success");
        return ResponseEntity.ok(Map.of("message","User registered successfully"));
    }

    //...........................LOGIN........................................
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        LoginResponse response=authService.login(request);
        return ResponseEntity.ok(response);
    }

    //..........................REFRESH TOKEN.................................
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {

        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Refresh token is required"));
        }

        String newAccessToken = authService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok(
                Map.of(
                        "accessToken", newAccessToken,
                        "expiresIn", authService.getAccessTokenExpiry()
                )
        );
    }

    @GetMapping("/test-secured")
    public String testSecured() {
        return "You are authenticated!";
    }

}
