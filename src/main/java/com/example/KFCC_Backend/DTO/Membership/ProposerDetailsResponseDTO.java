package com.example.KFCC_Backend.DTO.Membership;

import com.example.KFCC_Backend.Enum.MembershipCategory;

public class ProposerDetailsResponseDTO {

    private String firstName;
    private String middleName;
    private String lastName;

    private String address;

    private String mobile;

    private MembershipCategory designation;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public MembershipCategory getDesignation() {
        return designation;
    }

    public void setDesignation(MembershipCategory designation) {
        this.designation = designation;
    }


}
