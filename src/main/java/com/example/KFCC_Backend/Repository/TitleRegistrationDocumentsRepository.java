package com.example.KFCC_Backend.Repository;

import com.example.KFCC_Backend.entity.Title.TitleRegistrationDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TitleRegistrationDocumentsRepository extends JpaRepository<TitleRegistrationDocuments , Long> {

    Long countByApplicationId(Long applicationId);
    List<TitleRegistrationDocuments> findByApplicationId(Long applicationId);
    
}
