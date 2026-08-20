package com.Shree.ecom_web.repository;

import com.Shree.ecom_web.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepo
        extends JpaRepository<WishlistItem, Integer> {

    List<WishlistItem> findByUserId(int userId);

    Optional<WishlistItem> findByUserIdAndProductId(
            int userId,
            int productId
    );

    void deleteByUserId(int userId);
}
