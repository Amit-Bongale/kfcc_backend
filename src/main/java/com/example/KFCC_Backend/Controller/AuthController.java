package com.example.KFCC_Backend.Controller;

import com.example.KFCC_Backend.Repository.MembershipRepository;
import com.example.KFCC_Backend.Repository.UsersRepository;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetailsService;
import com.example.KFCC_Backend.Service.OtpService;
import com.example.KFCC_Backend.entity.Membership;
import com.example.KFCC_Backend.entity.Users;
import com.example.KFCC_Backend.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired private OtpService otpService;
    @Autowired private UsersRepository userRepository;
    @Autowired private MembershipRepository memberRepository;
    @Autowired private CustomUserDetailsService userDetailsService;
    @Autowired private JwtUtil jwtUtil;



    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestParam String mobileNo) {

        if (mobileNo == null || mobileNo.length() < 10) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Invalid mobile number"));
        }

        otpService.sendOtp(mobileNo);

        return ResponseEntity.ok(
                Map.of(
                        "message", "OTP sent successfully",
                        "mobileNo", mobileNo
                )
        );
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String mobileNo,
                                       @RequestParam String otp) {

        boolean isValid = otpService.verifyOtp(mobileNo, otp);

        if (!isValid) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired OTP"));
        }

        // Load User
        Users user = userRepository.findByMobileNo(mobileNo)
                .orElseThrow(() ->
                        new RuntimeException("User not registered"));

        // Load Spring Security UserDetails
        CustomUserDetails userDetails =
                (CustomUserDetails) userDetailsService
                        .loadUserByUsername(mobileNo);


        // Extract roles
        Set<String> roles = user.getRoles()
                .stream()
                .map(r -> r.getRole().name())
                .collect(Collectors.toSet());

        // Generate JWT
        String token = jwtUtil.generateToken(
                userDetails,
                roles
        );

        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "userId", user.getId(),
                        "roles", roles
                )
        );
    }
}
