package com.example.KFCC_Backend.Repository.Membership;

import com.example.KFCC_Backend.Enum.VoteDecision;

public interface VoteCountProjection {
    VoteDecision getVote();

    Long getCount();
}

