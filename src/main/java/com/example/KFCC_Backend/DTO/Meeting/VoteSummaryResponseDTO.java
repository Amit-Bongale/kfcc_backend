package com.example.KFCC_Backend.DTO.Meeting;

import lombok.Data;

@Data
public class VoteSummaryResponseDTO {

    private Long applicationId;
    private long approveCount;
    private long rejectCount;

    public VoteSummaryResponseDTO(Long applicationId, long approveCount, long rejectCount) {
        this.applicationId = applicationId;
        this.approveCount = approveCount;
        this.rejectCount = rejectCount;
    }
}
