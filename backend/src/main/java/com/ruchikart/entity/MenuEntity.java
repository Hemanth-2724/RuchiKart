package com.ruchikart.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Menu")
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MenuID")
    private Integer menuId;

    @Column(name = "RestaurantID", nullable = false)
    private Integer restaurantId;

    @Column(name = "ItemName", nullable = false, length = 150)
    private String itemName;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "Price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "IsAvailable", nullable = false)
    private boolean available = true;

    @Column(name = "ImagePath", length = 255)
    private String imagePath;

    @Column(name = "IsVeg", nullable = false)
    private boolean veg = true;

    public MenuEntity() {}

    public Integer getMenuId() { return menuId; }
    public void setMenuId(Integer menuId) { this.menuId = menuId; }

    public Integer getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Integer restaurantId) { this.restaurantId = restaurantId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public boolean isVeg() { return veg; }
    public void setVeg(boolean veg) { this.veg = veg; }
}
