package com.example.KFCC_Backend.Repository;

import com.example.KFCC_Backend.Enum.UserRoles;
import com.example.KFCC_Backend.entity.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole , Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.role = :role")
    void deleteByRole(@Param("role") UserRoles role);

}
