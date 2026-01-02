package com.example.KFCC_Backend.DTO.PublicityClearance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TitleRegistrationPublicityDTO {
    private Long id;
    private String title;
    private String titleInKannada;
    private LocalDate acceptedDate;
    private String category;
    private LocalDateTime createdAt;
    private String director;
    private LocalDate expireDate;
    private String language;
    private String leadActor;
    private String musicDirector;

    public TitleRegistrationPublicityDTO(Long id, String title, String titleInKannada, LocalDate acceptedDate, String category, LocalDateTime createdAt, String director, LocalDate expireDate, String language, String leadActor, String musicDirector) {
        this.id = id;
        this.title = title;
        this.titleInKannada = titleInKannada;
        this.acceptedDate = acceptedDate;
        this.category = category;
        this.createdAt = createdAt;
        this.director = director;
        this.expireDate = expireDate;
        this.language = language;
        this.leadActor = leadActor;
        this.musicDirector = musicDirector;
    }
}
