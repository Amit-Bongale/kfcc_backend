package com.example.KFCC_Backend.entity.Membership.ONM;

import com.example.KFCC_Backend.Enum.VoteDecision;
import com.example.KFCC_Backend.entity.Membership.MembershipApplication;
import com.example.KFCC_Backend.entity.Users;
import jakarta.persistence.*;

@Entity
@Table(
        name = "onm_vote",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"meeting_id", "application_id", "voter_id"}
        )
)
public class OnmVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private OnmMeeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private MembershipApplication application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", nullable = false)
    private Users voter;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteDecision vote;


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

    public MembershipApplication getApplication() {
        return application;
    }

    public void setApplication(MembershipApplication application) {
        this.application = application;
    }

    public Users getVoter() {
        return voter;
    }

    public void setVoter(Users voter) {
        this.voter = voter;
    }

    public VoteDecision getVote() {
        return vote;
    }

    public void setVote(VoteDecision vote) {
        this.vote = vote;
    }
}
