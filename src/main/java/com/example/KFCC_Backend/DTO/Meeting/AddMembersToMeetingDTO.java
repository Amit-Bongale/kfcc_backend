package com.example.KFCC_Backend.DTO.Meeting;

import lombok.Data;

import java.util.List;

@Data
public class AddMembersToMeetingDTO {

    private List<Long> memberIds;

    public List<Long> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<Long> memberIds) {
        this.memberIds = memberIds;
    }

}
