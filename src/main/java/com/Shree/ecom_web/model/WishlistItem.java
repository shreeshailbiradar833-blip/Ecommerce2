package com.Shree.ecom_web.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"userId", "productId"}
                )
        }
)
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    private int userId;

    private int productId;
}