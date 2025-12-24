package com.example.KFCC_Backend.Controller;

import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Service.TitleRegistrationService;
import com.example.KFCC_Backend.entity.Title.TitleRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/titleRegistration")
public class TitleRegistrationController {

    @Autowired
    private TitleRegistrationService titleRegistrationService;

    @PreAuthorize("hasAnyRole('PRODUCER')")
    @RequestMapping("/apply")
    public ResponseEntity<?> submitTitleApplication( @RequestBody TitleRegistration request,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails){

        TitleRegistration application = titleRegistrationService.submitApplication(request , userDetails);

        return ResponseEntity.ok(Map.of("message" , "Title Registered Successfully" ,
                "application" , application)
        );

    }


}
