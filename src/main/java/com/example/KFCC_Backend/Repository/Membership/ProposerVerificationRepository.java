package com.example.KFCC_Backend.Repository.Membership;

import com.example.KFCC_Backend.Entity.Membership.ProposerVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ProposerVerificationRepository extends JpaRepository<ProposerVerification , Long> {

    Optional<ProposerVerification>
    findTopByApplicantUserIdAndProposerMembershipIdAndEndorserTypeAndIsVerifiedFalseOrderByCreatedAtDesc(
            Long applicantUserId,
            String proposerMembershipId,
            ProposerVerification.EndorserType endorserType
    );


    boolean existsByApplicantUserIdAndEndorserTypeAndIsVerifiedTrue(
            Long applicantUserId,
            ProposerVerification.EndorserType endorserType
    );

    long countByApplicantUserIdAndProposerMembershipIdAndEndorserTypeAndCreatedAtAfter(
            Long applicantUserId,
            String proposerMembershipId,
            ProposerVerification.EndorserType endorserType,
            LocalDateTime time
    );

}
