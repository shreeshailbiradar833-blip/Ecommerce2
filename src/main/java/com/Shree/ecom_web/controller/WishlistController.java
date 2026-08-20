package com.Shree.ecom_web.controller;

import com.Shree.ecom_web.model.WishlistItem;
import com.Shree.ecom_web.service.WishlistService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;


    // ==========================================
    // ADD TO WISHLIST
    // ==========================================

    @PostMapping("/add")
    public ResponseEntity<?> addToWishlist(

            @RequestParam int userId,

            @RequestParam int productId
    ) {

        try {

            return ResponseEntity.ok(
                    wishlistService.addToWishlist(
                            userId,
                            productId
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ==========================================
    // GET WISHLIST
    // ==========================================

    @GetMapping("/{userId}")
    public ResponseEntity<List<WishlistItem>>
    getWishlist(

            @PathVariable int userId
    ) {

        return ResponseEntity.ok(
                wishlistService.getWishlist(userId)
        );
    }


    // ==========================================
    // REMOVE
    // ==========================================

    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<String>
    removeFromWishlist(

            @PathVariable int userId,

            @PathVariable int productId
    ) {

        try {

            wishlistService.removeFromWishlist(
                    userId,
                    productId
            );

            return ResponseEntity.ok(
                    "Product removed from wishlist"
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}