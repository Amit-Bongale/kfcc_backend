package com.example.KFCC_Backend.entity.Title;

import com.example.KFCC_Backend.Enum.VoteDecision;
import com.example.KFCC_Backend.entity.ONM.OnmVote;
import com.example.KFCC_Backend.entity.Users;
import jakarta.persistence.*;

@Entity
public class TitleVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private TitleRegistration application;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users voter;

    @Enumerated(EnumType.STRING)
    private VoteDecision vote; // APPROVE / REJECT

    private Long meetingId;
}
