package com.example.KFCC_Backend.DTO.Membership;

import com.example.KFCC_Backend.Enum.MembershipCategory;
import com.example.KFCC_Backend.Enum.MembershipStatus;
import lombok.Data;


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

    public MembershipApplicationsResponseDTO(
            Long applicationId,
            Long userId,
            String applicantName,
            String mobileNo,
            MembershipCategory membershipCategory,
            MembershipStatus status,
            LocalDateTime submittedAt,
            String remark
    ) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.applicantName = applicantName;
        this.mobileNo = mobileNo;
        this.membershipCategory = membershipCategory;
        this.status = status;
        this.submittedAt = submittedAt;
        this.remark = remark;
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
}
