package com.example.KFCC_Backend.Repository.PublicityClearance;

import com.example.KFCC_Backend.Enum.PublicityApplicationStatus;
import com.example.KFCC_Backend.Entity.PublicityClearance.PublicityClearanceApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PublicityClearanceRepository extends JpaRepository<PublicityClearanceApplication , Long> {

    Optional<PublicityClearanceApplication> findByTitle_Id(Long titleId);

    boolean existsByTitle_Id(Long titleId);

    @Query("""
        SELECT t, p.status, p.id
        FROM TitleRegistration t
        LEFT JOIN PublicityClearanceApplication p
            ON p.title.id = t.id
        WHERE t.producer.id = :producerId
          AND t.status = com.example.KFCC_Backend.Enum.TitleApplicationStatus.FINAL_APPROVED
    """)
    List<Object[]> findFinalApprovedTitlesWithPublicityStatus(
            @Param("producerId") Long producerId
    );

    @Query("""
    SELECT a FROM PublicityClearanceApplication a
    WHERE a.status IN :statuses
    ORDER BY a.submittedAt DESC
    """)
    List<PublicityClearanceApplication> findByCurrentStatusIn(@Param("statuses") Set<PublicityApplicationStatus> statuses);


}
