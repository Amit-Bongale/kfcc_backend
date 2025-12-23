package com.example.KFCC_Backend.entity.ONM;

import com.example.KFCC_Backend.entity.Users;
import jakarta.persistence.*;

@Entity
public class OnmMeetingMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private OnmMeeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users member;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OnmMeeting getMeeting() {
        return meeting;
    }

    public void setMeeting(OnmMeeting meeting) {
        this.meeting = meeting;
    }

    public Users getMember() {
        return member;
    }

    public void setMember(Users member) {
        this.member = member;
    }
}
