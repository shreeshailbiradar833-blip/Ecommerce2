package com.Shree.ecom_web.controller;

import com.Shree.ecom_web.model.CartItem;
import com.Shree.ecom_web.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;


    // ==========================================
    // ADD TO CART
    // ==========================================

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(

            @RequestParam int userId,

            @RequestParam int productId,

            @RequestParam(defaultValue = "1")
            int quantity
    ) {

        try {

            CartItem item =
                    cartService.addToCart(
                            userId,
                            productId,
                            quantity
                    );

            return ResponseEntity.ok(item);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ==========================================
    // GET CART
    // ==========================================

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCart(
            @PathVariable int userId
    ) {

        return ResponseEntity.ok(
                cartService.getCart(userId)
        );
    }


    // ==========================================
    // CART TOTAL
    // ==========================================

    @GetMapping("/{userId}/total")
    public ResponseEntity<Double> getCartTotal(
            @PathVariable int userId
    ) {

        return ResponseEntity.ok(
                cartService.getCartTotal(userId)
        );
    }


    // ==========================================
    // UPDATE QUANTITY
    // ==========================================

    @PutMapping("/{userId}/{productId}")
    public ResponseEntity<?> updateQuantity(

            @PathVariable int userId,

            @PathVariable int productId,

            @RequestParam int quantity
    ) {

        try {

            return ResponseEntity.ok(
                    cartService.updateQuantity(
                            userId,
                            productId,
                            quantity
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ==========================================
    // REMOVE
    // ==========================================

    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<String> removeFromCart(

            @PathVariable int userId,

            @PathVariable int productId
    ) {

        try {

            cartService.removeFromCart(
                    userId,
                    productId
            );

            return ResponseEntity.ok(
                    "Product removed from cart"
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ==========================================
    // CLEAR CART
    // ==========================================

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<String> clearCart(

            @PathVariable int userId
    ) {

        cartService.clearCart(userId);

        return ResponseEntity.ok(
                "Cart cleared successfully"
        );
    }
}