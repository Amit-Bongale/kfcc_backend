package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.DTO.Users.AddUserDTO;
import com.example.KFCC_Backend.DTO.Users.UpdateUserDTO;
import com.example.KFCC_Backend.DTO.Users.UserWithRolesDTO;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public List<UserWithRolesDTO> getUsersByRole(UserRoles role) {
        return usersRepository.findUsersByRole(role)
                .stream()
                .map(user -> {
                    UserWithRolesDTO dto = new UserWithRolesDTO();
                    dto.setId(user.getId());
                    dto.setFirstName(user.getFirstName());
                    dto.setMiddleName(user.getMiddleName());
                    dto.setLastName(user.getLastName());
                    dto.setMobileNo(user.getMobileNo());
                    dto.setEmail(user.getEmail());
                    dto.setBloodGroup(user.getBloodGroup());
                    dto.setDob(user.getDob());

                    dto.setRoles(
                            user.getRoles()
                                    .stream()
                                    .map(UserRole::getRole)
                                    .collect(Collectors.toSet())
                    );

                    return dto;
                })
                .toList();
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


    //delete user
    @Transactional
    public void deleteUserRole(Long userId , UserRoles role){

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserRole userRole = user.getRoles().stream()
                .filter(r -> r.getRole() == role)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Role not assigned"));

        user.getRoles().remove(userRole);

        // If no roles left  delete the user
        if (user.getRoles().isEmpty()) {
            usersRepository.delete(user);
        }

    }

    //add a user with role (Only for admin)
    public void createUserWithRole(AddUserDTO request) {

        System.out.println("working on adding role");

        boolean isExist = usersRepository.existsByMobileNo(request.getMobileNo());

        if(isExist){
            throw new BadRequestException("User Mobile number already Already Exists");
        }

        Users user = new Users();
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());
        user.setEmail(request.getEmail());
        user.setBloodGroup(request.getBloodGroup());
        user.setMobileNo(request.getMobileNo());

        Users user1 = usersRepository.save(user);

        UserRole userRole = new UserRole();
        userRole.setUser(user1);
        userRole.setRole(request.getRole());
        userRoleRepository.save(userRole);

    }


    //update user details
    @Transactional
    public void updateUser(Long id, UpdateUserDTO request){

        Users user = usersRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found"));

        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setMobileNo(request.getMobileNo());
        user.setBloodGroup(request.getBloodGroup());
        user.setDob(request.getDob());
        usersRepository.save(user);


        // Remove all old roles
        userRoleRepository.deleteByUserId(id);

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            List<UserRole> roles = request.getRoles()
                    .stream()
                    .distinct()
                    .map(role -> {
                        UserRole ur = new UserRole();
                        ur.setUser(user);
                        ur.setRole(role);
                        return ur;
                    })
                    .toList();

            userRoleRepository.saveAll(roles);
        }

    }


    //return all users information
    public Page<UserWithRolesDTO> getAllUsers(int page) {

        Pageable pageable  = PageRequest.of(page , 25 , Sort.by("id").descending() );

        return usersRepository.findAll(pageable)
                .map(user -> {
                    UserWithRolesDTO dto = new UserWithRolesDTO();
                    dto.setId(user.getId());
                    dto.setFirstName(user.getFirstName());
                    dto.setMiddleName(user.getMiddleName());
                    dto.setLastName(user.getLastName());
                    dto.setMobileNo(user.getMobileNo());
                    dto.setEmail(user.getEmail());
                    dto.setBloodGroup(user.getBloodGroup());
                    dto.setDob(user.getDob());

                    dto.setRoles(
                            user.getRoles()
                                    .stream()
                                    .map(UserRole::getRole)
                                    .collect(Collectors.toSet())
                    );

                    return dto;
                });

    }


    //get all users by role
    public Page<UserWithRolesDTO> getAllUsersByRole(UserRoles role, int page) {

        Pageable pageable = PageRequest.of(page, 25, Sort.by("id").descending());

        return usersRepository.findAllUsersByRole(role, pageable)
                .map(user -> {
                    UserWithRolesDTO dto = new UserWithRolesDTO();
                    dto.setId(user.getId());
                    dto.setFirstName(user.getFirstName());
                    dto.setMiddleName(user.getMiddleName());
                    dto.setLastName(user.getLastName());
                    dto.setMobileNo(user.getMobileNo());
                    dto.setEmail(user.getEmail());
                    dto.setBloodGroup(user.getBloodGroup());
                    dto.setDob(user.getDob());

                    dto.setRoles(
                            user.getRoles()
                                    .stream()
                                    .map(UserRole::getRole)
                                    .collect(Collectors.toSet())
                    );

                    return dto;
                });
    }


}
