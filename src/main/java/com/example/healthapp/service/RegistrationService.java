package com.example.healthapp.service;

import com.example.healthapp.dto.SurgeryRequest;
import com.example.healthapp.dto.UserRegistrationRequest;
import com.example.healthapp.entity.*;
import com.example.healthapp.repository.HealthConditionRepo;
import com.example.healthapp.repository.SurgeryHistoryRepo;
import com.example.healthapp.repository.UserRepo;
import com.example.healthapp.repository.UsersHealthRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistrationService {
    private final UserRepo userRepo;
    private final UsersHealthRepo usersHealthRepo;
    private final HealthConditionRepo healthConditionRepo;
    private final SurgeryHistoryRepo surgeryHistoryRepo;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepo userRepo, UsersHealthRepo usersHealthRepo, HealthConditionRepo healthConditionRepo, SurgeryHistoryRepo surgeryHistoryRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.usersHealthRepo = usersHealthRepo;
        this.healthConditionRepo = healthConditionRepo;
        this.surgeryHistoryRepo = surgeryHistoryRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(UserRegistrationRequest request){

        //check if username already exists
        if (userRepo.existsByUserName(request.getUserName())){
            throw new RuntimeException("Username already Exists, Please try another Username");
        }

        //Create and save into User Entity
        User user=new User();
        user.setFullName(request.getFullName());
        user.setDob(request.getDob());
        user.setGender(request.getGender());
        user.setUserName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRace(request.getRace());
        user.setHeightCm(request.getHeightCm());
        user.setWeightKg(request.getWeightKg());

        userRepo.save(user);

        //saving KNOWN Health Conditions data
        saveConditions(user, request.getKnownConditions(), ConditionSource.KNOWN);

        //saving RISK-BASED health Conditions data
        saveConditions(user, request.getRiskBasedConcerns(), ConditionSource.RISK_BASED);

        //save previous surgeries related data
        saveSurgeries(user, request.getSurgeries());

    }

    private void saveConditions(User user, List<String> conditions,ConditionSource source){
        if (conditions == null || conditions.isEmpty()){
            return;
        }
        for (String conditionName : conditions){
            HealthCondition condition=healthConditionRepo.findByNameIgnoreCase(conditionName)
                                                        .orElseGet(() -> {
                                                            HealthCondition newCondition = new HealthCondition();
                                                            newCondition.setName(conditionName);
                                                            return healthConditionRepo.save(newCondition);
                                                        });
            UsersHealth usersHealth = new UsersHealth();
            usersHealth.setUser(user);
            usersHealth.setHealthcondition(condition);
            usersHealth.setConditionsource(source);

            usersHealthRepo.save(usersHealth);
        }
    }

    private void saveSurgeries(User user, List<SurgeryRequest> surgeries){
        if (surgeries == null || surgeries.isEmpty()){
            return;
        }
        for (SurgeryRequest surgeryRequest: surgeries){

            SurgeryHistory surgeryHistory=new SurgeryHistory();
            surgeryHistory.setUser(user);
            surgeryHistory.setSurgeryname(surgeryRequest.getSurgeryName());
            surgeryHistory.setYear(surgeryRequest.getYear());

            surgeryHistoryRepo.save(surgeryHistory);
        }
    }

}
