package com.example.KFCC_Backend.Repository.Users;

import com.example.KFCC_Backend.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpVerification , Long> {
    Optional<OtpVerification> findByMobileNo(String mobileno);
}
