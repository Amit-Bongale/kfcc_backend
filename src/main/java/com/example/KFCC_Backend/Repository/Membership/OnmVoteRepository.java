package com.example.KFCC_Backend.Repository.Membership;

import com.example.KFCC_Backend.Entity.Membership.ONM.OnmMeeting;
import com.example.KFCC_Backend.Entity.Membership.ONM.OnmVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnmVoteRepository extends JpaRepository<OnmVote , Long> {

    @Query("""
        select count(v) > 0
        from OnmVote v
        where v.meeting.id = :meetingId
          and v.application.applicationId = :applicationId
          and v.voter.id = :voterId
    """)
    boolean existsVote(
            @Param("meetingId") Long meetingId,
            @Param("applicationId") Long applicationId,
            @Param("voterId") Long voterId
    );

    @Query("""
        select v
        from OnmVote v
        where v.meeting.id = :meetingId
          and v.application.applicationId = :applicationId
    """)
    List<OnmVote> findVotes(
            @Param("meetingId") Long meetingId,
            @Param("applicationId") Long applicationId
    );

    @Query("""
        SELECT v.vote as vote, COUNT(v) as count
        FROM OnmVote v
        WHERE v.meeting.id = :meetingId
          AND v.application.applicationId = :applicationId
        GROUP BY v.vote
    """)
    List<VoteCountProjection> getVoteSummary(
            @Param("meetingId") Long meetingId,
            @Param("applicationId") Long applicationId
    );


    void deleteByMeeting(OnmMeeting meeting);
}
