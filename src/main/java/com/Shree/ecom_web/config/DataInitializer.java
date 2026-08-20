package com.Shree.ecom_web.config;

import com.Shree.ecom_web.model.Product;
import com.Shree.ecom_web.repository.ProdRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Date;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProdRepo productRepo;

    public DataInitializer(ProdRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    public void run(String... args) {

        System.out.println("======================================");
        System.out.println("STARTING PRODUCT DATA INITIALIZATION");
        System.out.println("======================================");

        // Remove old products
        productRepo.deleteAll();

        try {

            RestClient client = RestClient.create();

            DummyResponse response = client
                    .get()
                    .uri("https://dummyjson.com/products?limit=0")
                    .retrieve()
                    .body(DummyResponse.class);

            if (response == null || response.products == null) {
                System.out.println("Could not fetch products.");
                return;
            }

            System.out.println(
                    "Products received from DummyJSON: "
                            + response.products.size()
            );

            int count = 0;

            /*
             * First save the real products.
             */
            for (DummyProduct dp : response.products) {

                if (count >= 500) {
                    break;
                }

                Product product = convertProduct(dp, count + 1);

                productRepo.save(product);

                count++;

                System.out.println(
                        "Saved " + count + " : " + product.getName()
                );
            }

            /*
             * If fewer than 500 products were received,
             * create additional products using the same
             * matching image/product information.
             */
            int originalCount = response.products.size();

            int index = 0;

            while (count < 500) {

                DummyProduct dp =
                        response.products.get(index % originalCount);

                Product product =
                        convertProduct(dp, count + 1);

                product.setName(
                        dp.title + " - Edition " + (count + 1)
                );

                productRepo.save(product);

                count++;

                index++;
            }

            System.out.println("======================================");
            System.out.println(
                    "TOTAL PRODUCTS SAVED: " + count
            );
            System.out.println("======================================");

        } catch (Exception e) {

            System.out.println(
                    "ERROR WHILE LOADING PRODUCTS:"
            );

            e.printStackTrace();
        }
    }

    private Product convertProduct(
            DummyProduct dp,
            int newId
    ) {

        Product product = new Product();

        product.setName(dp.title);

        product.setDescription(
                dp.description
        );

        product.setBrand(
                dp.brand != null
                        ? dp.brand
                        : "Generic"
        );

        product.setCategory(
                dp.category
        );

        product.setSubcategory(
                dp.category
        );

        product.setPrice(
                dp.price
        );

        product.setDiscount(
                dp.discountPercentage
        );

        double finalPrice =
                dp.price -
                        (dp.price *
                                dp.discountPercentage /
                                100);

        product.setFinalPrice(
                finalPrice
        );

        product.setRating(
                dp.rating
        );

        product.setReviewCount(
                dp.reviews != null
                        ? dp.reviews.size()
                        : 0
        );

        product.setStockQuantity(
                dp.stock
        );

        product.setProductAvailable(
                dp.stock > 0
        );

        product.setFeatured(
                dp.rating >= 4.5
        );

        product.setTrending(
                dp.rating >= 4.0
        );

        product.setReleaseDate(
                new Date()
        );

        /*
         * IMPORTANT:
         * We use the actual product-specific
         * image supplied by DummyJSON.
         */
        product.setImageUrl(
                dp.thumbnail
        );

        /*
         * We don't need to store the actual
         * image bytes in the database.
         */
        product.setImageData(null);
        product.setImageName(null);
        product.setImageType(null);

        return product;
    }

    /*
     * Response from DummyJSON
     */
    public static class DummyResponse {

        public List<DummyProduct> products;
        public int total;
        public int skip;
        public int limit;
    }

    /*
     * Product returned by DummyJSON
     */
    public static class DummyProduct {

        public int id;

        public String title;

        public String description;

        public String category;

        public double price;

        public double discountPercentage;

        public double rating;

        public int stock;

        public String brand;

        public List<DummyReview> reviews;

        public String thumbnail;
    }

    /*
     * Review object
     */
    public static class DummyReview {

        public int rating;

        public String comment;

        public String date;

        public String reviewerName;

        public String reviewerEmail;
    }
}