package com.Shree.ecom_web.controller;

import com.Shree.ecom_web.service.RazorpayService;
import com.razorpay.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(
        origins = "http://localhost:5173"
)
public class PaymentController {

    @Autowired
    private RazorpayService razorpayService;


    // ==========================================
    // CREATE RAZORPAY ORDER
    // ==========================================

    @PostMapping("/create-order")
    public ResponseEntity<?> createPaymentOrder(
            @RequestParam double amount
    ) {

        try {

            if (amount <= 0) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Amount must be greater than 0"
                        );
            }

            String receipt =
                    "receipt_" +
                            System.currentTimeMillis();

            Order order =
                    razorpayService.createOrder(
                            amount,
                            receipt
                    );

            return ResponseEntity.ok(
                    order.toString()
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Payment order creation failed: "
                                    + e.getMessage()
                    );
        }
    }


    // ==========================================
    // VERIFY PAYMENT
    // ==========================================

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestBody
            PaymentVerificationRequest request
    ) {

        try {

            if (
                    request.getRazorpayOrderId() == null ||
                            request.getRazorpayPaymentId() == null ||
                            request.getRazorpaySignature() == null
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Payment details are missing"
                        );
            }

            boolean verified =
                    razorpayService.verifyPayment(
                            request.getRazorpayOrderId(),
                            request.getRazorpayPaymentId(),
                            request.getRazorpaySignature()
                    );

            if (verified) {

                return ResponseEntity.ok(
                        "Payment verified successfully"
                );
            }

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Payment verification failed"
                    );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Payment verification failed: "
                                    + e.getMessage()
                    );
        }
    }


    // ==========================================
    // REQUEST DTO
    // ==========================================

    public static class PaymentVerificationRequest {

        private String razorpayOrderId;

        private String razorpayPaymentId;

        private String razorpaySignature;


        public String getRazorpayOrderId() {
            return razorpayOrderId;
        }

        public void setRazorpayOrderId(
                String razorpayOrderId
        ) {
            this.razorpayOrderId =
                    razorpayOrderId;
        }


        public String getRazorpayPaymentId() {
            return razorpayPaymentId;
        }

        public void setRazorpayPaymentId(
                String razorpayPaymentId
        ) {
            this.razorpayPaymentId =
                    razorpayPaymentId;
        }


        public String getRazorpaySignature() {
            return razorpaySignature;
        }

        public void setRazorpaySignature(
                String razorpaySignature
        ) {
            this.razorpaySignature =
                    razorpaySignature;
        }
    }
}