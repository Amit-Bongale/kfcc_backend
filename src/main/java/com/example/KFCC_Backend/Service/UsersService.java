package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.Repository.UsersRepository;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;

import com.example.KFCC_Backend.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    public Map<String, Object> getUserDetails() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated user");
        }

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        Users user = usersRepository.findByIdWithRoles(userDetails.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<String> roles = user.getRoles()
                .stream()
                .map(r -> r.getRole().name())
                .collect(Collectors.toSet());

        return Map.of(
                "userId", user.getId(),
                "firstName", user.getFirstName(),
                "mobile", user.getMobileNo(),
                "roles", roles
        );


    }
}
