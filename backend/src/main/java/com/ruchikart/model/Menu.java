package com.ruchikart.model;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class Menu {
    private int menuID;
    private int restaurantID;
    private String itemName;
    private String description;
    private BigDecimal price;
    private boolean isAvailable;
    private String imagePath;
    @SerializedName("isVeg")
    private boolean isVeg;

    public Menu() {}

    public Menu(int menuID, int restaurantID, String itemName, String description,
                BigDecimal price, boolean isAvailable, String imagePath, boolean isVeg) {
        this.menuID = menuID;
        this.restaurantID = restaurantID;
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.isAvailable = isAvailable;
        this.imagePath = imagePath;
        this.isVeg = isVeg;
    }

    public int getMenuID() { return menuID; }
    public void setMenuID(int menuID) { this.menuID = menuID; }

    public int getRestaurantID() { return restaurantID; }
    public void setRestaurantID(int restaurantID) { this.restaurantID = restaurantID; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public boolean isVeg() { return isVeg; }
    public void setVeg(boolean veg) { isVeg = veg; }

    @Override
    public String toString() {
        return "Menu{menuID=" + menuID + ", itemName='" + itemName + "', price=" + price + "}";
    }
}
