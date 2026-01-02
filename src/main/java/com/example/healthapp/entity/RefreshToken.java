package com.example.healthapp.entity;


import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name="refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // there can be many refresh tokens for each user
    @ManyToOne(optional = false, fetch=FetchType.LAZY)
    @JoinColumn(name="user_id",nullable =false)
    private User user;

    // refresh token String(UUID)
    @Column(nullable = false,unique = true,length = 500)
    private String token;

    // date of when this refresh token expires
    @Column(nullable=false)
    private Instant expiryDate;

    // Used for logout , if user logs out then set revoked will be true
    @Column(nullable=false)
    private boolean revoked=false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
}
