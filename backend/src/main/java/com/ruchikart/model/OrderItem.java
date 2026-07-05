package com.ruchikart.model;

import java.math.BigDecimal;

public class OrderItem {
    private int orderItemID;
    private int orderID;
    private int quantity;
    private BigDecimal itemTotal;
    private int menuID;

    // Extra field for joins
    private String itemName;

    public OrderItem() {}

    public OrderItem(int orderItemID, int orderID, int quantity, BigDecimal itemTotal, int menuID) {
        this.orderItemID = orderItemID;
        this.orderID = orderID;
        this.quantity = quantity;
        this.itemTotal = itemTotal;
        this.menuID = menuID;
    }

    public int getOrderItemID() { return orderItemID; }
    public void setOrderItemID(int orderItemID) { this.orderItemID = orderItemID; }

    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getItemTotal() { return itemTotal; }
    public void setItemTotal(BigDecimal itemTotal) { this.itemTotal = itemTotal; }

    public int getMenuID() { return menuID; }
    public void setMenuID(int menuID) { this.menuID = menuID; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    @Override
    public String toString() {
        return "OrderItem{orderItemID=" + orderItemID + ", menuID=" + menuID + ", quantity=" + quantity + "}";
    }
}
