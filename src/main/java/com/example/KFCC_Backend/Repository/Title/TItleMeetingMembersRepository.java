package com.example.KFCC_Backend.Repository.Title;

import com.example.KFCC_Backend.entity.Membership.ONM.OnmMeeting;
import com.example.KFCC_Backend.entity.Membership.ONM.OnmMeetingMember;
import com.example.KFCC_Backend.entity.Title.TitleMeeting;
import com.example.KFCC_Backend.entity.Title.TitleMeetingMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TItleMeetingMembersRepository extends JpaRepository<TitleMeetingMembers , Long> {

    boolean existsByMeetingIdAndMemberId(Long meetingId, Long memberId);

    void deleteByMeeting(TitleMeeting meeting);

    List<TitleMeetingMembers> findByMeetingId(Long meetingId);
}
