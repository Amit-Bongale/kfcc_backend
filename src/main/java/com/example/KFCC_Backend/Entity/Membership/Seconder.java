package com.example.KFCC_Backend.Entity.Membership;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Seconder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seconderId;

    @NotNull(message = "Seconder reference Id cannot be null")
    private Long seconderMembershipId;

    @NotBlank(message = "Seconder name required")
    private String seconderName;

    @NotBlank(message = "Seconder address required")
    private String seconderAddress;

    @NotBlank(message = "Seconder phone number required")
    private String seconderMobileNo;

    @NotBlank(message = "Seconder designation required")
    private String seconderDesignation;

    @OneToOne
    @JoinColumn(name = "application_id", referencedColumnName = "applicationId", nullable = false)
    @JsonBackReference
    private MembershipApplication membershipApplication;




    public Long getSeconderId() {
        return seconderId;
    }

    public void setSeconderId(Long seconderId) {
        this.seconderId = seconderId;
    }

    public Long getSeconderMembershipId() {
        return seconderMembershipId;
    }

    public void setSeconderMembershipId(Long seconderMembershipId) {
        this.seconderMembershipId = seconderMembershipId;
    }

    public String getSeconderName() {
        return seconderName;
    }

    public void setSeconderName(String seconderName) {
        this.seconderName = seconderName;
    }

    public String getSeconderAddress() {
        return seconderAddress;
    }

    public void setSeconderAddress(String seconderAddress) {
        this.seconderAddress = seconderAddress;
    }

    public String getSeconderMobileNo() {
        return seconderMobileNo;
    }

    public void setSeconderMobileNo(String seconderMobileNo) {
        this.seconderMobileNo = seconderMobileNo;
    }

    public String getSeconderDesignation() {
        return seconderDesignation;
    }

    public void setSeconderDesignation(String seconderDesignation) {
        this.seconderDesignation = seconderDesignation;
    }

    public MembershipApplication getMembershipApplication() {
        return membershipApplication;
    }

    public void setMembershipApplication(MembershipApplication membershipApplication) {
        this.membershipApplication = membershipApplication;
    }
}
