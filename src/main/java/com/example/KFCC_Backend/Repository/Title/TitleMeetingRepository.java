package com.example.KFCC_Backend.Repository.Title;

import com.example.KFCC_Backend.Entity.Title.TitleMeeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TitleMeetingRepository extends JpaRepository<TitleMeeting , Long> {

    boolean existsByStatus(TitleMeeting.MeetingStatus status);

    List<TitleMeeting> findByStatus(TitleMeeting.MeetingStatus status);

    // Fetch all meetings
    Page<TitleMeeting> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Fetch by status
    List<TitleMeeting> findByStatusOrderByCreatedAtDesc(TitleMeeting.MeetingStatus status);

}
