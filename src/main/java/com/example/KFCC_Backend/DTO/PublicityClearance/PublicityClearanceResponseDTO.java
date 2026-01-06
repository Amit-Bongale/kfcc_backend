package com.example.KFCC_Backend.DTO.PublicityClearance;
import com.example.KFCC_Backend.Enum.PublicityApplicationStatus;
import lombok.Data;

@Data
public class PublicityClearanceResponseDTO {

    private TitleRegistrationPublicityDTO  title;

    private Long publicityClearanceId;

    // NOT_SUBMITTED or actual publicity status
    private PublicityApplicationStatus publicityClearanceStatus;

    private String remark;
    private String remarkBy;

    public PublicityClearanceResponseDTO(TitleRegistrationPublicityDTO title, Long publicityClearanceId, PublicityApplicationStatus publicityClearanceStatus, String remark, String remarkBy) {
        this.title = title;
        this.publicityClearanceId = publicityClearanceId;
        this.publicityClearanceStatus = publicityClearanceStatus;
        this.remark = remark;
        this.remarkBy = remarkBy;
    }
}
