package com.example.KFCC_Backend.DTO.Membership;

import com.example.KFCC_Backend.Enum.MembershipCategory;
import com.example.KFCC_Backend.Enum.OwnershipType;
import com.example.KFCC_Backend.entity.Membership.Nominee;
import com.example.KFCC_Backend.entity.Membership.Partners;
import com.example.KFCC_Backend.entity.Membership.Proprietor;
import lombok.Data;

import java.util.List;

@Data
public class MembershipApplicationUpdateRequest {

    private String AddressLine1;
    private String AddressLine2;
    private String District;
    private String State;
    private Integer PinCode;
    private String FirmName;
    private String GstNo;
    private Proprietor proprietor;

    private MembershipCategory category;
    private OwnershipType ownershipType;

    // Nominees (max 2)
    private List<Nominee> nominees;

    // Partners (max 6, only for non-proprietor)
    private List<Partners> partners;



    public String getAddressLine1() {
        return AddressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        AddressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return AddressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        AddressLine2 = addressLine2;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public Integer getPinCode() {
        return PinCode;
    }

    public void setPinCode(Integer pinCode) {
        PinCode = pinCode;
    }

    public String getFirmName() {
        return FirmName;
    }

    public void setFirmName(String firmName) {
        FirmName = firmName;
    }

    public String getGstNo() {
        return GstNo;
    }

    public void setGstNo(String gstNo) {
        GstNo = gstNo;
    }

    public Proprietor getProprietor() {
        return proprietor;
    }

    public void setProprietor(Proprietor proprietor) {
        this.proprietor = proprietor;
    }

    public MembershipCategory getCategory() {
        return category;
    }

    public void setCategory(MembershipCategory category) {
        this.category = category;
    }

    public OwnershipType getOwnershipType() {
        return ownershipType;
    }

    public void setOwnershipType(OwnershipType ownershipType) {
        this.ownershipType = ownershipType;
    }

    public List<Nominee> getNominees() {
        return nominees;
    }

    public void setNominees(List<Nominee> nominees) {
        this.nominees = nominees;
    }

    public List<Partners> getPartners() {
        return partners;
    }

    public void setPartners(List<Partners> partners) {
        this.partners = partners;
    }
}
