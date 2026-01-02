package com.example.KFCC_Backend.DTO.PublicityClearance;
import com.example.KFCC_Backend.Enum.PublicityApplicationStatus;
import lombok.Data;

@Data
public class PublicityClearanceResponseDTO {

    private TitleRegistrationPublicityDTO  title;

    private Long publicityClearanceId;

    // NOT_SUBMITTED or actual publicity status
    private PublicityApplicationStatus publicityClearanceStatus;

    public PublicityClearanceResponseDTO(TitleRegistrationPublicityDTO title, Long publicityClearanceId, PublicityApplicationStatus publicityClearanceStatus) {
        this.title = title;
        this.publicityClearanceId = publicityClearanceId;
        this.publicityClearanceStatus = publicityClearanceStatus;
    }
}
