package com.example.KFCC_Backend.entity.Title;

import jakarta.persistence.*;

@Entity
public class TitleRegistrationDocuments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private TitleRegistration application;

    private String path;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TitleRegistration getApplication() {
        return application;
    }

    public void setApplication(TitleRegistration application) {
        this.application = application;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
