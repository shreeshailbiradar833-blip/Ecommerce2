package com.Shree.ecom_web.service;

import com.Shree.ecom_web.model.Product;
import com.Shree.ecom_web.repository.ProdRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProdRepo repo;


    // Get all products
    public List<Product> getAllProducts() {
        return repo.findAll();
    }
    private void downloadImage(Product product) throws IOException {

        if (product.getImageUrl() == null ||
                product.getImageUrl().isBlank()) {
            return;
        }

        URI uri = URI.create(product.getImageUrl());

        HttpURLConnection connection =
                (HttpURLConnection) uri.toURL().openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        connection.connect();

        if (connection.getResponseCode() != 200) {
            System.out.println(
                    "Image download failed: " +
                            product.getImageUrl()
            );
            return;
        }

        String contentType = connection.getContentType();

        product.setImageType(
                contentType != null
                        ? contentType
                        : "image/jpeg"
        );

        try (InputStream inputStream =
                     connection.getInputStream()) {

            product.setImageData(
                    inputStream.readAllBytes()
            );
        }

        String url = product.getImageUrl();

        String fileName =
                url.substring(url.lastIndexOf("/") + 1);

        if (fileName.contains("?")) {
            fileName =
                    fileName.substring(
                            0,
                            fileName.indexOf("?")
                    );
        }

        product.setImageName(fileName);

        connection.disconnect();
    }


    // Get product by ID
    public Product getProductById(int id) {
        return repo.findById(id).orElse(null);
    }


    // Add product
    public Product addProduct(Product product, MultipartFile imageFile) throws IOException {

        calculateFinalPrice(product);

        if (imageFile != null && !imageFile.isEmpty()) {

            product.setImageName(
                    imageFile.getOriginalFilename()
            );

            product.setImageType(
                    imageFile.getContentType()
            );

            product.setImageData(
                    imageFile.getBytes()
            );

        } else {

            downloadImage(product);
        }

        return repo.save(product);
    }


    // Update product
    public Product updateProductById(
            int id,
            Product product,
            MultipartFile imageFile
    ) throws IOException {

        Product existing = repo.findById(id).orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setBrand(product.getBrand());
        existing.setCategory(product.getCategory());
        existing.setSubcategory(product.getSubcategory());

        existing.setPrice(product.getPrice());
        existing.setDiscount(product.getDiscount());

        existing.setStockQuantity(product.getStockQuantity());
        existing.setProductAvailable(product.isProductAvailable());

        existing.setRating(product.getRating());
        existing.setReviewCount(product.getReviewCount());

        existing.setFeatured(product.isFeatured());
        existing.setTrending(product.isTrending());

        existing.setReleaseDate(product.getReleaseDate());

        calculateFinalPrice(existing);

        if (imageFile != null && !imageFile.isEmpty()) {

            existing.setImageName(imageFile.getOriginalFilename());
            existing.setImageType(imageFile.getContentType());
            existing.setImageData(imageFile.getBytes());
        }

        return repo.save(existing);
    }


    // Delete product
    public void deleteProductbyID(int id) {
        repo.deleteById(id);
    }


    // Search
    public List<Product> searchProducts(String keyword) {
        return repo.searchProduct(keyword);
    }


    // Category
    public List<Product> getProductsByCategory(String category) {
        return repo.findByCategoryIgnoreCase(category);
    }


    // Subcategory
    public List<Product> getProductsBySubcategory(String subcategory) {
        return repo.findBySubcategoryIgnoreCase(subcategory);
    }


    // Brand
    public List<Product> getProductsByBrand(String brand) {
        return repo.findByBrandIgnoreCase(brand);
    }


    // Featured
    public List<Product> getFeaturedProducts() {
        return repo.findByFeaturedTrue();
    }


    // Trending
    public List<Product> getTrendingProducts() {
        return repo.findByTrendingTrue();
    }


    // Available
    public List<Product> getAvailableProducts() {
        return repo.findByProductAvailableTrue();
    }


    // Price range
    public List<Product> getProductsByPriceRange(
            double minPrice,
            double maxPrice
    ) {
        return repo.findByPriceBetween(minPrice, maxPrice);
    }


    // Category + Subcategory
    public List<Product> getProductsByCategoryAndSubcategory(
            String category,
            String subcategory
    ) {
        return repo.findByCategoryIgnoreCaseAndSubcategoryIgnoreCase(
                category,
                subcategory
        );
    }


    // Sort by price
    public List<Product> sortProductsByPrice(
            String order
    ) {

        List<Product> products = repo.findAll();

        if (order.equalsIgnoreCase("desc")) {

            products.sort(
                    Comparator.comparingDouble(Product::getFinalPrice)
                            .reversed()
            );

        } else {

            products.sort(
                    Comparator.comparingDouble(Product::getFinalPrice)
            );
        }

        return products;
    }


    // Calculate final price
    private void calculateFinalPrice(Product product) {

        double price = product.getPrice();
        double discount = product.getDiscount();

        double finalPrice = price - (price * discount / 100);

        product.setFinalPrice(finalPrice);
    }
}