package com.example.KFCC_Backend.Controller;

import com.example.KFCC_Backend.Enum.UserRoles;
import com.example.KFCC_Backend.Service.UsersService;
import com.example.KFCC_Backend.Entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UsersService usersService;


    @GetMapping("/getDetail")
    public ResponseEntity<?> getUserDetails(){
        return ResponseEntity.ok(usersService.getUserDetails());
    }

    @GetMapping("/role")
    @PreAuthorize("hasAnyRole('STAFF','ONM_COMMITTEE', 'ONM_COMMITTEE_LEADER', 'EC_MEMBER','SECRETARY' , 'MANAGER' , 'PRESIDENT' )")
    public ResponseEntity<List<Users>> getUsersByRole( @RequestParam UserRoles role ) {
        return ResponseEntity.ok(
                usersService.getUsersByRole(role)
        );
    }
}
