package com.ruchikart.dao;

import com.ruchikart.model.Restaurant;
import com.ruchikart.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDAO {

    private Restaurant mapRow(ResultSet rs) throws SQLException {
        Restaurant r = new Restaurant();
        r.setRestaurantID(rs.getInt("RestaurantID"));
        r.setName(rs.getString("Name"));
        r.setCuisineType(rs.getString("CuisineType"));
        r.setDeliveryTime(rs.getInt("DeliveryTime"));
        r.setAddress(rs.getString("Address"));
        r.setRating(rs.getBigDecimal("Rating"));
        r.setActive(rs.getBoolean("IsActive"));
        r.setImagePath(rs.getString("ImagePath"));
        r.setVeg(rs.getBoolean("IsVeg"));
        return r;
    }

    public List<Restaurant> findAll() throws SQLException {
        List<Restaurant> list = new ArrayList<>();
        String sql = "SELECT * FROM Restaurant WHERE IsActive = TRUE ORDER BY Rating DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Restaurant> findAllForAdmin() throws SQLException {
        List<Restaurant> list = new ArrayList<>();
        String sql = "SELECT * FROM Restaurant ORDER BY RestaurantID";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Restaurant findById(int restaurantId) throws SQLException {
        String sql = "SELECT * FROM Restaurant WHERE RestaurantID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public Restaurant create(Restaurant restaurant) throws SQLException {
        String sql = "INSERT INTO Restaurant (Name, CuisineType, DeliveryTime, Address, Rating, IsActive, ImagePath, IsVeg) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, restaurant.getName());
            ps.setString(2, restaurant.getCuisineType());
            ps.setInt(3, restaurant.getDeliveryTime());
            ps.setString(4, restaurant.getAddress());
            ps.setBigDecimal(5, restaurant.getRating());
            ps.setBoolean(6, restaurant.isActive());
            ps.setString(7, restaurant.getImagePath());
            ps.setBoolean(8, restaurant.isVeg());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) restaurant.setRestaurantID(keys.getInt(1));
            }
        }
        return restaurant;
    }

    public boolean update(Restaurant restaurant) throws SQLException {
        String sql = "UPDATE Restaurant SET Name = ?, CuisineType = ?, DeliveryTime = ?, Address = ?, Rating = ?, ImagePath = ?, IsVeg = ? WHERE RestaurantID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, restaurant.getName());
            ps.setString(2, restaurant.getCuisineType());
            ps.setInt(3, restaurant.getDeliveryTime());
            ps.setString(4, restaurant.getAddress());
            ps.setBigDecimal(5, restaurant.getRating());
            ps.setString(6, restaurant.getImagePath());
            ps.setBoolean(7, restaurant.isVeg());
            ps.setInt(8, restaurant.getRestaurantID());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean toggleActive(int restaurantId, boolean isActive) throws SQLException {
        String sql = "UPDATE Restaurant SET IsActive = ? WHERE RestaurantID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            ps.setInt(2, restaurantId);
            return ps.executeUpdate() > 0;
        }
    }
}
