package com.example.KFCC_Backend.Entity.Membership;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Proposer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proposerId;

    @NotNull(message = "Proposer reference Id cannot be null")
    private Long proposerMembershipId;

    @NotBlank(message = "Proposer name required")
    private String proposerName;

    @NotBlank(message = "Proposer address required")
    private String proposerAddress;

    @NotBlank(message = "proposer phone number required")
    private String proposerMobileNo;

    @NotBlank(message = "Proposer designation required")
    private String proposerDesignation;

    @OneToOne
    @JoinColumn(name = "application_id", referencedColumnName = "applicationId", nullable = false)
    @JsonBackReference
    private MembershipApplication membershipApplication;


    

    public Long getProposerId() {
        return proposerId;
    }

    public void setProposerId(Long proposerId) {
        this.proposerId = proposerId;
    }

    public Long getProposerMembershipId() {
        return proposerMembershipId;
    }

    public void setProposerMembershipId(Long proposerMembershipId) {
        this.proposerMembershipId = proposerMembershipId;
    }

    public String getProposerName() {
        return proposerName;
    }

    public void setProposerName(String proposerName) {
        this.proposerName = proposerName;
    }

    public String getProposerAddress() {
        return proposerAddress;
    }

    public void setProposerAddress(String proposerAddress) {
        this.proposerAddress = proposerAddress;
    }

    public String getProposerMobileNo() {
        return proposerMobileNo;
    }

    public void setProposerMobileNo(String proposerMobileNo) {
        this.proposerMobileNo = proposerMobileNo;
    }

    public String getProposerDesignation() {
        return proposerDesignation;
    }

    public void setProposerDesignation(String proposerDesignation) {
        this.proposerDesignation = proposerDesignation;
    }

    public MembershipApplication getMembershipApplication() {
        return membershipApplication;
    }

    public void setMembershipApplication(MembershipApplication membershipApplication) {
        this.membershipApplication = membershipApplication;
    }
}
