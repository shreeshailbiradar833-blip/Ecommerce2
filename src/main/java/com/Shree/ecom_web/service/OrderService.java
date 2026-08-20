package com.Shree.ecom_web.service;
import org.springframework.transaction.annotation.Transactional;

import com.Shree.ecom_web.model.CartItem;
import com.Shree.ecom_web.model.OrderItem;
import com.Shree.ecom_web.model.Orders;
import com.Shree.ecom_web.model.Product;
import com.Shree.ecom_web.repository.CartRepo;
import com.Shree.ecom_web.repository.OrderRepo;
import com.Shree.ecom_web.repository.ProdRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private ProdRepo productRepo;

    @Transactional
    public Orders createOrder(
            int userId,
            String username,
            String firstName,
            String lastName,
            String phone,
            String address,
            String city,
            String state,
            String pincode
    ) {

        List<CartItem> cartItems =
                cartRepo.findByUserId(userId);


        if (cartItems.isEmpty()) {

            throw new RuntimeException(
                    "Cart is empty"
            );
        }


        Orders order = new Orders();

        order.setUserId(userId);

        order.setUsername(username);

        order.setFirstName(firstName);

        order.setLastName(lastName);

        order.setPhone(phone);

        order.setAddress(address);

        order.setCity(city);

        order.setState(state);

        order.setPincode(pincode);


        List<OrderItem> orderItems =
                new ArrayList<>();

        double totalAmount = 0;


        for (CartItem cartItem : cartItems) {

            Product product =
                    productRepo.findById(
                            cartItem.getProductId()
                    ).orElse(null);


            if (product == null) {

                throw new RuntimeException(
                        "Product not found: "
                                + cartItem.getProductId()
                );
            }


            if (!product.isProductAvailable()) {

                throw new RuntimeException(
                        product.getName()
                                + " is currently unavailable"
                );
            }


            if (cartItem.getQuantity()
                    > product.getStockQuantity()) {

                throw new RuntimeException(
                        "Insufficient stock for "
                                + product.getName()
                );
            }


            OrderItem orderItem =
                    new OrderItem();


            orderItem.setProductId(
                    product.getId()
            );

            orderItem.setProductName(
                    product.getName()
            );

            orderItem.setPrice(
                    product.getFinalPrice()
            );

            orderItem.setQuantity(
                    cartItem.getQuantity()
            );


            double subtotal =
                    product.getFinalPrice()
                            * cartItem.getQuantity();


            orderItem.setSubtotal(subtotal);


            orderItems.add(orderItem);

            totalAmount += subtotal;
        }


        order.setItems(orderItems);

        order.setTotalAmount(totalAmount);

        order.setPaymentStatus("PENDING");

        order.setOrderStatus("PLACED");


        Orders savedOrder =
                orderRepo.save(order);


        // Clear cart after creating order
        cartRepo.deleteByUserId(userId);


        return savedOrder;
    }


    public List<Orders> getOrdersByUser(
            int userId
    ) {

        return orderRepo.findByUserId(userId);
    }


    public Orders getOrderById(
            int orderId
    ) {

        return orderRepo.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        )
                );
    }
}
//http://localhost:8080/api/orders/checkout?userId=2&username=testuser&firstName=Test&lastName=User&phone=9876543211&address=123 Main Street&city=Bengaluru&state=Karnataka&pincode=560001