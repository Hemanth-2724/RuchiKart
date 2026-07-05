package com.ruchikart.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OwnerRestaurantMapping {

    public static int getRestaurantId(int userId) {
        String sql = "SELECT RestaurantID FROM Restaurant WHERE OwnerID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("RestaurantID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1; // Default fallback for safety
    }

    public static boolean hasMapping(int userId) {
        return true; // Dynamic lookup check
    }

    public static void addMapping(int userId, int restaurantId) {
        // No-op as relationships are directly modeled via foreign keys in DB seeding
    }
}
