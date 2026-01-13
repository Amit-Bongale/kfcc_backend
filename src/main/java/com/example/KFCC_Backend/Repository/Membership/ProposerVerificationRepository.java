package com.example.KFCC_Backend.Repository.Membership;

import com.example.KFCC_Backend.Entity.Membership.ProposerVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProposerVerificationRepository extends JpaRepository<ProposerVerification , Long> {

    Optional<ProposerVerification>
    findTopByApplicantUserIdAndProposerMembershipIdAndIsVerifiedFalseOrderByCreatedAtDesc(
            Long applicantUserId,
            String proposerMembershipId
    );

    boolean existsByApplicantUserIdAndIsVerifiedTrue(Long applicantUserId);

}
