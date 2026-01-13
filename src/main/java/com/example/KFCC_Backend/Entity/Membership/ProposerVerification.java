package com.example.KFCC_Backend.Entity.Membership;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class ProposerVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long applicantUserId;

    private String proposerMembershipId;

    private Long proposerUserId;

    private String otp;

    private Integer otpAttempts = 0;

    private LocalDateTime otpExpiresAt;

    private Boolean isVerified = false;

    private LocalDateTime verifiedAt;

    private LocalDateTime createdAt;


//  getter and setters



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicantUserId() {
        return applicantUserId;
    }

    public void setApplicantUserId(Long applicantUserId) {
        this.applicantUserId = applicantUserId;
    }

    public String getProposerMembershipId() {
        return proposerMembershipId;
    }

    public void setProposerMembershipId(String proposerMembershipId) {
        this.proposerMembershipId = proposerMembershipId;
    }

    public Long getProposerUserId() {
        return proposerUserId;
    }

    public void setProposerUserId(Long proposerUserId) {
        this.proposerUserId = proposerUserId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Integer getOtpAttempts() {
        return otpAttempts;
    }

    public void setOtpAttempts(Integer otpAttempts) {
        this.otpAttempts = otpAttempts;
    }

    public LocalDateTime getOtpExpiresAt() {
        return otpExpiresAt;
    }

    public void setOtpExpiresAt(LocalDateTime otpExpiresAt) {
        this.otpExpiresAt = otpExpiresAt;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
