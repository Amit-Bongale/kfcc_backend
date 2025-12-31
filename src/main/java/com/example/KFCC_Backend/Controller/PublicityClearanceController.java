package com.example.KFCC_Backend.Controller;

import com.example.KFCC_Backend.DTO.PublicityClearanceRequestDTO;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Service.PublicityClearanceService;
import com.example.KFCC_Backend.entity.PublicityClearance.PublicityClearanceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/publicityClearance")
public class PublicityClearanceController {

    @Autowired
    private PublicityClearanceService publicityClearanceService;

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
