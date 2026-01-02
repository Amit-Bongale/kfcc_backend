package com.example.KFCC_Backend.Service.CustomUserDetails;

import com.example.KFCC_Backend.Entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String mobileNo;
    private final String firstName;
    private final Set<String> roles;
    private final Collection<? extends GrantedAuthority> authorities;


    public CustomUserDetails(Users user) {
        this.userId = user.getId();
        this.mobileNo = user.getMobileNo();
        this.firstName = user.getFirstName();
        this.authorities = user.getRoles()
                .stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getRole().name()))
                .toList();
        this.roles = user.getRoles()
                .stream()
                .map(r -> r.getRole().name())
                .collect(Collectors.toSet());
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

    public Set<String> getRoles() {
        return roles;
    }


    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}

