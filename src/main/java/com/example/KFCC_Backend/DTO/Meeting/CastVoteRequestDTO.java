package com.example.KFCC_Backend.DTO.Meeting;

import com.example.KFCC_Backend.Enum.VoteDecision;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CastVoteRequestDTO {

    @NotNull
    private Long applicationId;

    @NotNull
    private VoteDecision vote;

    public Long getApplicationId() {
        return applicationId;
    }

    public VoteDecision getVote() {
        return vote;
    }
}
