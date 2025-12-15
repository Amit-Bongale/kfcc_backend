package com.example.KFCC_Backend.Service.CustomUserDetails;

import com.example.KFCC_Backend.Repository.UsersRepository;
import com.example.KFCC_Backend.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String mobileNo) throws UsernameNotFoundException {
        Users user = usersRepository.findByMobileNo(mobileNo)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(user);
    }
}
