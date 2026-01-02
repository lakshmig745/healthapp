package com.example.healthapp.repository;

import com.example.healthapp.entity.HealthCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HealthConditionRepo extends JpaRepository<HealthCondition, Long> {

    Optional<HealthCondition> findByNameIgnoreCase(String name);

}
