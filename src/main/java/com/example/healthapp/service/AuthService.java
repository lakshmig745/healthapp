package com.example.healthapp.service;

import com.example.healthapp.dto.LoginRequest;
import com.example.healthapp.dto.LoginResponse;
import com.example.healthapp.entity.RefreshToken;
import com.example.healthapp.entity.User;
import com.example.healthapp.repository.RefreshTokenRepo;
import com.example.healthapp.repository.UserRepo;
import com.example.healthapp.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepo userRepo;
    private final RefreshTokenRepo refreshTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;

    private static final long REFRESH_TOKEN_DAYS=7;

    public AuthService(UserRepo userRepo, RefreshTokenRepo refreshTokenRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.refreshTokenRepo = refreshTokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Login user and generate tokens
     */
    public LoginResponse login(LoginRequest request) {

        // Find user
        User user = userRepo.findByUserName(request.getUserName())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        //  Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        //  Generate access token (JWT)
        String accessToken = jwtUtil.generateAccessToken(user.getUserName());

        // Generate refresh token
        RefreshToken refreshToken = createRefreshToken(user);

        // Save refresh token
        refreshTokenRepo.save(refreshToken);

        //  Return response
        return new LoginResponse(
                accessToken,
                refreshToken.getToken(),
                jwtUtil.getExpirySeconds()
        );
    }
    //-----------------REFRESH ACCESS TOKEN---------------------------
    public String refreshAccessToken(String refreshTokenValue) {

        RefreshToken refreshToken = refreshTokenRepo.findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        User user = refreshToken.getUser();

        return jwtUtil.generateAccessToken(user.getUserName());
    }

    // ---------------- ACCESS TOKEN EXPIRY ----------------

    public long getAccessTokenExpiry() {
        return jwtUtil.getExpirySeconds();
    }


    /**
     * Create refresh token entity
     */
    private RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(
                Instant.now().plus(REFRESH_TOKEN_DAYS, ChronoUnit.DAYS)
        );
        refreshToken.setRevoked(false);

        return refreshToken;
    }


}
