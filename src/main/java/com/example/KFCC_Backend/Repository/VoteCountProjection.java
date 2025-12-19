package com.example.KFCC_Backend.Repository;

import com.example.KFCC_Backend.entity.ONM.OnmVote;

public interface VoteCountProjection {
    OnmVote.VoteDecision getVote();

    Long getCount();
}

