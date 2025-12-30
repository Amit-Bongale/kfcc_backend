package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.DTO.Meeting.AddMembersToMeetingDTO;
import com.example.KFCC_Backend.DTO.Meeting.CastVoteRequestDTO;
import com.example.KFCC_Backend.DTO.Meeting.VoteSummaryResponseDTO;
import com.example.KFCC_Backend.Enum.TitleApplicationStatus;
import com.example.KFCC_Backend.Enum.UserRoles;
import com.example.KFCC_Backend.Enum.VoteDecision;
import com.example.KFCC_Backend.ExceptionHandlers.BadRequestException;
import com.example.KFCC_Backend.ExceptionHandlers.ResourceNotFoundException;
import com.example.KFCC_Backend.Repository.Membership.MembershipRepository;
import com.example.KFCC_Backend.Repository.Membership.VoteCountProjection;
import com.example.KFCC_Backend.Repository.Title.TItleMeetingMembersRepository;
import com.example.KFCC_Backend.Repository.Title.TitleMeetingRepository;
import com.example.KFCC_Backend.Repository.Title.TitleRegistrationRepository;
import com.example.KFCC_Backend.Repository.Title.TitleVoteRepository;
import com.example.KFCC_Backend.Repository.Users.UserRoleRepository;
import com.example.KFCC_Backend.Repository.Users.UsersRepository;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;

import com.example.KFCC_Backend.entity.Title.TitleMeeting;
import com.example.KFCC_Backend.entity.Title.TitleMeetingMembers;
import com.example.KFCC_Backend.entity.Title.TitleRegistration;
import com.example.KFCC_Backend.entity.Title.TitleVote;
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
public class TitleMeetingService {

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private TitleRegistrationRepository titleRegistrationRepository;

    @Autowired
    private TitleMeetingRepository titleMeetingRepository;

    @Autowired
    private TItleMeetingMembersRepository tItleMeetingMembersRepository;

    @Autowired
    private TitleVoteRepository titleVoteRepository;

    // Create TITLE Meeting & appoints leader
    @Transactional(rollbackOn = Exception.class)
    public TitleMeeting CreateMeeting(Long leaderId , CustomUserDetails userDetails){

        if(titleMeetingRepository.existsByStatus(TitleMeeting.MeetingStatus.ACTIVE)){
            throw new BadRequestException("Meeting Already Exists");
        }

        Users manager = usersRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Users leader = usersRepository.findById(leaderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserRole userRole = new UserRole();
        userRole.setRole(UserRoles.TITLE_COMMITTEE_LEADER);
        userRole.setUser(leader);
        leader.getRoles().add(userRole);
        usersRepository.save(leader);

        TitleMeeting meeting = new TitleMeeting();
        meeting.setMeetingDate(LocalDate.now());
        meeting.setLeader(leader);
        meeting.setCreatedBy(manager);
        meeting.setStatus(TitleMeeting.MeetingStatus.ACTIVE);
        meeting.setCreatedAt(LocalDateTime.now());

        return titleMeetingRepository.save(meeting);

    }

    //Fetch all meetings
    public List<TitleMeeting> getAllMeetings() {
        Pageable pageable = PageRequest.of(0, 30);
        Page<TitleMeeting> page =
                titleMeetingRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<TitleMeeting> meetings = page.getContent();
        return meetings;
    }

    // Fetch meetings by status
    public List<TitleMeeting> getMeetingsByStatus(TitleMeeting.MeetingStatus status) {
        return titleMeetingRepository.findByStatusOrderByCreatedAtDesc(status);
    }


    // Add Members to Meeting
    @Transactional
    public void addMembers(Long meetingId, AddMembersToMeetingDTO request, CustomUserDetails userDetails) {

        TitleMeeting meeting = titleMeetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting not found") );

        System.out.println("meetings: " + meeting);

        System.out.println("requested member IDs: " + request);

        //  Validate meeting is active
        if (meeting.getStatus() != TitleMeeting.MeetingStatus.ACTIVE) {
            throw new ResourceNotFoundException("Meeting is not active");
        }

        //  Validate leader
        if (!meeting.getLeader().getId().equals(userDetails.getUserId())) {
            throw new BadRequestException("Only leader can add members");
        }

        // Add members
        for (Long memberId : request.getMemberIds()) {

            System.out.println("trying to add members: " + memberId);

            // Leader cannot add himself
            if (memberId.equals(meeting.getLeader().getId())) {
                continue;
            }

            // Avoid duplicates
            if (tItleMeetingMembersRepository.existsByMeetingIdAndMemberId(meetingId, memberId)) {
                continue;
            }

            Users member = usersRepository.findById(memberId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + memberId));

            boolean exists = userRoleRepository
                    .existsByUserAndRole(member, UserRoles.ONM_COMMITTEE_VOTER);

            if (exists) {
                throw new BadRequestException("User already has this role");
            }

            UserRole role = new UserRole();
            role.setUser(member);
            role.setRole(UserRoles.TITLE_COMMITTEE_VOTER);
            userRoleRepository.save(role);

            TitleMeetingMembers meetingMember = new TitleMeetingMembers();
            meetingMember.setMeeting(meeting);
            meetingMember.setMember(member);

            tItleMeetingMembersRepository.save(meetingMember);
        }
    }


