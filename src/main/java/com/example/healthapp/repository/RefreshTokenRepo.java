package com.example.healthapp.repository;

import com.example.healthapp.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    //Find the refresh token record by token string
    Optional<RefreshToken> findByToken(String token);

    //Delete all refresh tokens for a user(logout from all devices)
    void deleteByUserId(Long UserId);
}
