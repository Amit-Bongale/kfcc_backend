package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.DTO.ONMMeeting.AddMembersToMeetingDTO;
import com.example.KFCC_Backend.DTO.ONMMeeting.CastVoteRequestDTO;
import com.example.KFCC_Backend.DTO.ONMMeeting.VoteSummaryResponseDTO;
import com.example.KFCC_Backend.Enum.MembershipStatus;
import com.example.KFCC_Backend.Enum.UserRoles;
import com.example.KFCC_Backend.Enum.VoteDecision;
import com.example.KFCC_Backend.ExceptionHandlers.BadRequestException;
import com.example.KFCC_Backend.ExceptionHandlers.ResourceNotFoundException;
import com.example.KFCC_Backend.Repository.*;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.entity.Membership.MembershipApplication;
import com.example.KFCC_Backend.entity.Membership.ONM.OnmMeeting;
import com.example.KFCC_Backend.entity.Membership.ONM.OnmMeetingMember;
import com.example.KFCC_Backend.entity.Membership.ONM.OnmVote;
import com.example.KFCC_Backend.entity.UserRole;
import com.example.KFCC_Backend.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OnmMeetingService {
    @Autowired
    private OnmMeetingRepository onmMeetingRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private OnmMeetingMemberRepository onmMeetingMemberRepository;

    @Autowired
    private OnmVoteRepository onmVoteRepository;

    @Autowired
    private MembershipRepository membershipRepository;


    // Create ONM Meeting & appoints leader
    @Transactional
    public OnmMeeting CreateMeeting(Long leaderId , CustomUserDetails userDetails){

        if(onmMeetingRepository.existsByStatus(OnmMeeting.MeetingStatus.ACTIVE)){
            throw new IllegalArgumentException("Meeting Already Exists");
        }

        Users manager = usersRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Users leader = usersRepository.findById(leaderId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserRole userRole = new UserRole();
        userRole.setRole(UserRoles.ONM_COMMITTEE_LEADER);
        userRole.setUser(leader);
        leader.getRoles().add(userRole);
        usersRepository.save(leader);

        OnmMeeting meeting = new OnmMeeting();
        meeting.setMeetingDate(LocalDate.now());
        meeting.setLeader(leader);
        meeting.setCreatedBy(manager);
        meeting.setStatus(OnmMeeting.MeetingStatus.ACTIVE);
        meeting.setCreatedAt(LocalDateTime.now());

        return onmMeetingRepository.save(meeting);

    }

    //Fetch all meetings
    public List<OnmMeeting> getAllMeetings() {
        Pageable pageable = PageRequest.of(0, 30);
        Page<OnmMeeting> page =
                onmMeetingRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<OnmMeeting> meetings = page.getContent();
        return meetings;
    }

    // Fetch meetings by status
    public List<OnmMeeting> getMeetingsByStatus(OnmMeeting.MeetingStatus status) {
        return onmMeetingRepository.findByStatusOrderByCreatedAtDesc(status);
    }



    // Add Members to Meeting
    @Transactional
    public void addMembers(Long meetingId, AddMembersToMeetingDTO request, CustomUserDetails userDetails) {

        OnmMeeting meeting = onmMeetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalStateException("Meeting not found") );

        System.out.println("meetings: " + meeting);

        System.out.println("requested member IDs: " + request);

        //  Validate meeting is active
        if (meeting.getStatus() != OnmMeeting.MeetingStatus.ACTIVE) {
            System.out.println("meeting not active");
            throw new IllegalStateException("Meeting is not active");
        }

        //  Validate leader
        if (!meeting.getLeader().getId().equals(userDetails.getUserId())) {
            System.out.println("User is not leader");
            throw new AccessDeniedException("Only leader can add members");
        }

        System.out.println("add members process init");

        // 4. Add members
        for (Long memberId : request.getMemberIds()) {

            System.out.println("trying to add members: " + memberId);

            // Leader cannot add himself
            if (memberId.equals(meeting.getLeader().getId())) {
                continue;
            }

            // Avoid duplicates
            if (onmMeetingMemberRepository.existsByMeetingIdAndMemberId(meetingId, memberId)) {
                continue;
            }

            Users member = usersRepository.findById(memberId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("User not found: " + memberId)
                    );

            // validate ONM role
            // if (!member.hasRole(UserRole.ONM)) { throw ... }

            boolean exists = userRoleRepository
                    .existsByUserAndRole(member, UserRoles.ONM_COMMITTEE_VOTER);

            if (exists) {
                throw new IllegalStateException("User already has this role");
            }

            UserRole role = new UserRole();
            role.setUser(member);
            role.setRole(UserRoles.ONM_COMMITTEE_VOTER);
            userRoleRepository.save(role);

            OnmMeetingMember meetingMember = new OnmMeetingMember();
            meetingMember.setMeeting(meeting);
            meetingMember.setMember(member);

            onmMeetingMemberRepository.save(meetingMember);
        }


    }

    @Transactional
    public void castVote(
            Long meetingId,
            CastVoteRequestDTO request,
            CustomUserDetails userDetails
    ) {

        // Fetch meeting
        OnmMeeting meeting = onmMeetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalStateException("Meeting not found"));

        //Fetch application
        MembershipApplication application = membershipRepository.findByApplicationId(request.getApplicationId())
                .orElseThrow(() -> new IllegalArgumentException("Application does not Exists"));

        if(application.getMembershipStatus() != MembershipStatus.STAFF_APPROVED){
            throw new IllegalStateException("Application is not in Voting Stage"
            );
        }

        if (meeting.getStatus() != OnmMeeting.MeetingStatus.ACTIVE) {
            throw new IllegalStateException("Meeting is not active");
        }

        // Leader should not vote
        if (meeting.getLeader().getId().equals(userDetails.getUserId())) {
            throw new IllegalStateException("Leader cannot vote");
        }

        // Validate meeting membership
        boolean isMember =
                onmMeetingMemberRepository.existsByMeetingIdAndMemberId( meetingId, userDetails.getUserId());

        if (!isMember) {
            throw new AccessDeniedException("You are not part of this meeting");
        }


        // Prevent double vote
        if (onmVoteRepository.existsVote(
                meetingId,
                request.getApplicationId(),
                userDetails.getUserId()
        )) {
            throw new IllegalStateException("You have already voted");
        }

        // Save vote
        Users voter = usersRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        OnmVote vote = new OnmVote();
        vote.setMeeting(meeting);
        vote.setApplication(application);
        vote.setVoter(voter);
        vote.setVote(request.getVote());

        onmVoteRepository.save(vote);
    }


    @Transactional
    public VoteSummaryResponseDTO getVoteSummary(
            Long meetingId,
            Long applicationId,
            CustomUserDetails userDetails
    ) {

        // Fetch meeting
        OnmMeeting meeting = onmMeetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meeting not found")
                );

        // Validate ACTIVE
        if (meeting.getStatus() != OnmMeeting.MeetingStatus.ACTIVE) {
            throw new BadRequestException("Meeting is not active");
        }

        // Leader-only access
        if (!meeting.getLeader().getId().equals(userDetails.getUserId())) {
            throw new AccessDeniedException("Only leader can view vote summary");
        }

        //  Fetch votes
        List<VoteCountProjection> votes =
                onmVoteRepository.getVoteSummary(
                        meetingId, applicationId
                );

        long approve = 0;
        long reject = 0;

        for (VoteCountProjection c : votes) {
            if (c.getVote() == VoteDecision.APPROVE) {
                approve = c.getCount();
            } else if (c.getVote() == VoteDecision.REJECT) {
                reject = c.getCount();
            }
        }

        return new VoteSummaryResponseDTO(applicationId, approve, reject);
    }

    //Terminates meeting and remove onm leader role
    @Transactional
    public void terminateMeeting(
            Long meetingId,
            CustomUserDetails userDetails
    ) {

        // Fetch manager
        Users manager = usersRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        // Fetch meeting
        OnmMeeting meeting = onmMeetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meeting not found")
                );

        //  Validate ACTIVE
        if (meeting.getStatus() != OnmMeeting.MeetingStatus.ACTIVE) {
            throw new BadRequestException("Meeting already terminated");
        }

        //  Collect leader & members BEFORE delete
        Long leaderId = meeting.getLeader().getId();

        List<OnmMeetingMember> members =
                onmMeetingMemberRepository.findByMeetingId(meetingId);

        Set<Long> memberUserIds = members.stream()
                .map(m -> m.getMember().getId())
                .collect(Collectors.toSet());

        // Remove temporary ONM roles
        userRoleRepository.deleteByUserIdsAndRole(
                Set.of(leaderId),
                UserRoles.ONM_COMMITTEE_LEADER
        );

        if (!memberUserIds.isEmpty()) {
            userRoleRepository.deleteByUserIdsAndRole(
                    memberUserIds,
                    UserRoles.ONM_COMMITTEE_VOTER
            );
        }


        // Update meeting status
        meeting.setStatus(OnmMeeting.MeetingStatus.TERMINATED);
        meeting.setTerminatedAt(LocalDateTime.now());
        onmMeetingRepository.save(meeting);

        //  Cleanup temporary data
        onmMeetingMemberRepository.deleteByMeeting(meeting);
        onmVoteRepository.deleteByMeeting(meeting);
    }


    //Auto Delete Meeting at end of the day
    @Scheduled(cron = "0 0 23 * * ?")
    @Transactional
    public void autoTerminateMeetings() {

        List<OnmMeeting> activeMeetings =
                onmMeetingRepository.findByStatus(OnmMeeting.MeetingStatus.ACTIVE);

        for (OnmMeeting meeting : activeMeetings) {
            meeting.setStatus(OnmMeeting.MeetingStatus.TERMINATED);
            meeting.setTerminatedAt(LocalDateTime.now());
            onmMeetingMemberRepository.deleteByMeeting(meeting);
            onmVoteRepository.deleteByMeeting(meeting);

        }

        userRoleRepository.deleteByRole(UserRoles.ONM_COMMITTEE_LEADER);
    }


}
