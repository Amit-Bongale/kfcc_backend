package com.example.KFCC_Backend.entity.Title;

import com.example.KFCC_Backend.Enum.VoteDecision;
import com.example.KFCC_Backend.entity.Users;
import jakarta.persistence.*;

@Entity
@Table(
        name = "title_vote",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"meeting_id", "application_id", "voter_id"}
        )
)
public class TitleVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private TitleMeeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private TitleRegistration application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", nullable = false)
    private Users voter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteDecision vote; // APPROVE / REJECT



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

    public TitleRegistration getApplication() {
        return application;
    }

    public void setApplication(TitleRegistration application) {
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
