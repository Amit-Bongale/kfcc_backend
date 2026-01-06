package com.example.KFCC_Backend.Entity;

import com.example.KFCC_Backend.Entity.Membership.MembershipApplication;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class IdCardRequests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_application_id", nullable = false)
    private List<MembershipApplication> application;

    private String applicantImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdCardStatus status;

    private LocalDate issuedAt;

    private LocalDate submittedAt;

    public enum IdCardStatus {
        REQUESTED,
        STAFF_VERIFIED,
        ISSUED
    }

}

