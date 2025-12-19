package com.example.KFCC_Backend.Repository;

import com.example.KFCC_Backend.entity.ONM.OnmMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnmMeetingRepository extends JpaRepository<OnmMeeting , Long> {
    boolean existsByStatus(OnmMeeting.MeetingStatus status);

    List<OnmMeeting> findByStatus(OnmMeeting.MeetingStatus status);

}
