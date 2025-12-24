package com.example.KFCC_Backend.Repository;

import com.example.KFCC_Backend.entity.Membership.ONM.OnmMeeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnmMeetingRepository extends JpaRepository<OnmMeeting , Long> {
    boolean existsByStatus(OnmMeeting.MeetingStatus status);

    List<OnmMeeting> findByStatus(OnmMeeting.MeetingStatus status);

    // Fetch all meetings
    Page<OnmMeeting> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Fetch by status
    List<OnmMeeting> findByStatusOrderByCreatedAtDesc(OnmMeeting.MeetingStatus status);

}
