package com.example.KFCC_Backend.Repository;

import com.example.KFCC_Backend.Enum.MembershipStatus;
import com.example.KFCC_Backend.Enum.TitleApplicationStatus;
import com.example.KFCC_Backend.entity.Membership.MembershipApplication;
import com.example.KFCC_Backend.entity.Title.TitleRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TitleRegistrationRepository extends JpaRepository<TitleRegistration , Long> {

    boolean existsByTitleIgnoreCase(String Title);

    Optional<TitleRegistration> findByProducerId(Long ProducerId);

    @Query("""
    SELECT a FROM TitleRegistration a
    WHERE a.status IN :statuses
    ORDER BY a.createdAt DESC
    """)
    List<TitleRegistration> findByCurrentStatusIn(@Param("statuses") Set<TitleApplicationStatus> statuses);


}
