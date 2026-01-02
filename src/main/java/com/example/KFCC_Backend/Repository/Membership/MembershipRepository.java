package com.example.KFCC_Backend.Repository.Membership;

import com.example.KFCC_Backend.Enum.MembershipStatus;
import com.example.KFCC_Backend.Entity.Membership.MembershipApplication;
import com.example.KFCC_Backend.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MembershipRepository  extends JpaRepository <MembershipApplication, Long> {

    List<MembershipApplication> findByUserIdOrderBySubmittedAtDesc(Long userId);

    boolean existsByMembershipIdAndMembershipStatus(Long membershipId, MembershipStatus membershipStatus);

    Optional<MembershipApplication> findByApplicationId(Long applicaitonID);


    @Query("""
    SELECT a FROM MembershipApplication a
    WHERE a.membershipStatus IN :statuses
    ORDER BY a.submittedAt DESC
    """)
    List<MembershipApplication> findByCurrentStatusIn( @Param("statuses") Set<MembershipStatus> statuses);

    @Query("""
        SELECT COUNT(m) > 0
        FROM MembershipApplication m
        WHERE m.user.id = :userId
          AND m.membershipStatus = :status
          AND m.membershipExpiryDate >= :today
    """)
    boolean hasValidMembership(
            @Param("userId") Long userId,
            @Param("status") MembershipStatus status,
            @Param("today") LocalDate today
    );

    @Query("""
        SELECT user
        FROM MembershipApplication m
        WHERE m.membershipStatus = :status
          AND m.membershipExpiryDate >= :today
    """)
    List<Users> findMembers(  @Param("status") MembershipStatus status,
                              @Param("today") LocalDate today );
}
