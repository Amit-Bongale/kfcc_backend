package com.example.KFCC_Backend.DTO.Membership;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProposerOtpVerifyRequestDTO {

    private String proposerMembershipId;

    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    private String otp;

    public String getProposerMembershipId() {
        return proposerMembershipId;
    }

    public void setProposerMembershipId(String proposerMembershipId) {
        this.proposerMembershipId = proposerMembershipId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}

