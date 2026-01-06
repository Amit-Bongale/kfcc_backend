package com.example.KFCC_Backend.DTO.IdCard;

import com.example.KFCC_Backend.Enum.MembershipCategory;
import lombok.Data;
import java.time.LocalDate;

@Data
public class IdCardResponseDTO {

    // ---- USER DETAILS ----
    private String firstName;
    private String middleName;
    private String lastName;
    private String bloodGroup;
    private LocalDate dob;

    // ---- MEMBERSHIP DETAILS ----
    private MembershipCategory membershipCategory;
    private String membershipNumber;
    private String firmName;
    private String addressLine1;
    private String addressLine2;
    private LocalDate membershipExpiryDate;

    // ---- ID CARD DETAILS ----
    private Long id;
    private String applicantImage;
    private String status;
    private LocalDate issuedAt;
    private LocalDate submittedAt;


    public IdCardResponseDTO(String firstName, String middleName, String lastName, String bloodGroup, LocalDate dob, MembershipCategory membershipCategory, String membershipNumber, String firmName, String addressLine1, String addressLine2, LocalDate membershipExpiryDate, Long id, String applicantImage, String status, LocalDate issuedAt, LocalDate submittedAt) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.bloodGroup = bloodGroup;
        this.dob = dob;
        this.membershipCategory = membershipCategory;
        this.membershipNumber = membershipNumber;
        this.firmName = firmName;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.membershipExpiryDate = membershipExpiryDate;
        this.id = id;
        this.applicantImage = applicantImage;
        this.status = status;
        this.issuedAt = issuedAt;
        this.submittedAt = submittedAt;
    }


}
