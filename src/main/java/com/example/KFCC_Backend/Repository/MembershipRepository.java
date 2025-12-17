package com.example.KFCC_Backend.Repository;

import com.example.KFCC_Backend.Enum.MembershipStatus;
import com.example.KFCC_Backend.entity.Membership.MembershipApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository  extends JpaRepository <MembershipApplication, Long> {
    Optional<MembershipApplication> findByUserId(Long userId);

    boolean existsByMembershipIdAndMembershipStatus(Long membershipId, MembershipStatus membershipStatus);

    Optional<MembershipApplication> findByApplicationId(Long applicaitonID);
}
