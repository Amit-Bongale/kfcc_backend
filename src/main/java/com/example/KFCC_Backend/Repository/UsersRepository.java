package com.example.KFCC_Backend.Repository;

import com.example.KFCC_Backend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users , Long> {
    Optional<Users> findByMobileNo(String mobile);
}
