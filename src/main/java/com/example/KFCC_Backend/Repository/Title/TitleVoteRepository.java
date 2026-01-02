package com.example.KFCC_Backend.Repository.Title;

import com.example.KFCC_Backend.Repository.Membership.VoteCountProjection;
import com.example.KFCC_Backend.Entity.Membership.ONM.OnmVote;
import com.example.KFCC_Backend.Entity.Title.TitleMeeting;
import com.example.KFCC_Backend.Entity.Title.TitleVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TitleVoteRepository extends JpaRepository<TitleVote , Long> {

    @Query("""
        select count(v) > 0
        from TitleVote v
        where v.meeting.id = :meetingId
          and v.application.id = :applicationId
          and v.voter.id = :voterId
    """)
    boolean existsVote(
            @Param("meetingId") Long meetingId,
            @Param("applicationId") Long applicationId,
            @Param("voterId") Long voterId
    );

    @Query("""
        select v
        from TitleVote v
        where v.meeting.id = :meetingId
          and v.application.id = :applicationId
    """)
    List<OnmVote> findVotes(
            @Param("meetingId") Long meetingId,
            @Param("applicationId") Long applicationId
    );

    @Query("""
        SELECT v.vote as vote, COUNT(v) as count
        FROM TitleVote v
        WHERE v.meeting.id = :meetingId
        AND v.application.id = :applicationId
        GROUP BY v.vote
    """)
    List<VoteCountProjection> getVoteSummary(
            @Param("meetingId") Long meetingId,
            @Param("applicationId") Long applicationId
    );

    void deleteByMeeting(TitleMeeting meeting);

}
