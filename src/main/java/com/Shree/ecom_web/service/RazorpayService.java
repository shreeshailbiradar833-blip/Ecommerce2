package com.Shree.ecom_web.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;


    // ==========================================
    // CREATE RAZORPAY ORDER
    // ==========================================

    public Order createOrder(
            double amount,
            String receipt
    ) throws Exception {

        RazorpayClient razorpayClient =
                new RazorpayClient(
                        keyId,
                        keySecret
                );

        JSONObject orderRequest =
                new JSONObject();

        // Convert rupees to paise
        int amountInPaise =
                (int) Math.round(amount * 100);

        orderRequest.put(
                "amount",
                amountInPaise
        );

        orderRequest.put(
                "currency",
                "INR"
        );

        orderRequest.put(
                "receipt",
                receipt
        );

        orderRequest.put(
                "payment_capture",
                1
        );

        return razorpayClient.orders.create(
                orderRequest
        );
    }


    // ==========================================
    // VERIFY RAZORPAY PAYMENT
    // ==========================================

    public boolean verifyPayment(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature
    ) throws Exception {

        JSONObject options =
                new JSONObject();

        options.put(
                "razorpay_order_id",
                razorpayOrderId
        );

        options.put(
                "razorpay_payment_id",
                razorpayPaymentId
        );

        options.put(
                "razorpay_signature",
                razorpaySignature
        );

        return Utils.verifyPaymentSignature(
                options,
                keySecret
        );
    }


    // ==========================================
    // GET RAZORPAY KEY ID
    // ==========================================

    public String getKeyId() {
        return keyId;
    }
}