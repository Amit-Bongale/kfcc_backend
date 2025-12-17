package com.example.KFCC_Backend.DTO;


public class SignupRequest {

    private String firstName;
    private String middleName;
    private String lastName;
    private String mobileNo;
    private String otp;
    private String email;


    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getOtp() {
        return otp;
    }

    public String getEmail() {
        return email;
    }
}
