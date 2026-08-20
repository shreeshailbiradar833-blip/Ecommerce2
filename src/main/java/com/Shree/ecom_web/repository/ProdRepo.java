package com.Shree.ecom_web.repository;

import com.Shree.ecom_web.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdRepo extends JpaRepository<Product, Integer> {

    // Search products
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.subcategory) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProduct(String keyword);


    // Category
    List<Product> findByCategoryIgnoreCase(String category);


    // Subcategory
    List<Product> findBySubcategoryIgnoreCase(String subcategory);


    // Brand
    List<Product> findByBrandIgnoreCase(String brand);


    // Featured products
    List<Product> findByFeaturedTrue();


    // Trending products
    List<Product> findByTrendingTrue();


    // Available products
    List<Product> findByProductAvailableTrue();


    // Price range
    List<Product> findByPriceBetween(double minPrice, double maxPrice);


    // Category + subcategory
    List<Product> findByCategoryIgnoreCaseAndSubcategoryIgnoreCase(
            String category,
            String subcategory
    );
}