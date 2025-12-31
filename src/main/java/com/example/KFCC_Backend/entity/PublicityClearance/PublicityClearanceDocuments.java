package com.example.KFCC_Backend.entity.PublicityClearance;

import jakarta.persistence.*;

@Entity
public class PublicityClearanceDocuments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private PublicityClearanceApplication application;

    private PublicityDocumentType documentType;

    private String filePath;

    public enum PublicityDocumentType {
        CBCF_CERTIFICATE,
        HOARDINGS,
        POSTER_DESIGNS,
        PHOTOCARDS,
        SLIDES,
        BLOCK_DESIGNS,
        ART_PULLS,
        STICKERS,
        NEWSPAPER_PUBLICITY,
        OTHERS
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PublicityClearanceApplication getApplication() {
        return application;
    }

    public void setApplication(PublicityClearanceApplication application) {
        this.application = application;
    }

    public PublicityDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(PublicityDocumentType documentType) {
        this.documentType = documentType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
