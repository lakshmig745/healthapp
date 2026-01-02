package com.example.healthapp.repository;

import com.example.healthapp.entity.UsersHealth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersHealthRepo extends JpaRepository<UsersHealth, Long> {

    List<UsersHealth> findByUserId(Long userId);
}
