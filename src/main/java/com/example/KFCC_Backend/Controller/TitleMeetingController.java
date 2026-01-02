package com.example.KFCC_Backend.Controller;

import com.example.KFCC_Backend.DTO.Meeting.AddMembersToMeetingDTO;
import com.example.KFCC_Backend.DTO.Meeting.CastVoteRequestDTO;
import com.example.KFCC_Backend.DTO.Meeting.VoteSummaryResponseDTO;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Service.TitleMeetingService;
import com.example.KFCC_Backend.Entity.Title.TitleMeeting;
import com.example.KFCC_Backend.Entity.Users;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/title/meetings")
public class TitleMeetingController {

    @Autowired
    private TitleMeetingService titleMeetingService;

    //Fetch all TITLE meetings
    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','TITLE_COMMITTEE_LEADER','TITLE_COMMITTEE_VOTER' , 'MANAGER')")
    public ResponseEntity<List<TitleMeeting>> getAllMeetings() {
        return ResponseEntity.ok(
                titleMeetingService.getAllMeetings()
        );
    }

    // create ONM meeting and appoint Leader using User Id
    @PostMapping("/create/{leaderId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createMeeting(@PathVariable Long leaderId,
                                           @AuthenticationPrincipal CustomUserDetails user) {

        TitleMeeting meeting =  titleMeetingService.CreateMeeting(leaderId, user);

        return ResponseEntity.ok(
                Map.of(
                        "meetingId", meeting.getId(),
                        "status", meeting.getStatus(),
                        "leaderId", meeting.getLeader().getId()
                )
        );
    }


    //Fetch meetings by status
    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('STAFF','TITLE_COMMITTEE_LEADER','TITLE_COMMITTEE_VOTER', 'MANAGER' )")
    public ResponseEntity<List<TitleMeeting>> getMeetingsByStatus(
            @RequestParam TitleMeeting.MeetingStatus status ) {
        return ResponseEntity.ok(
                titleMeetingService.getMeetingsByStatus(status)
        );
    }

    // Fetch all valid members to add for title meeting
    @GetMapping("/allMembers")
    @PreAuthorize("hasAnyRole('TITLE_COMMITTEE_LEADER' , 'MANAGER')")
    public ResponseEntity<?> getAllMembers(){
        List<Users> members = titleMeetingService.findAllMembers();
        return  ResponseEntity.of(Optional.ofNullable(members));
    }


    //Add members to meeting
    @PostMapping("/{meetingId}/addMembers")
    @PreAuthorize("hasRole('TITLE_COMMITTEE_LEADER')")
    public ResponseEntity<?> addMembers( @PathVariable Long meetingId, @RequestBody @Valid AddMembersToMeetingDTO request,
                                         @AuthenticationPrincipal CustomUserDetails user) {

        System.out.println("add members route");

        titleMeetingService.addMembers(meetingId, request, user);

        return ResponseEntity.ok(
                Map.of("message", "Members added successfully")
        );
    }


    //perform voting
    @PostMapping("/{meetingId}/vote")
    @PreAuthorize("hasRole('TITLE_COMMITTEE_VOTER')")
    public ResponseEntity<?> castVote(
            @PathVariable Long meetingId,
            @RequestBody @Valid CastVoteRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        titleMeetingService.castVote(meetingId, request, user);

        return ResponseEntity.ok(
                Map.of("message", "Vote submitted successfully")
        );
    }


    // get application Votes summary
    @GetMapping("/{meetingId}/votes/{applicationId}")
    @PreAuthorize("hasRole('TITLE_COMMITTEE_LEADER')")
    public ResponseEntity<VoteSummaryResponseDTO> getVoteSummary(
            @PathVariable Long meetingId,
            @PathVariable Long applicationId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        VoteSummaryResponseDTO response =
                titleMeetingService.getVoteSummary(
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

        titleMeetingService.terminateMeeting(meetingId, user);

        return ResponseEntity.ok(
                Map.of("message", "Meeting terminated successfully")
        );
    }




}
