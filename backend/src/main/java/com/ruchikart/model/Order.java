package com.ruchikart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private int orderID;
    private int userID;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String status;
    private String paymentMethod;
    private int restaurantID;
    private Integer deliveryPartnerID;

    // Extra fields for joins (not in DB column)
    private String restaurantName;
    private String customerUsername;
    private java.util.List<OrderItem> items;

    public Order() {}

    public Order(int orderID, int userID, LocalDateTime orderDate, BigDecimal totalAmount,
                 String status, String paymentMethod, int restaurantID) {
        this.orderID = orderID;
        this.userID = userID;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.restaurantID = restaurantID;
    }

    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public int getRestaurantID() { return restaurantID; }
    public void setRestaurantID(int restaurantID) { this.restaurantID = restaurantID; }

    public Integer getDeliveryPartnerID() { return deliveryPartnerID; }
    public void setDeliveryPartnerID(Integer deliveryPartnerID) { this.deliveryPartnerID = deliveryPartnerID; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getCustomerUsername() { return customerUsername; }
    public void setCustomerUsername(String customerUsername) { this.customerUsername = customerUsername; }

    public java.util.List<OrderItem> getItems() { return items; }
    public void setItems(java.util.List<OrderItem> items) { this.items = items; }

    @Override
    public String toString() {
        return "Order{orderID=" + orderID + ", status='" + status + "', totalAmount=" + totalAmount + "}";
    }
}
