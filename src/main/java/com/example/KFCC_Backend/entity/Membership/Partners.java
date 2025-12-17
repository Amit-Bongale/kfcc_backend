package com.example.KFCC_Backend.entity.Membership;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Partners {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerId;

    private String partnerName;
    private String partnerAddress;
    private LocalDate partnerDob;
    private String partnerBloodGroup;
    private String partnerPanNo;
    private String partnerPanImg;
    private String partnerAadhaarNo;
    private String partnerAadhaarImg;
    private String partnerESignature;

    @ManyToOne
    @JoinColumn(name = "application_id", referencedColumnName = "applicationId", nullable = false)
    @JsonBackReference
    private MembershipApplication membershipApplication;

    
    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerAddress() {
        return partnerAddress;
    }

    public void setPartnerAddress(String partnerAddress) {
        this.partnerAddress = partnerAddress;
    }

    public LocalDate getPartnerDob() {
        return partnerDob;
    }

    public void setPartnerDob(LocalDate partnerDob) {
        this.partnerDob = partnerDob;
    }

    public String getPartnerBloodGroup() {
        return partnerBloodGroup;
    }

    public void setPartnerBloodGroup(String partnerBloodGroup) {
        this.partnerBloodGroup = partnerBloodGroup;
    }

    public String getPartnerPanNo() {
        return partnerPanNo;
    }

    public void setPartnerPanNo(String partnerPanNo) {
        this.partnerPanNo = partnerPanNo;
    }

    public String getPartnerPanImg() {
        return partnerPanImg;
    }

    public void setPartnerPanImg(String partnerPanImg) {
        this.partnerPanImg = partnerPanImg;
    }

    public String getPartnerAadhaarNo() {
        return partnerAadhaarNo;
    }

    public void setPartnerAadhaarNo(String partnerAadhaarNo) {
        this.partnerAadhaarNo = partnerAadhaarNo;
    }

    public String getPartnerAadhaarImg() {
        return partnerAadhaarImg;
    }

    public void setPartnerAadhaarImg(String partnerAadhaarImg) {
        this.partnerAadhaarImg = partnerAadhaarImg;
    }

    public String getPartnerESignature() {
        return partnerESignature;
    }

    public void setPartnerESignature(String partnerESignature) {
        this.partnerESignature = partnerESignature;
    }

    public MembershipApplication getMembershipApplication() {
        return membershipApplication;
    }

    public void setMembershipApplication(MembershipApplication membershipApplication) {
        this.membershipApplication = membershipApplication;
    }
}
