package com.example.healthapp.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
@Component
public class JwtUtil {
    // Token validity: 15 minutes
    private static final long JWT_EXPIRATION_MS = 15 * 60 * 1000;

    // Secret key (for demo; move to application.properties later)
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * Generate JWT access token
     */
    public String generateAccessToken(String username) {

        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(username)                // who the token belongs to
                .setIssuedAt(Date.from(now))         // when token created
                .setExpiration(Date.from(now.plusMillis(JWT_EXPIRATION_MS)))
                .signWith(key)                       // sign with secret key
                .compact();
    }

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Validate JWT token
     */
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Internal method to parse token
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Token expiry time in seconds (for response)
     */
    public long getExpirySeconds() {
        return JWT_EXPIRATION_MS / 1000;
    }
}
