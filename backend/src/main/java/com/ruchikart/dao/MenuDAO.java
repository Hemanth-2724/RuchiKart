package com.ruchikart.dao;

import com.ruchikart.model.Menu;
import com.ruchikart.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {

    private Menu mapRow(ResultSet rs) throws SQLException {
        Menu menu = new Menu();
        menu.setMenuID(rs.getInt("MenuID"));
        menu.setRestaurantID(rs.getInt("RestaurantID"));
        menu.setItemName(rs.getString("ItemName"));
        menu.setDescription(rs.getString("Description"));
        menu.setPrice(rs.getBigDecimal("Price"));
        menu.setAvailable(rs.getBoolean("IsAvailable"));
        menu.setImagePath(rs.getString("ImagePath"));
        menu.setVeg(rs.getBoolean("IsVeg"));
        return menu;
    }

    public List<Menu> findByRestaurantId(int restaurantId) throws SQLException {
        List<Menu> list = new ArrayList<>();
        String sql = "SELECT * FROM Menu WHERE RestaurantID = ? ORDER BY MenuID";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Menu> findAvailableByRestaurantId(int restaurantId) throws SQLException {
        List<Menu> list = new ArrayList<>();
        String sql = "SELECT * FROM Menu WHERE RestaurantID = ? AND IsAvailable = TRUE ORDER BY MenuID";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Menu findById(int menuId) throws SQLException {
        String sql = "SELECT * FROM Menu WHERE MenuID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, menuId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public Menu create(Menu menu) throws SQLException {
        String sql = "INSERT INTO Menu (RestaurantID, ItemName, Description, Price, IsAvailable, ImagePath, IsVeg) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, menu.getRestaurantID());
            ps.setString(2, menu.getItemName());
            ps.setString(3, menu.getDescription());
            ps.setBigDecimal(4, menu.getPrice());
            ps.setBoolean(5, menu.isAvailable());
            ps.setString(6, menu.getImagePath());
            ps.setBoolean(7, menu.isVeg());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) menu.setMenuID(keys.getInt(1));
            }
        }
        return menu;
    }

    public boolean update(Menu menu) throws SQLException {
        String sql = "UPDATE Menu SET ItemName = ?, Description = ?, Price = ?, IsAvailable = ?, ImagePath = ?, IsVeg = ? WHERE MenuID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, menu.getItemName());
            ps.setString(2, menu.getDescription());
            ps.setBigDecimal(3, menu.getPrice());
            ps.setBoolean(4, menu.isAvailable());
            ps.setString(5, menu.getImagePath());
            ps.setBoolean(6, menu.isVeg());
            ps.setInt(7, menu.getMenuID());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int menuId) throws SQLException {
        String sql = "DELETE FROM Menu WHERE MenuID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, menuId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean toggleAvailability(int menuId, boolean isAvailable) throws SQLException {
        String sql = "UPDATE Menu SET IsAvailable = ? WHERE MenuID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isAvailable);
            ps.setInt(2, menuId);
            return ps.executeUpdate() > 0;
        }
    }

    public int countByRestaurantId(int restaurantId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Menu WHERE RestaurantID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }
}
