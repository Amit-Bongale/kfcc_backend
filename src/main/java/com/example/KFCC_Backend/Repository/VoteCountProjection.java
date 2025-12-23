package com.example.KFCC_Backend.Repository;

import com.example.KFCC_Backend.Enum.VoteDecision;
import com.example.KFCC_Backend.entity.ONM.OnmVote;

public interface VoteCountProjection {
    VoteDecision getVote();

    Long getCount();
}

