package com.Shree.ecom_web.repository;

import com.Shree.ecom_web.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<CartItem, Integer> {

    List<CartItem> findByUserId(int userId);

    Optional<CartItem> findByUserIdAndProductId(
            int userId,
            int productId
    );

    void deleteByUserId(int userId);
}