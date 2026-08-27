package com.ruchikart.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Restaurant")
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RestaurantID")
    private Integer restaurantId;

    @Column(name = "Name", nullable = false, length = 150)
    private String name;

    @Column(name = "CuisineType", nullable = false, length = 100)
    private String cuisineType;

    @Column(name = "DeliveryTime", nullable = false)
    private Integer deliveryTime;

    @Column(name = "Address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "Rating", precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "IsActive", nullable = false)
    private boolean active = true;

    @Column(name = "ImagePath", length = 255)
    private String imagePath;

    @Column(name = "IsVeg", nullable = false)
    private boolean veg = false;

    @Column(name = "OwnerID")
    private Integer ownerId;

    public RestaurantEntity() {}

    public Integer getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Integer restaurantId) { this.restaurantId = restaurantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCuisineType() { return cuisineType; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }

    public Integer getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(Integer deliveryTime) { this.deliveryTime = deliveryTime; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public boolean isVeg() { return veg; }
    public void setVeg(boolean veg) { this.veg = veg; }

    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }
}
