package com.example.KFCC_Backend.Repository;

import com.example.KFCC_Backend.entity.Membership.ONM.OnmMeeting;
import com.example.KFCC_Backend.entity.Membership.ONM.OnmMeetingMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnmMeetingMemberRepository extends JpaRepository<OnmMeetingMember , Long> {

    boolean existsByMeetingIdAndMemberId(Long meetingId, Long memberId);

    void deleteByMeeting(OnmMeeting meeting);

    List<OnmMeetingMember> findByMeetingId(Long meetingId);

}
