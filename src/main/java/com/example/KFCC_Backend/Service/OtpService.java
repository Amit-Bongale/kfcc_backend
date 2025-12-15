package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.Repository.OtpRepository;
import com.example.KFCC_Backend.entity.OtpVerification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int RESEND_SECONDS = 60;

    @Autowired
    private OtpRepository otpRepository;

    public void sendOtp(String mobileNo) {
        OtpVerification otp = otpRepository
                .findByMobileNo(mobileNo)
                .orElse(new OtpVerification());

        if (otp.getLastSentAt() != null &&
                Duration.between(otp.getLastSentAt(), LocalDateTime.now()).getSeconds() < RESEND_SECONDS) {
            throw new IllegalStateException("OTP resend limit exceeded");
        }

        String code = String.valueOf(100000 + new Random().nextInt(900000));

        otp.setMobileNo(mobileNo);
        otp.setOtp(code);
        otp.setAttempts(0);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otp.setLastSentAt(LocalDateTime.now());

        otpRepository.save(otp);

        // MSG91 (DEV)
        System.out.println("OTP for " + mobileNo + " is: " + code);
    }

    public boolean verifyOtp(String mobileNo, String otpCode) {
        OtpVerification otp = otpRepository
                .findByMobileNo(mobileNo)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (otp.getAttempts() >= MAX_ATTEMPTS)
            throw new IllegalStateException("OTP attempts exceeded");

        otp.setAttempts(otp.getAttempts() + 1);
        otpRepository.save(otp);

        if (otp.getExpiresAt().isBefore(LocalDateTime.now()))
            return false;

        return otp.getOtp().equals(otpCode);
    }
}

