package com.Shree.ecom_web.repository;

import com.Shree.ecom_web.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Orders, Integer> {

    List<Orders> findByUserId(int userId);
}