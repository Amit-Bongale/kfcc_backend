package com.example.KFCC_Backend.Repository.Users;

import com.example.KFCC_Backend.Enum.UserRoles;
import com.example.KFCC_Backend.Entity.UserRole;
import com.example.KFCC_Backend.Entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole , Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.role = :role")
    void deleteByRole(@Param("role") UserRoles role);

    @Transactional
    @Modifying
    @Query("""
        DELETE FROM UserRole ur
        WHERE ur.user.id IN :userIds
          AND ur.role = :role
    """)
    void deleteByUserIdsAndRole(
            @Param("userIds") Set<Long> userIds,
            @Param("role") UserRoles role
    );

    boolean existsByUserAndRole(Users user, UserRoles role);


}
