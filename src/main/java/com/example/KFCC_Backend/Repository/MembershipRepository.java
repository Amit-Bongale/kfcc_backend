package com.example.KFCC_Backend.Repository;

import com.example.KFCC_Backend.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipRepository  extends JpaRepository <Membership , Long> {
    Optional<Membership> findByUserId(Long userId);
}
