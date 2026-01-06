package com.example.KFCC_Backend.Repository.IdCard;

import com.example.KFCC_Backend.Entity.IdCardRequests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdCardRequestRepository extends JpaRepository<IdCardRequests , Long> {
    List<IdCardRequests> findByUserId(Long userId);

    List<IdCardRequests> findByStatus(IdCardRequests.IdCardStatus status);

//    boolean existsByMembershipApplicationId(Long membershipApplication);
}
