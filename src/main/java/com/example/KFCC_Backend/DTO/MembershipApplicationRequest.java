package com.example.KFCC_Backend.DTO;

import com.example.KFCC_Backend.entity.Membership.*;
import lombok.Data;

import java.util.List;

@Data
public class MembershipApplicationRequest {

    private Long userId;
    private String applicantMembershipCategory;
    private String applicantFirmName;
    private String applicantOwnershipType;
    private String applicantGstNo;
    private String applicantAddressLine1;
    private String applicantAddressLine2;
    private String applicantDistrict;
    private String applicantState;


    private Integer membershipFee;
    private Integer kalyanNidhi;

    private Proprietor proprietor;
    private List<Partners> partners;
    private List<Nominee> nominees;

    private Proposer proposer;
    private Seconder seconder;

    public Long getUserId() {
        return userId;
    }

    public String getApplicantMembershipCategory() {
        return applicantMembershipCategory;
    }

    public String getApplicantFirmName() {
        return applicantFirmName;
    }

    public String getApplicantOwnershipType() {
        return applicantOwnershipType;
    }

    public String getApplicantGstNo() {
        return applicantGstNo;
    }

    public Integer getMembershipFee() {
        return membershipFee;
    }

    public Integer getKalyanNidhi() {
        return kalyanNidhi;
    }

    public Proprietor getProprietor() {
        return proprietor;
    }

    public List<Partners> getPartners() {
        return partners;
    }

    public List<Nominee> getNominees() {
        return nominees;
    }

    public Proposer getProposer() {
        return proposer;
    }

    public Seconder getSeconder() {
        return seconder;
    }

    public String getApplicantAddressLine1() {
        return applicantAddressLine1;
    }

    public String getApplicantAddressLine2() {
        return applicantAddressLine2;
    }

    public String getApplicantDistrict() {
        return applicantDistrict;
    }

    public String getApplicantState() {
        return applicantState;
    }
}
