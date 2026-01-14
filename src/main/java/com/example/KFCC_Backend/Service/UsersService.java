package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.Entity.UserRole;
import com.example.KFCC_Backend.Enum.UserRoles;
import com.example.KFCC_Backend.ExceptionHandlers.BadRequestException;
import com.example.KFCC_Backend.ExceptionHandlers.ResourceNotFoundException;
import com.example.KFCC_Backend.Repository.Users.UserRoleRepository;
import com.example.KFCC_Backend.Repository.Users.UsersRepository;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;

import com.example.KFCC_Backend.Entity.Users;
import com.example.KFCC_Backend.Jwt.JwtUtil;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.Nullable;
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
    private UserRoleRepository userRoleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // return user details by token for redux
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

    //fetch all details of the user
    public Users getUserAllDetails(CustomUserDetails user) {
        return usersRepository.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not Found"));
    }

    //assign new role to user
    public void assignRole(Long userId, UserRoles role){
        Users user = usersRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean exists = userRoleRepository.existsByUserAndRole(user , role);

        if(exists){
            throw new BadRequestException("User with role Already exists");
        }

        UserRole user_role = new UserRole();
        user_role.setUser(user);
        user_role.setRole(role);

        userRoleRepository.save(user_role);
    }

    //delete user role
    @Transactional
    public void deleteUserRole(Long userId , UserRoles role){

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserRole userRole = user.getRoles().stream()
                .filter(r -> r.getRole() == role)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Role not assigned"));

        user.getRoles().remove(userRole);

    }



}
