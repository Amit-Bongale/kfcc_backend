package com.example.KFCC_Backend.entity.Membership;

import com.example.KFCC_Backend.Enum.MembershipCategory;
import com.example.KFCC_Backend.Enum.MembershipStatus;
import com.example.KFCC_Backend.Enum.OwnershipType;
import com.example.KFCC_Backend.entity.Users;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(
        name = "membership_application",
        indexes = {
                @Index(
                        name = "idx_membership_status",
                        columnList = "membership_status"
                )
        }
)
public class MembershipApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    // assign membershipId after membership application is accepted by EC and set expiry 1yr after acceptance date
    private String membershipId;

    // 1 user can apply for many memberships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" ,  nullable = false)
    private Users user;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotBlank(message = "Address line 1 is required")
    private String applicantAddressLine1;

    @NotBlank(message = "Address line 2 is required")
    private String applicantAddressLine2;

    @NotBlank(message = "District is required")
    private String applicantDistrict;

    @NotBlank(message = "State is required")
    private String applicantState;

    @NotNull(message = "Pin code is required")
    private Integer applicantPinCode;

    @Enumerated(EnumType.STRING)
    @NotBlank(message = "Membership category is required")
    private MembershipCategory applicantMembershipCategory;

    @NotBlank(message = "Membership firm name is required")
    private String applicantFirmName;

    @NotBlank(message = "Ownership type required")
    @Enumerated(EnumType.STRING)
    private OwnershipType applicantOwnershipType;

    @NotBlank(message = "GST number is required")
    @Pattern(
            regexp = "^[0-9A-Z]{15}$",
            message = "Invalid GST number format — it must be 15 characters, alphanumeric and uppercase"
    )
    private String applicantGstNo;


    // status
    @Enumerated(EnumType.STRING)
    @Column(name = "membership_status", nullable = false)
    private MembershipStatus membershipStatus;

    private String remark;
    private String remarkedBy;


    /* ---------------- DOCUMENTS ---------------- */
    private String applicantImage;
    private String applicantPan;
    private String applicantAadhaar;
    private String applicantAddressProof;
    private String applicantSignature;
    private String firmSeal;
    private String partnershipDeed;
    private String moa;
    private String aoa;

    /* -------------------------------- */


    // only 1 proprietor for OwnerShip type: Proprietor
    @OneToOne(mappedBy = "membershipApplication", cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
    private Proprietor proprietor;

    // upto 6 partners for all other OwnerShip Types
    @OneToMany(mappedBy = "membershipApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Partners> partners = new ArrayList<>();

    // Upto 2 nominee for 1 applicaiton
    @OneToMany(mappedBy = "membershipApplication", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private List<Nominee> nominee = new ArrayList<>();


    // 1 Mandatary Proposer [who is already an member of KFCC]
    @OneToOne(mappedBy = "membershipApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private Proposer proposer;

    // 1 Mandatary Seconder [who is already an member of KFCC]
    @OneToOne(mappedBy = "membershipApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private Seconder seconder;


    /* ---------------- FEES ---------------- */

    @NotNull
    private Integer membershipFee;

    // mandatory for 1st application & Optional for rest of applications
    @NotNull
    private Integer kalyanNidhi;

    @NotNull
    private Integer totalAmount;


    /* ---------------- ACCEPTANCE & EXPIRY ---------------- */
    // set date of EC approval
    private LocalDate membershipAcceptanceDate;

    // Set ONLY when application is APPROVED [1yrs]
    private LocalDate membershipExpiryDate;


    private LocalDateTime submittedAt = LocalDateTime.now();


    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }



    public String getApplicantAddressLine1() {
        return applicantAddressLine1;
    }

    public void setApplicantAddressLine1(String applicantAddressLine1) {
        this.applicantAddressLine1 = applicantAddressLine1;
    }

    public String getApplicantAddressLine2() {
        return applicantAddressLine2;
    }

    public void setApplicantAddressLine2(String applicantAddressLine2) {
        this.applicantAddressLine2 = applicantAddressLine2;
    }

    public String getApplicantDistrict() {
        return applicantDistrict;
    }

    public void setApplicantDistrict(String applicantDistrict) {
        this.applicantDistrict = applicantDistrict;
    }

    public String getApplicantState() {
        return applicantState;
    }

    public void setApplicantState(String applicantState) {
        this.applicantState = applicantState;
    }

    public int getApplicantPinCode() {
        return applicantPinCode;
    }

    public void setApplicantPinCode(int applicantPinCode) {
        this.applicantPinCode = applicantPinCode;
    }

    public String getApplicantImage() {
        return applicantImage;
    }

    public void setApplicantImage(String applicantImage) {
        this.applicantImage = applicantImage;
    }


    public MembershipCategory getApplicantMembershipCategory() {
        return applicantMembershipCategory;
    }

    public void setApplicantMembershipCategory(MembershipCategory applicantMembershipCategory) {
        this.applicantMembershipCategory = applicantMembershipCategory;
    }

    public String getApplicantFirmName() {
        return applicantFirmName;
    }

    public void setApplicantFirmName(String applicantFirmName) {
        this.applicantFirmName = applicantFirmName;
    }

    public OwnershipType getApplicantOwnershipType() {
        return applicantOwnershipType;
    }

    public void setApplicantOwnershipType(OwnershipType applicantOwnershipType) {
        this.applicantOwnershipType = applicantOwnershipType;
    }

    public String getApplicantGstNo() {
        return applicantGstNo;
    }

    public void setApplicantGstNo(String applicantGstNo) {
        this.applicantGstNo = applicantGstNo;
    }

    public MembershipStatus getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(MembershipStatus membershipStatus) {
        this.membershipStatus = membershipStatus;
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

    public void setApplicantPinCode(Integer applicantPinCode) {
        this.applicantPinCode = applicantPinCode;
    }

    public Proprietor getProprietor() {
        return proprietor;
    }

    public void setProprietor(Proprietor proprietor) {
        this.proprietor = proprietor;
    }

    public List<Partners> getPartners() {
        return partners;
    }

    public void setPartners(List<Partners> partners) {
        this.partners = partners;
    }

    public String getPartnershipDeed() {
        return partnershipDeed;
    }

    public void setPartnershipDeed(String partnershipDeed) {
        this.partnershipDeed = partnershipDeed;
    }

    public List<Nominee> getNominee() {
        return nominee;
    }

    public void setNominee(List<Nominee> nominee) {
        this.nominee = nominee;
    }

    public Proposer getProposer() {
        return proposer;
    }

    public void setProposer(Proposer proposer) {
        this.proposer = proposer;
    }

    public Seconder getSeconder() {
        return seconder;
    }

    public void setSeconder(Seconder seconder) {
        this.seconder = seconder;
    }

    public Integer getMembershipFee() {
        return membershipFee;
    }

    public void setMembershipFee(Integer membershipFee) {
        this.membershipFee = membershipFee;
    }

    public Integer getKalyanNidhi() {
        return kalyanNidhi;
    }

    public void setKalyanNidhi(Integer kalyanNidhi) {
        this.kalyanNidhi = kalyanNidhi;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getMembershipAcceptanceDate() {
        return membershipAcceptanceDate;
    }

    public void setMembershipAcceptanceDate(LocalDate membershipAcceptanceDate) {
        this.membershipAcceptanceDate = membershipAcceptanceDate;
    }

    public LocalDate getMembershipExpiryDate() {
        return membershipExpiryDate;
    }

    public void setMembershipExpiryDate(LocalDate membershipExpiryDate) {
        this.membershipExpiryDate = membershipExpiryDate;
    }

    public String getApplicantPan() {
        return applicantPan;
    }

    public void setApplicantPan(String applicantPan) {
        this.applicantPan = applicantPan;
    }

    public String getApplicantAadhaar() {
        return applicantAadhaar;
    }

    public void setApplicantAadhaar(String applicantAadhaar) {
        this.applicantAadhaar = applicantAadhaar;
    }

    public String getApplicantAddressProof() {
        return applicantAddressProof;
    }

    public void setApplicantAddressProof(String applicantAddressProof) {
        this.applicantAddressProof = applicantAddressProof;
    }

    public String getFirmSeal() {
        return firmSeal;
    }

    public void setFirmSeal(String firmSeal) {
        this.firmSeal = firmSeal;
    }

    public String getMoa() {
        return moa;
    }

    public void setMoa(String moa) {
        this.moa = moa;
    }

    public String getAoa() {
        return aoa;
    }

    public void setAoa(String aoa) {
        this.aoa = aoa;
    }

    public String getApplicantSignature() {
        return applicantSignature;
    }

    public void setApplicantSignature(String applicantSignature) {
        this.applicantSignature = applicantSignature;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

}