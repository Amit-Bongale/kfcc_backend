package com.example.KFCC_Backend.Controller;

import com.example.KFCC_Backend.DTO.IdCard.IdCardResponseDTO;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Service.IdCardRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/idcard")
public class IdCardRequestsController {

    @Autowired
    private IdCardRequestService idCardRequestService;

    //apply for Id cards
    @PostMapping(path = "/apply/{membershipApplicationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public IdCardResponseDTO applyForId(
            @PathVariable("membershipApplicationId") Long membershipApplicationId ,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestPart(value = "applicantPhoto", required = true ) MultipartFile applicantPhoto) throws IOException {

        return idCardRequestService.requestIdCard(membershipApplicationId , user  ,applicantPhoto);

    }

    //get all the ID card requests
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('STAFF')")
    public ResponseEntity<?> getRequests(){
        List<IdCardResponseDTO> requests =  idCardRequestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    //set status to be issued
    @PostMapping("/issued/{applicationId}")
    @PreAuthorize("hasAnyRole('STAFF')")
    public ResponseEntity<?> issueCard( @PathVariable Long applicationId){
        idCardRequestService.cardIssued(applicationId);
        return ResponseEntity.ok("Status Updated Successfully");
    }



}