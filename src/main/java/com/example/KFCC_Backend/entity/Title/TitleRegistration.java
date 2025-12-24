package com.example.KFCC_Backend.entity.Title;

import com.example.KFCC_Backend.Enum.TitleApplicationStatus;
import com.example.KFCC_Backend.entity.Users;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "title_registration",
        indexes = {
                @Index(name = "idx_title_registration_title", columnList = "title")
        }
)
public class TitleRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    private Users producer;

    @Column(nullable = false , unique = true)
    private String title;

    private LocalDate date;
    private Boolean isFirstFilm;
    private String institution;
    private String language;
    private Boolean previouslyRegistered;
    private String previouslyRegisteredDetails;
    private String filmsByInstitutes;
    private String director;
    private String musicDirector;
    private String leadActor;
    private String category;
    private String gstNo;
    private LocalDate acceptedDate;
    private LocalDate expireDate;

    @OneToMany(
            mappedBy = "application",
            fetch = FetchType.LAZY
    )
    private List<TitleRegistrationDocuments> documents;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private TitleApplicationStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getProducer() {
        return producer;
    }

    public void setProducer(Users producer) {
        this.producer = producer;
    }

    public String getTitle() {
        return title;
    }



    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getFirstFilm() {
        return isFirstFilm;
    }

    public void setFirstFilm(Boolean firstFilm) {
        isFirstFilm = firstFilm;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getPreviouslyRegistered() {
        return previouslyRegistered;
    }

    public void setPreviouslyRegistered(Boolean previouslyRegistered) {
        this.previouslyRegistered = previouslyRegistered;
    }

    public String getPreviouslyRegisteredDetails() {
        return previouslyRegisteredDetails;
    }

    public void setPreviouslyRegisteredDetails(String previouslyRegisteredDetails) {
        this.previouslyRegisteredDetails = previouslyRegisteredDetails;
    }

    public String getFilmsByInstitutes() {
        return filmsByInstitutes;
    }

    public void setFilmsByInstitutes(String filmsByInstitutes) {
        this.filmsByInstitutes = filmsByInstitutes;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getMusicDirector() {
        return musicDirector;
    }

    public void setMusicDirector(String musicDirector) {
        this.musicDirector = musicDirector;
    }

    public String getLeadActor() {
        return leadActor;
    }

    public void setLeadActor(String leadActor) {
        this.leadActor = leadActor;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGstNo() {
        return gstNo;
    }

    public void setGstNo(String gstNo) {
        this.gstNo = gstNo;
    }

    public LocalDate getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(LocalDate acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public List<TitleRegistrationDocuments> getDocuments() {
        return documents;
    }

    public void setDocuments(List<TitleRegistrationDocuments> documents) {
        this.documents = documents;
    }

    public TitleApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(TitleApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
