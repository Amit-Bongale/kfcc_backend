package com.example.KFCC_Backend.Controller;

import com.example.KFCC_Backend.DTO.SignupRequestDTO;
import com.example.KFCC_Backend.Enum.UserRoles;
import com.example.KFCC_Backend.Repository.Membership.MembershipRepository;
import com.example.KFCC_Backend.Repository.Users.UsersRepository;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetailsService;
import com.example.KFCC_Backend.Service.OtpService;
import com.example.KFCC_Backend.Entity.UserRole;
import com.example.KFCC_Backend.Entity.Users;
import com.example.KFCC_Backend.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    // send otp request for signup
    @PostMapping("/signup/request-otp")
    public ResponseEntity<?> signupRequestOtp(@RequestParam String mobileNo) {
        System.out.println("signup otp request");
        if (userRepository.findByMobileNo(mobileNo).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "User already registered"));
        }

        otpService.sendOtp(mobileNo);

        return ResponseEntity.ok(
                Map.of(
                        "message", "OTP sent for signup",
                        "mobileNo", mobileNo
                )
        );
    }


    // create user validating otp
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDTO request){

        if(userRepository.findByMobileNo(request.getMobileNo()).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error" , "User Already Exist"));
        }

        boolean otpValid = otpService.verifyOtp(
                request.getMobileNo(),
                request.getOtp()
        );

        if (!otpValid) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired OTP"));
        }

        Users user = new Users();
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setMobileNo(request.getMobileNo());
        user.setEmail(request.getEmail());
        user.getBloodGroup(request.getBloodGroup());
        user.setDob(request.getDob());

        UserRole userRole = new UserRole();
        userRole.setRole(UserRoles.USER);
        userRole.setUser(user);

        user.getRoles().add(userRole);

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message" , "user registered successfully"));

    }


    // send otp for login
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


    // verify otp and user return jwt
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
