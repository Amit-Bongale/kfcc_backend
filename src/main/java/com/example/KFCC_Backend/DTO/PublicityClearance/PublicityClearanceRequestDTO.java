package com.example.KFCC_Backend.DTO.PublicityClearance;

import com.example.KFCC_Backend.Entity.PublicityClearance.PublicityClearanceDocuments;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PublicityClearanceRequestDTO {

    private Long cbcfCertificateNo;
    private LocalDate cbcfCertificateDate;

    // key = material type, value = list of files
    private Map<PublicityClearanceDocuments.PublicityDocumentType, List<MultipartFile>> documents;

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

    public Map<PublicityClearanceDocuments.PublicityDocumentType, List<MultipartFile>> getDocuments() {
        return documents;
    }

    public void setDocuments(Map<PublicityClearanceDocuments.PublicityDocumentType, List<MultipartFile>> documents) {
        this.documents = documents;
    }
}
