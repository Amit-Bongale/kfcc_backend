package com.example.KFCC_Backend.DTO;


import java.time.LocalDate;

public class SignupRequestDTO {

    private String firstName;
    private String middleName;
    private String lastName;
    private String mobileNo;
    private String otp;
    private String email;
    private String bloodGroup;
    private LocalDate dob;


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

    public String getBloodGroup() {
        return bloodGroup;
    }

    public LocalDate getDob() {
        return dob;
    }
}
