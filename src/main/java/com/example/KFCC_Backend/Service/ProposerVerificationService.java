package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.DTO.Membership.ProposerDetailsResponseDTO;
import com.example.KFCC_Backend.Entity.Membership.MembershipApplication;
import com.example.KFCC_Backend.Entity.Membership.ProposerVerification;
import com.example.KFCC_Backend.Enum.MembershipStatus;
import com.example.KFCC_Backend.ExceptionHandlers.BadRequestException;
import com.example.KFCC_Backend.ExceptionHandlers.ResourceNotFoundException;
import com.example.KFCC_Backend.Repository.Membership.MembershipRepository;
import com.example.KFCC_Backend.Repository.Membership.ProposerVerificationRepository;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class ProposerVerificationService {

    @Autowired
    private ProposerVerificationRepository proposerVerificationRepository;

    @Autowired
    private MembershipRepository membershipRepository;


    //send otp to proposer/seconder
    public String sendProposerOtp(CustomUserDetails applicant , String proposerMembershipId, ProposerVerification.EndorserType type){

        // rate limiting
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(15);

        long otpCount = proposerVerificationRepository
                .countByApplicantUserIdAndProposerMembershipIdAndEndorserTypeAndCreatedAtAfter(
                        applicant.getUserId(),
                        proposerMembershipId,
                        type,
                        windowStart
                );

        if (otpCount >= 3) {
            throw new BadRequestException(
                    "OTP limit exceeded. Please try again after 15 minutes."
            );
        }

        // Invalidate any previous unverified OTPs if present
        proposerVerificationRepository.findTopByApplicantUserIdAndProposerMembershipIdAndEndorserTypeAndIsVerifiedFalseOrderByCreatedAtDesc(
                        applicant.getUserId(),
                        proposerMembershipId,
                        type
                )
                .ifPresent(old -> {
                    old.setOtpExpiresAt(LocalDateTime.now());
                    proposerVerificationRepository.save(old);
                });

        MembershipApplication proposer =  membershipRepository.
                findbyMemberhsipIdAndIsValid(proposerMembershipId , MembershipStatus.FINAL_APPROVED , LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException("Membership ID is Not Valid"));

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        ProposerVerification verification = new ProposerVerification();
        verification.setApplicantUserId(applicant.getUserId());
        verification.setOtp(otp);
        verification.setProposerMembershipId(proposer.getMembershipId());
        verification.setOtpAttempts(0);
        verification.setOtpExpiresAt(LocalDateTime.now().plusMinutes(15));
        verification.setVerified(false);
        verification.setProposerUserId(proposer.getUser().getId());
        verification.setCreatedAt(LocalDateTime.now());
        verification.setEndorserType(type);

        proposerVerificationRepository.save(verification);

        // DEVELOPMENT ONLY (replace with SMS service later)
        System.out.println( type + proposer.getMembershipId() + " OTP : " + otp);

        return proposer.getUser().getMobileNo().substring(7);

    }

    public ProposerDetailsResponseDTO verifyProposerOtp(
            CustomUserDetails applicant,
            String proposerMembershipId,
            ProposerVerification.EndorserType type,
            String otp
    ){

        ProposerVerification verification = proposerVerificationRepository.findTopByApplicantUserIdAndProposerMembershipIdAndEndorserTypeAndIsVerifiedFalseOrderByCreatedAtDesc(
                        applicant.getUserId(), proposerMembershipId , type
                )
                .orElseThrow(() -> new ResourceNotFoundException("OTP Not found"));

        System.out.println("verification ID catched:" + verification.getId());

        if (verification.getOtpAttempts() >= 3) {
            throw new BadRequestException("Maximum OTP attempts exceeded");
        }

        if (verification.getOtpExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired");
        }

        if (!verification.getOtp().equals(otp)) {
            verification.setOtpAttempts(verification.getOtpAttempts() + 1);
            proposerVerificationRepository.save(verification);
            throw new BadRequestException("Invalid OTP");
        }

        verification.setVerified(true);
        verification.setVerifiedAt(LocalDateTime.now());
        proposerVerificationRepository.save(verification);

        MembershipApplication proposer = membershipRepository
                .findbyMemberhsipIdAndIsValid(proposerMembershipId , MembershipStatus.FINAL_APPROVED , LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));

        return mapToResponse(proposer);

    }


    private ProposerDetailsResponseDTO mapToResponse(MembershipApplication membership) {

        ProposerDetailsResponseDTO response = new ProposerDetailsResponseDTO();

        response.setFirstName(membership.getUser().getFirstName());
        response.setMiddleName(membership.getUser().getMiddleName());
        response.setLastName(membership.getUser().getLastName());
        response.setAddress(membership.getApplicantAddressLine1()+ "," + membership.getApplicantAddressLine2());
        response.setMobile(membership.getUser().getMobileNo());
        response.setDesignation(membership.getApplicantMembershipCategory());

        return response;
    }

}
