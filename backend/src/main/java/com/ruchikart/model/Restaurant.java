package com.ruchikart.model;

import java.math.BigDecimal;

public class Restaurant {
    private int restaurantID;
    private String name;
    private String cuisineType;
    private int deliveryTime;
    private String address;
    private BigDecimal rating;
    private boolean isActive;
    private String imagePath;
    private boolean isVeg;
    private int ownerID;

    public Restaurant() {}

    public Restaurant(int restaurantID, String name, String cuisineType, int deliveryTime,
                      String address, BigDecimal rating, boolean isActive, String imagePath, boolean isVeg) {
        this.restaurantID = restaurantID;
        this.name = name;
        this.cuisineType = cuisineType;
        this.deliveryTime = deliveryTime;
        this.address = address;
        this.rating = rating;
        this.isActive = isActive;
        this.imagePath = imagePath;
        this.isVeg = isVeg;
    }

    public int getOwnerID() { return ownerID; }
    public void setOwnerID(int ownerID) { this.ownerID = ownerID; }

    public int getRestaurantID() { return restaurantID; }
    public void setRestaurantID(int restaurantID) { this.restaurantID = restaurantID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCuisineType() { return cuisineType; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }

    public int getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(int deliveryTime) { this.deliveryTime = deliveryTime; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public boolean isVeg() { return isVeg; }
    public void setVeg(boolean veg) { isVeg = veg; }

    @Override
    public String toString() {
        return "Restaurant{restaurantID=" + restaurantID + ", name='" + name + "'}";
    }
}
