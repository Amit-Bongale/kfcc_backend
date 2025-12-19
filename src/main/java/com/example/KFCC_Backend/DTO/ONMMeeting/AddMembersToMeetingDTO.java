package com.example.KFCC_Backend.DTO.ONMMeeting;


import java.util.List;

public class AddMembersToMeetingDTO {

    private List<Long> memberIds;

    public List<Long> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<Long> memberIds) {
        this.memberIds = memberIds;
    }

}
