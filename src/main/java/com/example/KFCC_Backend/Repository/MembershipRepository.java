package com.example.KFCC_Backend.Repository;

import com.example.KFCC_Backend.Enum.MembershipStatus;
import com.example.KFCC_Backend.entity.Membership.MembershipApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MembershipRepository  extends JpaRepository <MembershipApplication, Long> {
    Optional<MembershipApplication> findByUserId(Long userId);

    boolean existsByMembershipIdAndMembershipStatus(Long membershipId, MembershipStatus membershipStatus);

    Optional<MembershipApplication> findByApplicationId(Long applicaitonID);


    @Query("""
    SELECT a FROM MembershipApplication a
    WHERE a.membershipStatus IN :statuses
    ORDER BY a.submittedAt DESC
       """)
    List<MembershipApplication> findByCurrentStatusIn( @Param("statuses") Set<MembershipStatus> statuses);
}
