package com.example.KFCC_Backend.Controller;

import jakarta.persistence.Entity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ec")
public class Dashboard {

    @PreAuthorize("hasAnyRole('EC_MEMBER' , 'SECRETARY', 'MANAGER')")
    @GetMapping("/dashboard")
    public String dashboard() {
        return "MEMBER Dashboard";
    }
}
