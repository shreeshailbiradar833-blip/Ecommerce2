package com.Shree.ecom_web.service;

import com.Shree.ecom_web.model.CartItem;
import com.Shree.ecom_web.model.Product;
import com.Shree.ecom_web.repository.CartRepo;
import com.Shree.ecom_web.repository.ProdRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private ProdRepo productRepo;


    // ==========================================
    // ADD TO CART
    // ==========================================

    public CartItem addToCart(
            int userId,
            int productId,
            int quantity
    ) {

        Product product = productRepo
                .findById(productId)
                .orElse(null);

        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        if (!product.isProductAvailable()) {
            throw new RuntimeException(
                    "Product is currently unavailable"
            );
        }

        if (quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }

        if (quantity > product.getStockQuantity()) {
            throw new RuntimeException(
                    "Only " +
                            product.getStockQuantity() +
                            " items are available"
            );
        }

        CartItem cartItem = cartRepo
                .findByUserIdAndProductId(
                        userId,
                        productId
                )
                .orElse(null);

        if (cartItem == null) {

            cartItem = new CartItem();

            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);

        } else {

            int newQuantity =
                    cartItem.getQuantity() + quantity;

            if (newQuantity >
                    product.getStockQuantity()) {

                throw new RuntimeException(
                        "Cannot add more than available stock"
                );
            }

            cartItem.setQuantity(newQuantity);
        }

        cartItem.setPrice(
                product.getFinalPrice()
        );

        cartItem.setTotalPrice(
                cartItem.getPrice() *
                        cartItem.getQuantity()
        );

        return cartRepo.save(cartItem);
    }


    // ==========================================
    // GET CART
    // ==========================================

    public List<CartItem> getCart(int userId) {

        List<CartItem> cart =
                cartRepo.findByUserId(userId);

        for (CartItem item : cart) {

            item.setTotalPrice(
                    item.getPrice() *
                            item.getQuantity()
            );
        }

        return cart;
    }


    // ==========================================
    // UPDATE QUANTITY
    // ==========================================

    public CartItem updateQuantity(
            int userId,
            int productId,
            int quantity
    ) {

        CartItem cartItem =
                cartRepo
                        .findByUserIdAndProductId(
                                userId,
                                productId
                        )
                        .orElse(null);

        if (cartItem == null) {
            throw new RuntimeException(
                    "Product is not in cart"
            );
        }

        Product product =
                productRepo
                        .findById(productId)
                        .orElse(null);

        if (product == null) {
            throw new RuntimeException(
                    "Product not found"
            );
        }

        if (quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }

        if (quantity >
                product.getStockQuantity()) {

            throw new RuntimeException(
                    "Only " +
                            product.getStockQuantity() +
                            " items are available"
            );
        }

        cartItem.setQuantity(quantity);

        cartItem.setPrice(
                product.getFinalPrice()
        );

        cartItem.setTotalPrice(
                product.getFinalPrice() *
                        quantity
        );

        return cartRepo.save(cartItem);
    }


    // ==========================================
    // REMOVE
    // ==========================================

    public void removeFromCart(
            int userId,
            int productId
    ) {

        CartItem cartItem =
                cartRepo
                        .findByUserIdAndProductId(
                                userId,
                                productId
                        )
                        .orElse(null);

        if (cartItem == null) {
            throw new RuntimeException(
                    "Product is not in cart"
            );
        }

        cartRepo.delete(cartItem);
    }


    // ==========================================
    // CLEAR CART
    // ==========================================

    public void clearCart(int userId) {

        cartRepo.deleteByUserId(userId);
    }


    // ==========================================
    // CART TOTAL
    // ==========================================

    public double getCartTotal(int userId) {

        List<CartItem> cart =
                cartRepo.findByUserId(userId);

        double total = 0;

        for (CartItem item : cart) {

            total +=
                    item.getPrice() *
                            item.getQuantity();
        }

        return total;
    }
}