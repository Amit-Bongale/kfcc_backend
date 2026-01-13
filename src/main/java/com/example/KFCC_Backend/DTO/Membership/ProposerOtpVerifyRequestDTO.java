package com.example.KFCC_Backend.DTO.Membership;

import com.example.KFCC_Backend.Entity.Membership.ProposerVerification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProposerOtpVerifyRequestDTO {

    private String proposerMembershipId;

    private ProposerVerification.EndorserType type;

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

    public ProposerVerification.EndorserType getType() {
        return type;
    }

    public void setType(ProposerVerification.EndorserType type) {
        this.type = type;
    }
}

