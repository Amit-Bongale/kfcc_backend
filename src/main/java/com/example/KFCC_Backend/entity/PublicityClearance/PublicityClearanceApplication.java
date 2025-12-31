package com.example.KFCC_Backend.entity.PublicityClearance;

import com.example.KFCC_Backend.Enum.PublicityApplicationStatus;
import com.example.KFCC_Backend.entity.Title.TitleRegistration;
import com.example.KFCC_Backend.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PublicityClearanceApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "title_id" , nullable = false)
    private TitleRegistration title;

    @ManyToOne
    @JoinColumn(name = "producer_id" , nullable = false)
    @JsonIgnoreProperties("applications")
    private Users producer;

    @Enumerated(EnumType.STRING)
    private PublicityApplicationStatus status;

    private Long cbcfCertificateNo;
    private LocalDate cbcfCertificateDate;


    @OneToMany(
            mappedBy = "application",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PublicityClearanceDocuments> documents = new ArrayList<>();

    private String remark;
    private String remarkedBy;

    private LocalDateTime submittedAt;
    private LocalDate acceptedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TitleRegistration getTitle() {
        return title;
    }

    public void setTitle(TitleRegistration title) {
        this.title = title;
    }

    public Users getProducer() {
        return producer;
    }

    public void setProducer(Users producer) {
        this.producer = producer;
    }

    public PublicityApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(PublicityApplicationStatus status) {
        this.status = status;
    }

    public Long getCbcfCertificateNo() {
        return cbcfCertificateNo;
    }

    public void setCbcfCertificateNo(Long cbcfCertificateNo) {
        this.cbcfCertificateNo = cbcfCertificateNo;
    }

    public LocalDate getCbcfCertificateDate() {
        return cbcfCertificateDate;
    }

    public void setCbcfCertificateDate(LocalDate cbcfCertificateDate) {
        this.cbcfCertificateDate = cbcfCertificateDate;
    }
    

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemarkedBy() {
        return remarkedBy;
    }

    public void setRemarkedBy(String remarkedBy) {
        this.remarkedBy = remarkedBy;
    }

    public List<PublicityClearanceDocuments> getDocuments() {
        return documents;
    }

    public void setDocuments(List<PublicityClearanceDocuments> documents) {
        this.documents = documents;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDate getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDate acceptedAt) {
        this.acceptedAt = acceptedAt;
    }
}
