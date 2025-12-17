package com.example.KFCC_Backend.Controller;

import com.example.KFCC_Backend.Service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UsersService usersService;

    @GetMapping("/getDetail")
    public ResponseEntity<?> getUserDetails(){
        return ResponseEntity.ok(usersService.getUserDetails());
    }
}
