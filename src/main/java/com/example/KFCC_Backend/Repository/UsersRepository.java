package com.example.KFCC_Backend.Repository;

import com.example.KFCC_Backend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users , Long> {
    Optional<Users> findByMobileNo(String mobile);

    @Query("""
        SELECT DISTINCT u FROM Users u
        LEFT JOIN FETCH u.roles
        WHERE u.id = :userId
    """)
    Optional<Users> findByIdWithRoles(@Param("userId") Long userId);
}
