package com.example.KFCC_Backend.DTO.ONMMeeting;

import com.example.KFCC_Backend.entity.ONM.OnmVote;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CastVoteRequestDTO {

    @NotNull
    private Long applicationId;

    @NotNull
    private OnmVote.VoteDecision vote;

    public Long getApplicationId() {
        return applicationId;
    }

    public OnmVote.VoteDecision getVote() {
        return vote;
    }
}
