package com.ruchikart.dao;

import com.ruchikart.model.User;
import com.ruchikart.util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private String getTableNameAndRole(int userId, String[] outRole) {
        if (userId >= 4000000) {
            if (outRole != null) outRole[0] = "admin";
            return "Admin";
        } else if (userId >= 3000000) {
            if (outRole != null) outRole[0] = "delivery_partner";
            return "DeliveryPartner";
        } else if (userId >= 2000000) {
            if (outRole != null) outRole[0] = "restaurant_owner";
            return "RestaurantOwner";
        } else {
            if (outRole != null) outRole[0] = "customer";
            return "Customer";
        }
    }

    private String getIdColumn(String tableName) {
        switch (tableName) {
            case "Customer": return "CustomerID";
            case "RestaurantOwner": return "OwnerID";
            case "DeliveryPartner": return "PartnerID";
            case "Admin": return "AdminID";
            default: return "UserID";
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserID(rs.getInt("UserID"));
        user.setUsername(rs.getString("Username"));
        user.setPassword(rs.getString("Password"));
        user.setEmail(rs.getString("Email"));
        user.setAddress(rs.getString("Address"));
        user.setRole(rs.getString("Role"));
        Timestamp created = rs.getTimestamp("CreatedDate");
        if (created != null) user.setCreatedDate(created.toLocalDateTime());
        Timestamp lastLogin = rs.getTimestamp("LastLoginDate");
        if (lastLogin != null) user.setLastLoginDate(lastLogin.toLocalDateTime());
        return user;
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT 'customer' AS Role, CustomerID AS UserID, Username, Password, Email, Address, CreatedDate, LastLoginDate FROM Customer WHERE Username = ?" +
                     " UNION ALL " +
                     "SELECT 'restaurant_owner' AS Role, OwnerID AS UserID, Username, Password, Email, Address, CreatedDate, LastLoginDate FROM RestaurantOwner WHERE Username = ?" +
                     " UNION ALL " +
                     "SELECT 'delivery_partner' AS Role, PartnerID AS UserID, Username, Password, Email, Address, CreatedDate, LastLoginDate FROM DeliveryPartner WHERE Username = ?" +
                     " UNION ALL " +
                     "SELECT 'admin' AS Role, AdminID AS UserID, Username, Password, Email, Address, CreatedDate, LastLoginDate FROM Admin WHERE Username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, username);
            ps.setString(3, username);
            ps.setString(4, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT 'customer' AS Role, CustomerID AS UserID, Username, Password, Email, Address, CreatedDate, LastLoginDate FROM Customer WHERE Email = ?" +
                     " UNION ALL " +
                     "SELECT 'restaurant_owner' AS Role, OwnerID AS UserID, Username, Password, Email, Address, CreatedDate, LastLoginDate FROM RestaurantOwner WHERE Email = ?" +
                     " UNION ALL " +
                     "SELECT 'delivery_partner' AS Role, PartnerID AS UserID, Username, Password, Email, Address, CreatedDate, LastLoginDate FROM DeliveryPartner WHERE Email = ?" +
                     " UNION ALL " +
                     "SELECT 'admin' AS Role, AdminID AS UserID, Username, Password, Email, Address, CreatedDate, LastLoginDate FROM Admin WHERE Email = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, email);
            ps.setString(3, email);
            ps.setString(4, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public User findById(int userId) throws SQLException {
        String[] roleHolder = new String[1];
        String tableName = getTableNameAndRole(userId, roleHolder);
        String idCol = getIdColumn(tableName);
        String sql = "SELECT '" + roleHolder[0] + "' AS Role, " + idCol + " AS UserID, Username, Password, Email, Address, CreatedDate, LastLoginDate " +
                     "FROM " + tableName + " WHERE " + idCol + " = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public User create(User user) throws SQLException {
        String role = user.getRole() != null ? user.getRole() : "customer";
        String tableName;
        if ("admin".equals(role)) tableName = "Admin";
        else if ("restaurant_owner".equals(role)) tableName = "RestaurantOwner";
        else if ("delivery_partner".equals(role)) tableName = "DeliveryPartner";
        else tableName = "Customer";

        String sql = "INSERT INTO " + tableName + " (Username, Password, Email, Address, CreatedDate) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getAddress());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) user.setUserID(keys.getInt(1));
            }
        }
        return user;
    }

    public boolean update(User user) throws SQLException {
        String tableName = getTableNameAndRole(user.getUserID(), null);
        String idCol = getIdColumn(tableName);
        String sql = "UPDATE " + tableName + " SET Username = ?, Email = ?, Address = ? WHERE " + idCol + " = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getAddress());
            ps.setInt(4, user.getUserID());
            return ps.executeUpdate() > 0;
        }
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT 'customer' AS Role, CustomerID AS UserID, Username, Password, Email, Address, CreatedDate, LastLoginDate FROM Customer " +
                     "UNION ALL " +
                     "SELECT 'restaurant_owner' AS Role, OwnerID AS UserID, Username, Password, Email, Address, CreatedDate, LastLoginDate FROM RestaurantOwner " +
                     "UNION ALL " +
                     "SELECT 'delivery_partner' AS Role, PartnerID AS UserID, Username, Password, Email, Address, CreatedDate, LastLoginDate FROM DeliveryPartner " +
                     "UNION ALL " +
                     "SELECT 'admin' AS Role, AdminID AS UserID, Username, Password, Email, Address, CreatedDate, LastLoginDate FROM Admin " +
                     "ORDER BY UserID";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(mapRow(rs));
        }
        return users;
    }

    public boolean updateRole(int userId, String role) throws SQLException {
        User user = findById(userId);
        if (user == null) return false;
        if (user.getRole().equals(role)) return true;

        // Delete from current table
        delete(userId);

        // Insert into new table
        user.setRole(role);
        create(user);
        return true;
    }

    public boolean updateLastLogin(int userId) throws SQLException {
        String tableName = getTableNameAndRole(userId, null);
        String idCol = getIdColumn(tableName);
        String sql = "UPDATE " + tableName + " SET LastLoginDate = ? WHERE " + idCol + " = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int userId) throws SQLException {
        String tableName = getTableNameAndRole(userId, null);
        String idCol = getIdColumn(tableName);
        String sql = "DELETE FROM " + tableName + " WHERE " + idCol + " = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }
}
