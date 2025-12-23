package com.example.KFCC_Backend.Controller;

import com.example.KFCC_Backend.DTO.ONMMeeting.AddMembersToMeetingDTO;
import com.example.KFCC_Backend.DTO.ONMMeeting.CastVoteRequestDTO;
import com.example.KFCC_Backend.DTO.ONMMeeting.VoteSummaryResponseDTO;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Service.OnmMeetingService;
import com.example.KFCC_Backend.entity.ONM.OnmMeeting;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/onm/meetings")
public class OnmMeetingController {

    @Autowired
    private OnmMeetingService meetingService;

    @PostMapping("/create/{leaderId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createMeeting( @PathVariable Long leaderId,
                                            @AuthenticationPrincipal CustomUserDetails user) {

        OnmMeeting meeting =  meetingService.CreateMeeting(leaderId, user);

        return ResponseEntity.ok(
                Map.of(
                        "meetingId", meeting.getId(),
                        "status", meeting.getStatus(),
                        "leaderId", meeting.getLeader().getId()
                )
        );
    }

    //Fetch all ONM meetings
    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','ONM_COMMITTEE_LEADER','ONM_COMMITTEE_VOTER' , 'MANAGER')")
    public ResponseEntity<List<OnmMeeting>> getAllMeetings() {
        return ResponseEntity.ok(
                meetingService.getAllMeetings()
        );
    }

    //Fetch meetings by status
    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('STAFF','ONM_COMMITTEE_LEADER','ONM_COMMITTEE_VOTER', 'MANAGER' )")
    public ResponseEntity<List<OnmMeeting>> getMeetingsByStatus(
            @RequestParam OnmMeeting.MeetingStatus status ) {
        return ResponseEntity.ok(
                meetingService.getMeetingsByStatus(status)
        );
    }



    @PostMapping("/{meetingId}/addMembers")
    @PreAuthorize("hasRole('ONM_COMMITTEE_LEADER')")
    public ResponseEntity<?> addMembers( @PathVariable Long meetingId, @RequestBody @Valid AddMembersToMeetingDTO request,
            @AuthenticationPrincipal CustomUserDetails user) {

        System.out.println("add members route");

        meetingService.addMembers(meetingId, request, user);

        return ResponseEntity.ok(
                Map.of("message", "Members added successfully")
        );
    }

    @PostMapping("/{meetingId}/vote")
    @PreAuthorize("hasRole('ONM_COMMITTEE')")
    public ResponseEntity<?> castVote(
            @PathVariable Long meetingId,
            @RequestBody @Valid CastVoteRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        meetingService.castVote(meetingId, request, user);

        return ResponseEntity.ok(
                Map.of("message", "Vote submitted successfully")
        );
    }


    // get application Votes
    @GetMapping("/{meetingId}/votes/{applicationId}")
    @PreAuthorize("hasRole('ONM_COMMITTEE_LEADER')")
    public ResponseEntity<VoteSummaryResponseDTO> getVoteSummary(
            @PathVariable Long meetingId,
            @PathVariable Long applicationId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        VoteSummaryResponseDTO response =
                meetingService.getVoteSummary(
                        meetingId, applicationId, user
                );

        return ResponseEntity.ok(response);
    }

    //terminates meeting
    @PostMapping("/{meetingId}/terminate")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> terminateMeeting(
            @PathVariable Long meetingId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        meetingService.terminateMeeting(meetingId, user);

        return ResponseEntity.ok(
                Map.of("message", "Meeting terminated successfully")
        );

    }

}

