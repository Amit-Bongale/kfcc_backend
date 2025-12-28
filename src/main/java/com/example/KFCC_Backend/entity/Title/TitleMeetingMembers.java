package com.example.KFCC_Backend.entity.Title;


import com.example.KFCC_Backend.entity.Users;
import jakarta.persistence.*;

@Entity
public class TitleMeetingMembers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private TitleMeeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users member;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TitleMeeting getMeeting() {
        return meeting;
    }

    public void setMeeting(TitleMeeting meeting) {
        this.meeting = meeting;
    }

    public Users getMember() {
        return member;
    }

    public void setMember(Users member) {
        this.member = member;
    }
}
