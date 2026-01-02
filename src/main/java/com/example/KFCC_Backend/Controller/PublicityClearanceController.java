package com.example.KFCC_Backend.Controller;

import com.example.KFCC_Backend.DTO.PublicityClearance.PublicityClearanceRequestDTO;
import com.example.KFCC_Backend.DTO.PublicityClearance.TitleWithPublicityStatusDTO;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Service.PublicityClearanceService;
import com.example.KFCC_Backend.entity.PublicityClearance.PublicityClearanceApplication;
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

    // fetch titleApplications for clearance
    @PreAuthorize("hasRole('PRODUCER')")
    @GetMapping("/producer/titles/publicity-status")
    public ResponseEntity<List<TitleWithPublicityStatusDTO>> getTitlesWithPublicityStatus(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(
                publicityClearanceService
                        .getEligibleTitlesForClearance(user)
        );
    }


    // Apply for Publicity Clearance
    @PostMapping( value = "{titleId}/apply" ,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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



}