    //submit vote for application
    @Transactional
    public void castVote(
            Long meetingId,
            CastVoteRequestDTO request,
            CustomUserDetails userDetails
    ) {

        // Fetch meeting
        TitleMeeting meeting = titleMeetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting not found"));

        //Fetch application
        TitleRegistration application = titleRegistrationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application does not Exists"));

        if(application.getStatus() != TitleApplicationStatus.STAFF_APPROVED){
            throw new BadRequestException("Application is not in Voting Stage"
            );
        }

        if (meeting.getStatus() != TitleMeeting.MeetingStatus.ACTIVE) {
            throw new ResourceNotFoundException("Meeting is not active");
        }

        // Leader should not vote
        if (meeting.getLeader().getId().equals(userDetails.getUserId())) {
            throw new BadRequestException("Leader cannot vote");
        }

        // Validate meeting membership
        boolean isMember =
                tItleMeetingMembersRepository.existsByMeetingIdAndMemberId( meetingId, userDetails.getUserId());

        if (!isMember) {
            throw new AccessDeniedException("You are not part of this meeting");
        }


        // Prevent double vote
        if (titleVoteRepository.existsVote(
                meetingId,
                request.getApplicationId(),
                userDetails.getUserId()
        )) {
            throw new BadRequestException("You have already voted");
        }

        // Save vote
        Users voter = usersRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TitleVote vote = new TitleVote();
        vote.setMeeting(meeting);
        vote.setApplication(application);
        vote.setVoter(voter);
        vote.setVote(request.getVote());

        titleVoteRepository.save(vote);
    }


    @Transactional
    public VoteSummaryResponseDTO getVoteSummary(
            Long meetingId,
            Long applicationId,
            CustomUserDetails userDetails
    ) {

        // Fetch meeting
        TitleMeeting meeting = titleMeetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meeting not found")
                );

        // Validate ACTIVE
        if (meeting.getStatus() != TitleMeeting.MeetingStatus.ACTIVE) {
            throw new BadRequestException("Meeting is not active");
        }

        // Leader-only access
        if (!meeting.getLeader().getId().equals(userDetails.getUserId())) {
            throw new AccessDeniedException("Only leader can view vote summary");
        }

        //  Fetch votes
        List<VoteCountProjection> votes =
                titleVoteRepository.getVoteSummary(
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
        TitleMeeting meeting = titleMeetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meeting not found")
                );

        //  Validate ACTIVE
        if (meeting.getStatus() != TitleMeeting.MeetingStatus.ACTIVE) {
            throw new BadRequestException("Meeting already terminated");
        }

        //  Collect leader & members BEFORE delete
        Long leaderId = meeting.getLeader().getId();

        List<TitleMeetingMembers> members =
                tItleMeetingMembersRepository.findByMeetingId(meetingId);

        Set<Long> memberUserIds = members.stream()
                .map(m -> m.getMember().getId())
                .collect(Collectors.toSet());

        // Remove temporary ONM roles
        userRoleRepository.deleteByUserIdsAndRole(
                Set.of(leaderId),
                UserRoles.TITLE_COMMITTEE_LEADER
        );

        if (!memberUserIds.isEmpty()) {
            userRoleRepository.deleteByUserIdsAndRole(
                    memberUserIds,
                    UserRoles.TITLE_COMMITTEE_VOTER
            );
        }


        // Update meeting status
        meeting.setStatus(TitleMeeting.MeetingStatus.TERMINATED);
        meeting.setTerminatedAt(LocalDateTime.now());
        titleMeetingRepository.save(meeting);

        //  Cleanup temporary data
        tItleMeetingMembersRepository.deleteByMeeting(meeting);
        titleVoteRepository.deleteByMeeting(meeting);
    }



    //Auto Delete Meeting at end of the day
    @Scheduled(cron = "0 0 23 * * ?")
    @Transactional
    public void autoTerminateMeetings() {

        List<TitleMeeting> activeMeetings =
                titleMeetingRepository.findByStatus(TitleMeeting.MeetingStatus.ACTIVE);

        for (TitleMeeting meeting : activeMeetings) {
            meeting.setStatus(TitleMeeting.MeetingStatus.TERMINATED);
            meeting.setTerminatedAt(LocalDateTime.now());
            tItleMeetingMembersRepository.deleteByMeeting(meeting);
            titleVoteRepository.deleteByMeeting(meeting);
        }

        userRoleRepository.deleteByRole(UserRoles.ONM_COMMITTEE_LEADER);
    }

}
