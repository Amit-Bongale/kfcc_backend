package com.example.KFCC_Backend.Controller;

import com.example.KFCC_Backend.Entity.Payments;
import com.example.KFCC_Backend.Enum.PaymentModule;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;


    @PostMapping("/create-order")
    public ResponseEntity<?> crateOrder(
                                        @RequestParam PaymentModule module,
                                        @RequestParam Long applicationId,
                                        @AuthenticationPrincipal CustomUserDetails user
    ) throws RazorpayException {

        Order order = paymentService.createOrder(
                "kfcc_" + System.currentTimeMillis() , user , module , applicationId
        );

        return ResponseEntity.ok(order.toString());

    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestBody Map<String, String> data) throws Exception {

        return paymentService.verifyOrder(data);

    }


}
