package com.example.KFCC_Backend.entity.Title;

import com.example.KFCC_Backend.entity.Users;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class TitleMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate meetingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private Users leader;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users createdBy;     //Manager

    public enum MeetingStatus{
        ACTIVE, TERMINATED
    }

    @Enumerated(EnumType.STRING)
    private MeetingStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime terminatedAt;
}
