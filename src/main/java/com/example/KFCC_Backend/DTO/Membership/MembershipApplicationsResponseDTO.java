package com.example.KFCC_Backend.DTO.Membership;

import com.example.KFCC_Backend.Enum.MembershipCategory;
import com.example.KFCC_Backend.Enum.MembershipStatus;
import lombok.Data;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MembershipApplicationsResponseDTO {

    private Long applicationId;
    private Long userId;
    private String applicantName;
    private String mobileNo;
    private MembershipCategory membershipCategory;
    private MembershipStatus status;
    private LocalDateTime submittedAt;
    private String remark;
    private String remarkedBy;
    private LocalDate expiryDate;
    private String membershipId;
    private LocalDate acceptanceDate;

    public MembershipApplicationsResponseDTO(
            Long applicationId,
            Long userId,
            String applicantName,
            String mobileNo,
            MembershipCategory membershipCategory,
            MembershipStatus status,
            LocalDateTime submittedAt,
            String remark,
            String remarkedBy,
            LocalDate expiryDate,
            String membershipId,
            LocalDate acceptanceDate
    ) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.applicantName = applicantName;
        this.mobileNo = mobileNo;
        this.membershipCategory = membershipCategory;
        this.status = status;
        this.submittedAt = submittedAt;
        this.remark = remark;
        this.remarkedBy = remarkedBy;
        this.expiryDate = expiryDate;
        this.membershipId = membershipId;
        this.acceptanceDate = acceptanceDate;
    }


    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public MembershipCategory getMembershipCategory() {
        return membershipCategory;
    }

    public void setMembershipCategory(MembershipCategory membershipCategory) {
        this.membershipCategory = membershipCategory;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemarkedBy() {
        return remarkedBy;
    }

    public void setRemarkedBy(String remarkedBy) {
        this.remarkedBy = remarkedBy;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public LocalDate getAcceptanceDate() {
        return acceptanceDate;
    }

    public void setAcceptanceDate(LocalDate acceptanceDate) {
        this.acceptanceDate = acceptanceDate;
    }
}
