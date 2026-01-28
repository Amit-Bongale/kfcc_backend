package com.example.KFCC_Backend.Repository;

import com.example.KFCC_Backend.Entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payments , Long> {

    Optional<Payments> findByRazorpayOrderId(String orderId);

}
