package com.example.KFCC_Backend.Controller;

import com.example.KFCC_Backend.DTO.Membership.ApplicationActionRequestDTO;
import com.example.KFCC_Backend.DTO.PublicityClearance.PublicityClearanceRequestDTO;
import com.example.KFCC_Backend.DTO.PublicityClearance.PublicityClearanceResponseDTO;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Service.PublicityClearanceService;
import com.example.KFCC_Backend.Entity.PublicityClearance.PublicityClearanceApplication;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/publicityClearance")
public class PublicityClearanceController {

    @Autowired
    private PublicityClearanceService publicityClearanceService;


    // fetch titles for Publicity clearance [clearance applied and not applied Titles]
    @PreAuthorize("hasRole('PRODUCER')")
    @GetMapping("/producer/titles/publicity-status")
    public ResponseEntity<List<PublicityClearanceResponseDTO>> getTitlesWithPublicityStatus(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(
                publicityClearanceService
                        .getEligibleTitlesForClearance(user)
        );
    }


    // Apply for Publicity Clearance
    @PostMapping( value = "{titleId}/apply" ,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    public ResponseEntity<?> applyClearance(
            @PathVariable Long titleId,
            @ModelAttribute PublicityClearanceRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails user) throws IOException {

        PublicityClearanceApplication app = publicityClearanceService.applyClearance( titleId, request, user);
        return ResponseEntity.ok(
                Map.of(
                        "applicationId", app,
                        "message", "Publicity clearance application submitted successfully"
                )
        );

    }


    //get particular application details by ID
    @GetMapping("/{applicationId}")
    @PreAuthorize("hasAnyRole( 'PRODUCER' , 'STAFF','VP_PRODUCER', 'SECRETARY' , 'MANAGER' )")
    public ResponseEntity<?> getTitleDetails(
            @PathVariable Long applicationId , @AuthenticationPrincipal CustomUserDetails user){

        PublicityClearanceApplication application = publicityClearanceService.getApplicationDetailsById(applicationId , user);

        return ResponseEntity.ok(application);

    }


    // Fetch submitted applications for all Roles
    @GetMapping("/pending/requests")
    @PreAuthorize("hasAnyRole('STAFF','VP_PRODUCER', 'SECRETARY' , 'MANAGER' )")
    public ResponseEntity<List<PublicityClearanceResponseDTO>> getPendingApplications(
            @AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(
                publicityClearanceService.getPendingApplications(user)
        );
    }


    // Approve / Reject /  Remark Application for all Roles
    @PostMapping("/{id}/action")
    @PreAuthorize("hasAnyRole('STAFF', 'VP_PRODUCER' ,'SECRETARY' , 'MANAGER' )")
    public ResponseEntity<?> applicationAction( @PathVariable Long id,
                                               @RequestBody @Valid ApplicationActionRequestDTO request,
                                               @AuthenticationPrincipal CustomUserDetails user){
        publicityClearanceService.applicationAction(id , request, user);
        return ResponseEntity.ok(Map.of("message" , "Application Status Updated"));
    }



}