package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.Enum.UserRoles;
import com.example.KFCC_Backend.ExceptionHandlers.ResourceNotFoundException;
import com.example.KFCC_Backend.Repository.Users.UsersRepository;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;

import com.example.KFCC_Backend.Entity.Users;
import com.example.KFCC_Backend.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> getUserDetails() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Unauthenticated user");
        }

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        Users user = usersRepository.findByIdWithRoles(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<String> roles = user.getRoles()
                .stream()
                .map(r -> r.getRole().name())
                .collect(Collectors.toSet());


        String token = jwtUtil.generateToken(
                userDetails,
                roles
        );

        return Map.of(
                "userId", user.getId(),
                "firstName", user.getFirstName(),
                "mobile", user.getMobileNo(),
                "roles", roles,
                "token" , token
        );

    }

    public List<Users> getUsersByRole(UserRoles role) {
        return usersRepository.findUsersByRole(role);
    }

}
