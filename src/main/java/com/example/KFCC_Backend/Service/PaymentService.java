package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.Entity.Payments;
import com.example.KFCC_Backend.Entity.Title.TitleRegistration;
import com.example.KFCC_Backend.Entity.Users;
import com.example.KFCC_Backend.Enum.PaymentModule;
import com.example.KFCC_Backend.ExceptionHandlers.ResourceNotFoundException;
import com.example.KFCC_Backend.Repository.PaymentRepository;
import com.example.KFCC_Backend.Repository.Users.UsersRepository;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaymentService {

    @Autowired
    private RazorpayClient razorpayClient;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MembershipApplicationService membershipApplicationService;

    @Autowired
    private TitleRegistrationService titleRegistrationService;

    @Autowired
    private IdCardRequestService idCardRequestService;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;


    public Order createOrder(
            String receiptId , CustomUserDetails user,
            PaymentModule module , Long applicationId) throws RazorpayException {

        if (module == null) {
            throw new IllegalArgumentException("Payment module is required");
        }

        int amount = module.getAmount();

        JSONObject options = new JSONObject();
        options.put("amount", amount * 100); // INR → paise
        options.put("currency", "INR");
        options.put("receipt", receiptId);

        Order order = razorpayClient.orders.create(options);

        Users applicant = usersRepository.findById(user.getUserId()).orElseThrow(() -> new ResourceNotFoundException("user not found"));

        Payments payment = new Payments();
        payment.setUser(applicant);
        payment.setModule(module);
        payment.setApplicationId(applicationId);
        payment.setAmount(amount);
        payment.setRazorpayOrderId(order.get("id"));
        payment.setStatus(Payments.PaymentStatus.CREATED);

        paymentRepository.save(payment);

        return order;
    }


    public ResponseEntity<String> verifyOrder(Map<String, String> data) throws RazorpayException {

        String orderId = data.get("razorpay_order_id");
        String paymentId = data.get("razorpay_payment_id");
        String signature = data.get("razorpay_signature");

        Payments payment = paymentRepository
                .findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        String payload = orderId + "|" + paymentId;

        String expectedSignature = Utils.getHash(
                payload,
                razorpayKeySecret
        );

        if (!expectedSignature.equals(signature)) {
            // if Payment is not legit
            payment.setStatus(Payments.PaymentStatus.FAILED);
            paymentRepository.save(payment);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid payment signature");
        }

        payment.setRazorpayPaymentId(paymentId);
        payment.setStatus(Payments.PaymentStatus.PAID);
        paymentRepository.save(payment);

        switch (payment.getModule()){
            case MEMBERSHIP ->  membershipApplicationService.markAsPaid(payment.getApplicationId());
            case TITLE -> titleRegistrationService.markAsPaid(payment.getApplicationId());
            case IDCARD ->  idCardRequestService.markAsPaid(payment.getApplicationId());
        }

        return ResponseEntity.ok("Payment verified");
    }
}
