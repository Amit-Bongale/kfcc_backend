package com.example.KFCC_Backend.entity.Membership;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Proprietor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proprietorId;

    private String proprietorName;
    private String proprietorAddress;
    private LocalDate proprietorDob;
    private String proprietorBloodGroup;
    private String proprietorPanNo;
    private String proprietorPanImg;
    private String proprietorAadhaarNo;
    private String proprietorAadhaarImg;
    private String proprietorESignature;

    // Relationship with Membership
    @OneToOne
    @JoinColumn(name = "application_id", referencedColumnName = "applicationId", nullable = false)
    @JsonBackReference
    private MembershipApplication membershipApplication;



    

    public Long getProprietorId() {
        return proprietorId;
    }

    public void setProprietorId(Long proprietorId) {
        this.proprietorId = proprietorId;
    }

    public String getProprietorName() {
        return proprietorName;
    }

    public void setProprietorName(String proprietorName) {
        this.proprietorName = proprietorName;
    }

    public String getProprietorAddress() {
        return proprietorAddress;
    }

    public void setProprietorAddress(String proprietorAddress) {
        this.proprietorAddress = proprietorAddress;
    }

    public LocalDate getProprietorDob() {
        return proprietorDob;
    }

    public void setProprietorDob(LocalDate proprietorDob) {
        this.proprietorDob = proprietorDob;
    }

    public String getProprietorBloodGroup() {
        return proprietorBloodGroup;
    }

    public void setProprietorBloodGroup(String proprietorBloodGroup) {
        this.proprietorBloodGroup = proprietorBloodGroup;
    }

    public String getProprietorPanNo() {
        return proprietorPanNo;
    }

    public void setProprietorPanNo(String proprietorPanNo) {
        this.proprietorPanNo = proprietorPanNo;
    }

    public String getProprietorPanImg() {
        return proprietorPanImg;
    }

    public void setProprietorPanImg(String proprietorPanImg) {
        this.proprietorPanImg = proprietorPanImg;
    }

    public String getProprietorAadhaarNo() {
        return proprietorAadhaarNo;
    }

    public void setProprietorAadhaarNo(String proprietorAadhaarNo) {
        this.proprietorAadhaarNo = proprietorAadhaarNo;
    }

    public String getProprietorAadhaarImg() {
        return proprietorAadhaarImg;
    }

    public void setProprietorAadhaarImg(String proprietorAadhaarImg) {
        this.proprietorAadhaarImg = proprietorAadhaarImg;
    }

    public String getProprietorESignature() {
        return proprietorESignature;
    }

    public void setProprietorESignature(String proprietorESignature) {
        this.proprietorESignature = proprietorESignature;
    }

    public MembershipApplication getMembershipApplication() {
        return membershipApplication;
    }

    public void setMembershipApplication(MembershipApplication membershipApplication) {
        this.membershipApplication = membershipApplication;
    }
}
