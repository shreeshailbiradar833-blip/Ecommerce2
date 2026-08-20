package com.Shree.ecom_web.controller;

import com.Shree.ecom_web.model.Product;
import com.Shree.ecom_web.service.ProductService;
import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;


    // =====================================================
    // GET ALL PRODUCTS
    // =====================================================

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return service.getAllProducts();
    }


    // =====================================================
    // GET PRODUCT BY ID
    // =====================================================

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable int id
    ) {

        Product product = service.getProductById(id);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(product);
    }
    @PostMapping("/products/import")
    public ResponseEntity<List<Product>> importProducts(
            @RequestBody List<Product> products
    ) throws IOException {

        List<Product> savedProducts = new ArrayList<>();

        for (Product product : products) {

            Product saved =
                    service.addProduct(product, null);

            savedProducts.add(saved);
        }

        return ResponseEntity.ok(savedProducts);
    }


    // =====================================================
    // ADD PRODUCT WITH IMAGE
    // =====================================================

    @PostMapping(
            value = "/products",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<Product> addProduct(

            @RequestPart("product") String productJson,

            @RequestPart(
                    value = "imageFile",
                    required = false
            )
            MultipartFile imageFile

    ) throws IOException {

        // Convert JSON String → Product object
        Product product =
                objectMapper.readValue(
                        productJson,
                        Product.class
                );

        // Save product and image
        Product savedProduct =
                service.addProduct(
                        product,
                        imageFile
                );

        return ResponseEntity.ok(savedProduct);
    }


    // =====================================================
    // UPDATE PRODUCT WITH IMAGE
    // =====================================================

    @PutMapping(
            value = "/products/{id}",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<Product> updateProduct(

            @PathVariable int id,

            @RequestPart("product") String productJson,

            @RequestPart(
                    value = "imageFile",
                    required = false
            )
            MultipartFile imageFile

    ) throws IOException {

        // Convert JSON String → Product
        Product product =
                objectMapper.readValue(
                        productJson,
                        Product.class
                );

        // Update product
        Product updatedProduct =
                service.updateProductById(
                        id,
                        product,
                        imageFile
                );

        if (updatedProduct == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedProduct);
    }


    // =====================================================
    // DELETE PRODUCT
    // =====================================================

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable int id
    ) {

        service.deleteProductbyID(id);

        return ResponseEntity.noContent().build();
    }
}