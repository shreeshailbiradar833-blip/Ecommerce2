package com.Shree.ecom_web.controller;

import com.Shree.ecom_web.model.Orders;
import com.Shree.ecom_web.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;


    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(

            @RequestParam int userId,

            @RequestParam String username,

            @RequestParam String firstName,

            @RequestParam String lastName,

            @RequestParam String phone,

            @RequestParam String address,

            @RequestParam String city,

            @RequestParam String state,

            @RequestParam String pincode
    ) {

        try {

            Orders order =
                    orderService.createOrder(
                            userId,
                            username,
                            firstName,
                            lastName,
                            phone,
                            address,
                            city,
                            state,
                            pincode
                    );

            return ResponseEntity.ok(order);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Orders>> getUserOrders(
            @PathVariable int userId
    ) {

        return ResponseEntity.ok(
                orderService.getOrdersByUser(userId)
        );
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(
            @PathVariable int orderId
    ) {

        try {

            return ResponseEntity.ok(
                    orderService.getOrderById(orderId)
            );

        } catch (Exception e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }
}