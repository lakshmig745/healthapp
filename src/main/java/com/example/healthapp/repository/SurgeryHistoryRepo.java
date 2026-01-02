package com.example.healthapp.repository;

import com.example.healthapp.entity.SurgeryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurgeryHistoryRepo extends JpaRepository<SurgeryHistory, Long> {

}
