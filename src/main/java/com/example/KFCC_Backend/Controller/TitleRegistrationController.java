package com.example.KFCC_Backend.Controller;

import com.example.KFCC_Backend.DTO.Membership.ApplicationActionRequestDTO;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Service.TitleRegistrationService;
import com.example.KFCC_Backend.entity.Title.TitleRegistration;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/titleRegistration")
public class TitleRegistrationController {

    @Autowired
    private TitleRegistrationService titleRegistrationService;

    //get particular application details by ID
    @GetMapping("/{applicationId}")
    @PreAuthorize("hasAnyRole( 'PRODUCER' , 'STAFF','ONM_COMMITTEE_VOTER', 'ONM_COMMITTEE_LEADER', 'EC_MEMBER', 'SECRETARY' , 'PRESIDENT' )")
    public ResponseEntity<?> getTitleDetails(@PathVariable Long applicationId){

        TitleRegistration application = titleRegistrationService.getApplicationDetailsById(applicationId);

        return ResponseEntity.ok(application);

    }

    //Apply for title Registration
    @PreAuthorize("hasAnyRole('PRODUCER')")
    @RequestMapping("/apply")
    public ResponseEntity<?> submitTitleApplication( @RequestPart("request") TitleRegistration request,
                                                    @RequestPart("files") List<MultipartFile> files,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {

        TitleRegistration application = titleRegistrationService.submitApplication(request , userDetails , files);

        return ResponseEntity.ok(Map.of("message" , "Title Registered Successfully" ,
                "application" , application)
        );

    }


    // Approve / Reject /  Remark Applications for all Roles
    @PostMapping("/{id}/action")
    @PreAuthorize("hasAnyRole('STAFF', 'TITLE_COMMITTEE_LEADER' ,'SECRETARY' , 'PRESIDENT' )")
    public ResponseEntity<?> applicationAction( @PathVariable Long id,
                                               @RequestBody @Valid ApplicationActionRequestDTO request,
                                               @AuthenticationPrincipal CustomUserDetails user){
        titleRegistrationService.TitleApplicationAction(id , request, user);
        return ResponseEntity.ok(Map.of("message" , "Application Status Updated"));
    }

}
