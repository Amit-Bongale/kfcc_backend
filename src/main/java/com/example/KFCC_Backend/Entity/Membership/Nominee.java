package com.example.KFCC_Backend.Entity.Membership;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Nominee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nomineeId;

    @NotBlank(message = "Nominee name required")
    private String nomineeFirstName;

    @NotBlank(message = "Nominee name required")
    private String nomineeMiddleName;

    @NotBlank(message = "Nominee name required")
    private String nomineeLastName;

    @NotBlank(message = "Contact number is required")
    private String nomineeMobileNo;

    @Email(message = "Invalid email format")
    private String nomineeEmail;

    @NotBlank(message = "Nominee designation required")
    private String nomineeRelationship;

    @ManyToOne
    @JoinColumn(name = "application_id", referencedColumnName = "applicationId", nullable = false)
    @JsonBackReference
    private MembershipApplication membershipApplication;






    
    public Long getNomineeId() {
        return nomineeId;
    }

    public void setNomineeId(Long nomineeId) {
        this.nomineeId = nomineeId;
    }

    public String getNomineeFirstName() {
        return nomineeFirstName;
    }

    public void setNomineeFirstName(String nomineeFirstName) {
        this.nomineeFirstName = nomineeFirstName;
    }

    public String getNomineeMiddleName() {
        return nomineeMiddleName;
    }

    public void setNomineeMiddleName(String nomineeMiddleName) {
        this.nomineeMiddleName = nomineeMiddleName;
    }

    public String getNomineeLastName() {
        return nomineeLastName;
    }

    public void setNomineeLastName(String nomineeLastName) {
        this.nomineeLastName = nomineeLastName;
    }

    public String getNomineeMobileNo() {
        return nomineeMobileNo;
    }

    public void setNomineeMobileNo(String nomineeMobileNo) {
        this.nomineeMobileNo = nomineeMobileNo;
    }

    public String getNomineeEmail() {
        return nomineeEmail;
    }

    public void setNomineeEmail(String nomineeEmail) {
        this.nomineeEmail = nomineeEmail;
    }

    public String getNomineeRelationship() {
        return nomineeRelationship;
    }

    public void setNomineeRelationship(String nomineeRelationship) {
        this.nomineeRelationship = nomineeRelationship;
    }

    public MembershipApplication getMembershipApplication() {
        return membershipApplication;
    }

    public void setMembershipApplication(MembershipApplication membershipApplication) {
        this.membershipApplication = membershipApplication;
    }
}
