package com.Shree.ecom_web.service;

import com.Shree.ecom_web.model.Product;
import com.Shree.ecom_web.model.WishlistItem;
import com.Shree.ecom_web.repository.ProdRepo;
import com.Shree.ecom_web.repository.WishlistRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepo wishlistRepo;

    @Autowired
    private ProdRepo productRepo;


    // ==========================================
    // ADD TO WISHLIST
    // ==========================================

    public WishlistItem addToWishlist(
            int userId,
            int productId
    ) {

        Product product =
                productRepo.findById(productId)
                        .orElse(null);

        if (product == null) {

            throw new RuntimeException(
                    "Product not found"
            );
        }


        WishlistItem existing =
                wishlistRepo
                        .findByUserIdAndProductId(
                                userId,
                                productId
                        )
                        .orElse(null);


        if (existing != null) {

            return existing;
        }


        WishlistItem item =
                new WishlistItem();

        item.setUserId(userId);

        item.setProductId(productId);


        return wishlistRepo.save(item);
    }


    // ==========================================
    // GET WISHLIST
    // ==========================================

    public List<WishlistItem> getWishlist(
            int userId
    ) {

        return wishlistRepo.findByUserId(userId);
    }


    // ==========================================
    // REMOVE
    // ==========================================

    public void removeFromWishlist(
            int userId,
            int productId
    ) {

        WishlistItem item =
                wishlistRepo
                        .findByUserIdAndProductId(
                                userId,
                                productId
                        )
                        .orElse(null);

        if (item == null) {

            throw new RuntimeException(
                    "Product is not in wishlist"
            );
        }

        wishlistRepo.delete(item);
    }
}