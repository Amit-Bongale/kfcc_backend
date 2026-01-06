package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.Entity.Membership.MembershipApplication;
import com.example.KFCC_Backend.Entity.Users;
import com.example.KFCC_Backend.ExceptionHandlers.ResourceNotFoundException;
import com.example.KFCC_Backend.Repository.IdCard.IdCardRequestRepository;
import com.example.KFCC_Backend.Repository.Membership.MembershipRepository;
import com.example.KFCC_Backend.Repository.Users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IdCardRequestService {

    @Autowired
    private IdCardRequestRepository idCardRequestRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private UsersRepository usersRepository;

    public void requestIdCard(Long userID){

        Users user = usersRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<MembershipApplication> application = membershipRepository.findByUserIdOrderBySubmittedAtDesc(userID);
    }
}
