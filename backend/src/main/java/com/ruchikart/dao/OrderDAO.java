package com.ruchikart.dao;

import com.ruchikart.model.Order;
import com.ruchikart.model.OrderItem;
import com.ruchikart.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    private Order mapOrderRow(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderID(rs.getInt("OrderID"));
        order.setUserID(rs.getInt("UserID"));
        Timestamp orderDate = rs.getTimestamp("OrderDate");
        if (orderDate != null) order.setOrderDate(orderDate.toLocalDateTime());
        order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        order.setStatus(rs.getString("Status"));
        order.setPaymentMethod(rs.getString("PaymentMethod"));
        order.setRestaurantID(rs.getInt("RestaurantID"));
        int partnerId = rs.getInt("DeliveryPartnerID");
        if (!rs.wasNull()) {
            order.setDeliveryPartnerID(partnerId);
        }
        // Optional join columns
        try { order.setRestaurantName(rs.getString("RestaurantName")); } catch (SQLException ignored) {}
        try { order.setCustomerUsername(rs.getString("CustomerUsername")); } catch (SQLException ignored) {}
        return order;
    }

    private OrderItem mapOrderItemRow(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();
        item.setOrderItemID(rs.getInt("OrderItemID"));
        item.setOrderID(rs.getInt("OrderID"));
        item.setMenuID(rs.getInt("MenuID"));
        item.setQuantity(rs.getInt("Quantity"));
        item.setItemTotal(rs.getBigDecimal("ItemTotal"));
        try { item.setItemName(rs.getString("ItemName")); } catch (SQLException ignored) {}
        return item;
    }

    public Order create(Order order) throws SQLException {
        String sql = "INSERT INTO OrderTable (UserID, OrderDate, TotalAmount, Status, PaymentMethod, RestaurantID) VALUES (?, NOW(), ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getUserID());
            ps.setBigDecimal(2, order.getTotalAmount());
            ps.setString(3, order.getStatus() != null ? order.getStatus() : "pending");
            ps.setString(4, order.getPaymentMethod());
            ps.setInt(5, order.getRestaurantID());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) order.setOrderID(keys.getInt(1));
            }
        }
        return order;
    }

    public void addOrderItems(List<OrderItem> items) throws SQLException {
        String sql = "INSERT INTO OrderItem (OrderID, MenuID, Quantity, ItemTotal) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (OrderItem item : items) {
                ps.setInt(1, item.getOrderID());
                ps.setInt(2, item.getMenuID());
                ps.setInt(3, item.getQuantity());
                ps.setBigDecimal(4, item.getItemTotal());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public List<Order> findByUserId(int userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, r.Name AS RestaurantName FROM OrderTable o " +
                     "LEFT JOIN Restaurant r ON o.RestaurantID = r.RestaurantID " +
                     "WHERE o.UserID = ? ORDER BY o.OrderDate DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) orders.add(mapOrderRow(rs));
            }
        }
        return orders;
    }

    public List<Order> findByRestaurantId(int restaurantId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.Username AS CustomerUsername FROM OrderTable o " +
                     "LEFT JOIN Customer u ON o.UserID = u.CustomerID " +
                     "WHERE o.RestaurantID = ? ORDER BY o.OrderDate DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) orders.add(mapOrderRow(rs));
            }
        }
        return orders;
    }

    public List<Order> findPendingDeliveryOrders(int partnerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, r.Name AS RestaurantName, u.Username AS CustomerUsername " +
                     "FROM OrderTable o " +
                     "LEFT JOIN Restaurant r ON o.RestaurantID = r.RestaurantID " +
                     "LEFT JOIN Customer u ON o.UserID = u.CustomerID " +
                     "WHERE (o.Status IN ('ready', 'ready_for_pickup') AND o.DeliveryPartnerID IS NULL) " +
                     "   OR (o.DeliveryPartnerID = ?) " +
                     "ORDER BY o.OrderDate DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, partnerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) orders.add(mapOrderRow(rs));
            }
        }
        return orders;
    }

    public boolean assignDeliveryPartnerAndStatus(int orderId, int partnerId, String status) throws SQLException {
        String sql = "UPDATE OrderTable SET DeliveryPartnerID = ?, Status = ? " +
                     "WHERE OrderID = ? AND (DeliveryPartnerID IS NULL OR DeliveryPartnerID = ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, partnerId);
            ps.setString(2, status);
            ps.setInt(3, orderId);
            ps.setInt(4, partnerId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Order> findAll() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, r.Name AS RestaurantName, u.Username AS CustomerUsername " +
                     "FROM OrderTable o " +
                     "LEFT JOIN Restaurant r ON o.RestaurantID = r.RestaurantID " +
                     "LEFT JOIN Customer u ON o.UserID = u.CustomerID " +
                     "ORDER BY o.OrderDate DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) orders.add(mapOrderRow(rs));
        }
        return orders;
    }

    public boolean updateStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE OrderTable SET Status = ? WHERE OrderID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    public Order findById(int orderId) throws SQLException {
        String sql = "SELECT o.*, r.Name AS RestaurantName, u.Username AS CustomerUsername " +
                     "FROM OrderTable o " +
                     "LEFT JOIN Restaurant r ON o.RestaurantID = r.RestaurantID " +
                     "LEFT JOIN Customer u ON o.UserID = u.CustomerID " +
                     "WHERE o.OrderID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapOrderRow(rs);
            }
        }
        return null;
    }

    public List<OrderItem> findItemsByOrderId(int orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, m.ItemName FROM OrderItem oi " +
                     "LEFT JOIN Menu m ON oi.MenuID = m.MenuID " +
                     "WHERE oi.OrderID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) items.add(mapOrderItemRow(rs));
            }
        }
        return items;
    }

    // Admin stats helpers
    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM OrderTable";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public double totalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(TotalAmount), 0) FROM OrderTable WHERE Status = 'delivered'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    public int countPendingByRestaurantId(int restaurantId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM OrderTable WHERE RestaurantID = ? AND Status IN ('pending','preparing')";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public double revenueByRestaurantId(int restaurantId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(TotalAmount), 0) FROM OrderTable WHERE RestaurantID = ? AND Status = 'delivered'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        return 0;
    }

    public int countByRestaurantId(int restaurantId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM OrderTable WHERE RestaurantID = ?";
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
