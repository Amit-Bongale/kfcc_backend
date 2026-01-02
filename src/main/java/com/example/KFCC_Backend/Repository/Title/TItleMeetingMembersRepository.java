package com.example.KFCC_Backend.Repository.Title;

import com.example.KFCC_Backend.Entity.Title.TitleMeeting;
import com.example.KFCC_Backend.Entity.Title.TitleMeetingMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TItleMeetingMembersRepository extends JpaRepository<TitleMeetingMembers , Long> {

    boolean existsByMeetingIdAndMemberId(Long meetingId, Long memberId);

    void deleteByMeeting(TitleMeeting meeting);

    List<TitleMeetingMembers> findByMeetingId(Long meetingId);
}
