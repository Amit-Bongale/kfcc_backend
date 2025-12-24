package com.example.KFCC_Backend.entity.Title;


import com.example.KFCC_Backend.entity.Users;
import jakarta.persistence.*;

public class TitleMeetingMembers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private TitleMeeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users member;
}
