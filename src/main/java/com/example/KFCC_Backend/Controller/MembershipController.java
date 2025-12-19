package com.example.KFCC_Backend.Controller;

import com.example.KFCC_Backend.DTO.ApplicationActionRequestDTO;
import com.example.KFCC_Backend.DTO.MembershipApplicationRequestDTO;
import com.example.KFCC_Backend.Repository.MembershipRepository;
import com.example.KFCC_Backend.Repository.UsersRepository;
import com.example.KFCC_Backend.DTO.MembershipApplicationsResponseDTO;

import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Service.MembershipApplicationService;
import com.example.KFCC_Backend.entity.Membership.MembershipApplication;
import com.example.KFCC_Backend.entity.Users;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/membership")
public class MembershipController {

    @Autowired
    private MembershipApplicationService membershipApplicationService;

    @Autowired
    MembershipRepository membershipRepository;

    @Autowired
    private UsersRepository usersRepository;


    @GetMapping("/{applicationId}")
//    @PreAuthorize("hasAnyRole('USER','STAFF','ONM_COMMITTEE','ONM_COMMITTEE_LEADER','EC_MEMBER','SECRETARY','PRESIDENT')")
    public ResponseEntity<?> getMembershipApplicationById(
            @PathVariable Long applicationId
    ) {
        MembershipApplication application =
                membershipApplicationService.getApplicationDetailsByID(applicationId);

        return ResponseEntity.ok(application);
    }


    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitApplication(

            @RequestPart("request") MembershipApplicationRequestDTO request,

            // Applicant
            @RequestPart(value = "applicantPhoto", required = false ) MultipartFile applicantPhoto,
            @RequestPart(value = "applicantPan" , required = false) MultipartFile applicantPan,
            @RequestPart(value = "applicantAadhaar" , required = false ) MultipartFile applicantAadhaar,
            @RequestPart(value = "applicantAddressProof" , required = false) MultipartFile applicantAddressProof,
            @RequestPart(value = "applicantSignature" , required = false) MultipartFile applicantSignature,
            @RequestPart(value = "firmSeal" , required = false) MultipartFile firmSeal,

            // Proprietor (only if PROPRIETOR)
            @RequestPart(value = "proprietorPan", required = false) MultipartFile proprietorPan,
            @RequestPart(value = "proprietorAadhaar", required = false) MultipartFile proprietorAadhaar,
            @RequestPart(value = "proprietorESignature", required = false) MultipartFile proprietorESignature,

            // Partners (indexed, for NON-PROPRIETOR)
            @RequestPart(value = "partnerPan", required = false) MultipartFile[] partnerPan,
            @RequestPart(value = "partnerAadhaar", required = false) MultipartFile[] partnerAadhaar,
            @RequestPart(value = "partnerSignature", required = false) MultipartFile[] partnerSignature,

            // Ownership documents
            @RequestPart(value = "partnershipDeed", required = false) MultipartFile partnershipDeed,
            @RequestPart(value = "moa", required = false) MultipartFile moa,
            @RequestPart(value = "aoa", required = false) MultipartFile aoa

    ) throws IOException {

        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        MembershipApplication app =
                membershipApplicationService.submitApplication(
                        user,
                        request,
                        applicantPhoto,
                        applicantPan,
                        applicantAadhaar,
                        applicantAddressProof,
                        applicantSignature,
                        firmSeal,
                        proprietorPan,
                        proprietorAadhaar,
                        proprietorESignature,
                        partnerPan,
                        partnerAadhaar,
                        partnerSignature,
                        partnershipDeed,
                        moa,
                        aoa
                );

        return ResponseEntity.ok(Map.of(
                "applicationId", app.getApplicationId(),
                "status", "SUBMITTED"
        ));
    }


//    Fetch user submitted applications
    @GetMapping("/pending/requests")
    @PreAuthorize("hasAnyRole('STAFF','ONM_COMMITTEE', 'ONM_COMMITTEE_LEADER', 'EC_MEMBER','SECRETARY' , 'PRESIDENT' )")
    public ResponseEntity<List<MembershipApplicationsResponseDTO>> getPendingApplications(
            @AuthenticationPrincipal CustomUserDetails user) {

        System.out.println("user:" + user);
        return ResponseEntity.ok(
                membershipApplicationService.getPendingApplications(user)
        );
    }

    @PostMapping("/{id}/action")
    @PreAuthorize("hasAnyRole('STAFF','ONM_COMMITTEE', 'ONM_COMMITTEE_LEADER', 'EC_MEMBER','SECRETARY' , 'PRESIDENT' )")
    public ResponseEntity<?> ApplcationAction( @PathVariable Long id,
                                               @RequestBody @Valid ApplicationActionRequestDTO request,
                                               @AuthenticationPrincipal CustomUserDetails user){
        membershipApplicationService.MembershipApplicationAction(id , request, user);
        return ResponseEntity.ok(Map.of("message" , "Application Status Updated"));
    }



}
