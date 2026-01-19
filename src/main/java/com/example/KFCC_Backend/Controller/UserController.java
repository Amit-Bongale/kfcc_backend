package com.example.KFCC_Backend.Controller;

import com.example.KFCC_Backend.DTO.Users.AddUserDTO;
import com.example.KFCC_Backend.DTO.Users.UserRoleRequestDTO;
import com.example.KFCC_Backend.DTO.Users.UserWithRolesDTO;
import com.example.KFCC_Backend.Enum.UserRoles;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Service.UsersService;
import com.example.KFCC_Backend.Entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UsersService usersService;

    //get user details by token for redux
    @GetMapping("/getDetail")
    public ResponseEntity<?> getUserDetails(){
        return ResponseEntity.ok(usersService.getUserDetails());
    }

    //get all the users
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<Page<UserWithRolesDTO>> getAllUsers(@RequestParam(defaultValue = "0") int page ){
        return ResponseEntity.ok(usersService.getAllUsers(page));
    }

    //get all users based on roles
    @GetMapping("/role")
    @PreAuthorize("hasAnyRole('STAFF','ONM_COMMITTEE', 'ONM_COMMITTEE_LEADER', 'EC_MEMBER','SECRETARY' , 'MANAGER' , 'PRESIDENT' , 'SUPER_ADMIN')")
    public ResponseEntity<Page<UserWithRolesDTO>> getUsersByRole(@RequestParam UserRoles role , @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(
                usersService.getAllUsersByRole(role , page )
        );
    }

    //get user details
    @GetMapping("/userDetails")
    public ResponseEntity<?> getUserAllDetailsOfUser(@AuthenticationPrincipal CustomUserDetails user){
        return  ResponseEntity.ok(usersService.getUserAllDetails(user));
    }

    //add a user with role
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> createUserWithRole(@RequestBody AddUserDTO request){
        usersService.createUserWithRole(request);
        return ResponseEntity.ok("User Added Successfully");
    }


    //assign role to user
    @PostMapping("/assign/role")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN' )")
    public ResponseEntity<?> assignRole(@RequestBody UserRoleRequestDTO request){
        usersService.assignRole(request.getUserID(),  request.getRole());
        return ResponseEntity.ok("User Role Updated Successfully");
    }

    //delete user role
    @PostMapping("/remove/role")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN' )")
    public ResponseEntity<?> removeRole(@RequestBody UserRoleRequestDTO request){
        usersService.deleteUserRole(request.getUserID(),  request.getRole());
        return ResponseEntity.ok("User Role Deleted Successfully");
    }

}
