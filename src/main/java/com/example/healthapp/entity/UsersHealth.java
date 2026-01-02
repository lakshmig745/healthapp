package com.example.healthapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name="usershealth")
public class UsersHealth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable=false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condition_id",nullable=false)
    private HealthCondition healthcondition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionSource conditionsource;

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

    public HealthCondition getHealthcondition() {
        return healthcondition;
    }

    public void setHealthcondition(HealthCondition healthcondition) {
        this.healthcondition = healthcondition;
    }

    public ConditionSource getConditionsource() {
        return conditionsource;
    }

    public void setConditionsource(ConditionSource conditionsource) {
        this.conditionsource = conditionsource;
    }
}
