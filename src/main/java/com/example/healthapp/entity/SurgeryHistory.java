package com.example.healthapp.entity;


import jakarta.persistence.*;

@Entity
@Table(name="surgeryhistory")
public class SurgeryHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable=false)
    private User user;

    @Column(nullable=false)
    private String surgeryname;

    private Integer year;

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

    public String getSurgeryname() {
        return surgeryname;
    }

    public void setSurgeryname(String surgeryname) {
        this.surgeryname = surgeryname;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
