package com.example.KFCC_Backend.Entity;

import com.example.KFCC_Backend.Entity.Membership.MembershipApplication;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.time.LocalDate;

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
    private MembershipApplication application;

    private String applicantImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdCardStatus status;

    private LocalDate issuedAt;
    private LocalDate submittedAt;

    public enum IdCardStatus {
        PENDING_PAYMENT,
        REQUESTED,
        PRINTED,
        ISSUED
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public MembershipApplication getApplication() {
        return application;
    }

    public void setApplication(MembershipApplication application) {
        this.application = application;
    }

    public String getApplicantImage() {
        return applicantImage;
    }

    public void setApplicantImage(String applicantImage) {
        this.applicantImage = applicantImage;
    }

    public IdCardStatus getStatus() {
        return status;
    }

    public void setStatus(IdCardStatus status) {
        this.status = status;
    }

    public LocalDate getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDate issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDate getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDate submittedAt) {
        this.submittedAt = submittedAt;
    }
}

