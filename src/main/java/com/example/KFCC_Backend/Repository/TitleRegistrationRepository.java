package com.example.KFCC_Backend.Repository;

import com.example.KFCC_Backend.entity.Title.TitleRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TitleRegistrationRepository extends JpaRepository<TitleRegistration , Long> {

    boolean existsByTitleIgnoreCase(String Title);

    Optional<TitleRegistration> findByProducerId(Long ProducerId);

}
