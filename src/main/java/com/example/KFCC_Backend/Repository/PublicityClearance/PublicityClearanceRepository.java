package com.example.KFCC_Backend.Repository.PublicityClearance;

import com.example.KFCC_Backend.entity.PublicityClearance.PublicityClearanceApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublicityClearanceRepository extends JpaRepository<PublicityClearanceApplication , Long> {

    Optional<PublicityClearanceApplication> findByTitle_Id(Long titleId);

    boolean existsByTitle_Id(Long titleId);

}
