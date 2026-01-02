package com.example.KFCC_Backend.DTO.PublicityClearance;
import com.example.KFCC_Backend.Enum.PublicityApplicationStatus;
import lombok.Data;

@Data
public class TitleWithPublicityStatusDTO {
    private TitleRegistrationPublicityDTO  title;

    // NOT_SUBMITTED or actual publicity status
    private PublicityApplicationStatus publicityClearanceStatus;

    public TitleWithPublicityStatusDTO(TitleRegistrationPublicityDTO title, PublicityApplicationStatus publicityClearanceStatus) {
        this.title = title;
        this.publicityClearanceStatus = publicityClearanceStatus;
    }

}
