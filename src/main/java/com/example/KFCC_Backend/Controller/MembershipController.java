package com.example.KFCC_Backend.Controller;

import com.example.KFCC_Backend.DTO.MembershipApplicationRequest;
import com.example.KFCC_Backend.Repository.MembershipRepository;
import com.example.KFCC_Backend.Repository.UsersRepository;

import com.example.KFCC_Backend.Service.MembershipApplicationService;
import com.example.KFCC_Backend.entity.Membership.MembershipApplication;
import com.example.KFCC_Backend.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

//    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> submitApplication(
//            @RequestPart("request") MembershipApplicationRequest request,
//            @RequestPart("applicantPhoto") MultipartFile applicantPhoto,
//            @RequestPart( value = "proprietorPan", required = false) MultipartFile proprietorPan,
//            @RequestPart( value = "proprietorESignature", required = false) MultipartFile proprietorESignature,
//            @RequestPart(value = "proprietorAadhaar", required = false) MultipartFile proprietorAadhaar
//    ) throws IOException {
//
//        Users user = usersRepository.findById(request.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        MembershipApplication app =
//                membershipApplicationService.submitApplication(
//                        user,
//                        request,
//                        applicantPhoto,
//                        proprietorPan,
//                        proprietorAadhaar,
//                        proprietorESignature
//                );
//
//        return ResponseEntity.ok(Map.of(
//                "applicationId", app.getApplicationId(),
//                "status", "SUBMITTED"
//        ));
//    }



    @GetMapping("/{applicationId}")
    public ResponseEntity<?> getMembershipApplicationById(
            @PathVariable Long applicationId
    ) {
        MembershipApplication application =
                membershipRepository.findByApplicationId(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException("Membership application not found"));

        return ResponseEntity.ok(application);
    }


    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitApplication(

            @RequestPart("request") MembershipApplicationRequest request,

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


}
