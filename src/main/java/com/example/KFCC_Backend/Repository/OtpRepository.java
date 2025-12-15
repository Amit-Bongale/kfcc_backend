package com.example.KFCC_Backend.Repository;

import com.example.KFCC_Backend.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpVerification , Long> {
    Optional<OtpVerification> findByMobileNo(String mobileno);
}
