package com.example.KFCC_Backend.Service.CustomUserDetails;

import com.example.KFCC_Backend.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String mobileNo;
    private final String firstName;
    private final Collection<? extends GrantedAuthority> authorities;


    public CustomUserDetails(Users user) {
        this.userId = user.getId();
        this.mobileNo = user.getMobileNo();
        this.firstName = user.getFirstName();
        this.authorities = user.getRoles()
                .stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getRole().name()))
                .toList();
    }

    public Long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // OTP based – no password
    }

    @Override
    public String getUsername() {
        return mobileNo;
    }


    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}

