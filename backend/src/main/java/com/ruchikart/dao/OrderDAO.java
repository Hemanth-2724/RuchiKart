package com.ruchikart.dao;

import com.ruchikart.entity.*;
import com.ruchikart.model.Order;
import com.ruchikart.model.OrderItem;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    private Order mapEntity(OrderEntity e) {
        if (e == null) return null;
        Order o = new Order();
        o.setOrderID(e.getOrderId());
        o.setUserID(e.getUserId());
        o.setOrderDate(e.getOrderDate());
        o.setTotalAmount(e.getTotalAmount());
        o.setStatus(e.getStatus());
        o.setPaymentMethod(e.getPaymentMethod());
        o.setRestaurantID(e.getRestaurantId());
        o.setDeliveryPartnerID(e.getDeliveryPartnerId());
        return o;
    }

    public Order create(Order order) {
        Session session = getSession();
        OrderEntity entity = new OrderEntity();
        entity.setUserId(order.getUserID());
        entity.setOrderDate(LocalDateTime.now());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setStatus(order.getStatus() != null ? order.getStatus() : "pending");
        entity.setPaymentMethod(order.getPaymentMethod());
        entity.setRestaurantId(order.getRestaurantID());

        session.persist(entity);
        order.setOrderID(entity.getOrderId());
        order.setOrderDate(entity.getOrderDate());
        return order;
    }

    public void addOrderItems(List<OrderItem> items) {
        Session session = getSession();
        for (OrderItem item : items) {
            OrderItemEntity entity = new OrderItemEntity();
            entity.setOrderId(item.getOrderID());
            entity.setMenuId(item.getMenuID());
            entity.setQuantity(item.getQuantity());
            entity.setItemTotal(item.getItemTotal());
            session.persist(entity);
        }
    }

    public List<Order> findByUserId(int userId) {
        Session session = getSession();
        List<Object[]> rows = session.createQuery(
                "SELECT o, r.name FROM OrderEntity o LEFT JOIN RestaurantEntity r ON o.restaurantId = r.restaurantId " +
                "WHERE o.userId = :uId ORDER BY o.orderDate DESC", Object[].class
        ).setParameter("uId", userId).getResultList();

        List<Order> list = new ArrayList<>();
        for (Object[] row : rows) {
            OrderEntity oe = (OrderEntity) row[0];
            String rName = (String) row[1];
            Order o = mapEntity(oe);
            o.setRestaurantName(rName);
            list.add(o);
        }
        return list;
    }

    public List<Order> findByRestaurantId(int restaurantId) {
        Session session = getSession();
        List<Object[]> rows = session.createQuery(
                "SELECT o, c.username FROM OrderEntity o LEFT JOIN CustomerEntity c ON o.userId = c.customerId " +
                "WHERE o.restaurantId = :rId ORDER BY o.orderDate DESC", Object[].class
        ).setParameter("rId", restaurantId).getResultList();

        List<Order> list = new ArrayList<>();
        for (Object[] row : rows) {
            OrderEntity oe = (OrderEntity) row[0];
            String cUsername = (String) row[1];
            Order o = mapEntity(oe);
            o.setCustomerUsername(cUsername);
            list.add(o);
        }
        return list;
    }

    public List<Order> findPendingDeliveryOrders(int partnerId) {
        Session session = getSession();
        List<Object[]> rows = session.createQuery(
                "SELECT o, r.name, c.username FROM OrderEntity o " +
                "LEFT JOIN RestaurantEntity r ON o.restaurantId = r.restaurantId " +
                "LEFT JOIN CustomerEntity c ON o.userId = c.customerId " +
                "WHERE (o.status IN ('ready', 'ready_for_pickup') AND o.deliveryPartnerId IS NULL) " +
                "   OR (o.deliveryPartnerId = :pId) " +
                "ORDER BY o.orderDate DESC", Object[].class
        ).setParameter("pId", partnerId).getResultList();

        List<Order> list = new ArrayList<>();
        for (Object[] row : rows) {
            OrderEntity oe = (OrderEntity) row[0];
            String rName = (String) row[1];
            String cUsername = (String) row[2];
            Order o = mapEntity(oe);
            o.setRestaurantName(rName);
            o.setCustomerUsername(cUsername);
            list.add(o);
        }
        return list;
    }

    public boolean assignDeliveryPartnerAndStatus(int orderId, int partnerId, String status) {
        Session session = getSession();
        OrderEntity entity = session.get(OrderEntity.class, orderId);
        if (entity == null) return false;

        if (entity.getDeliveryPartnerId() == null || entity.getDeliveryPartnerId().equals(partnerId)) {
            entity.setDeliveryPartnerId(partnerId);
            entity.setStatus(status);
            session.merge(entity);
            return true;
        }
        return false;
    }

    public List<Order> findAll() {
        Session session = getSession();
        List<Object[]> rows = session.createQuery(
                "SELECT o, r.name, c.username FROM OrderEntity o " +
                "LEFT JOIN RestaurantEntity r ON o.restaurantId = r.restaurantId " +
                "LEFT JOIN CustomerEntity c ON o.userId = c.customerId " +
                "ORDER BY o.orderDate DESC", Object[].class
        ).getResultList();

        List<Order> list = new ArrayList<>();
        for (Object[] row : rows) {
            OrderEntity oe = (OrderEntity) row[0];
            String rName = (String) row[1];
            String cUsername = (String) row[2];
            Order o = mapEntity(oe);
            o.setRestaurantName(rName);
            o.setCustomerUsername(cUsername);
            list.add(o);
        }
        return list;
    }

    public boolean updateStatus(int orderId, String status) {
        Session session = getSession();
        OrderEntity entity = session.get(OrderEntity.class, orderId);
        if (entity == null) return false;

        entity.setStatus(status);
        session.merge(entity);
        return true;
    }

    public Order findById(int orderId) {
        Session session = getSession();
        List<Object[]> rows = session.createQuery(
                "SELECT o, r.name, c.username FROM OrderEntity o " +
                "LEFT JOIN RestaurantEntity r ON o.restaurantId = r.restaurantId " +
                "LEFT JOIN CustomerEntity c ON o.userId = c.customerId " +
                "WHERE o.orderId = :oId", Object[].class
        ).setParameter("oId", orderId).getResultList();

        if (rows.isEmpty()) return null;
        Object[] row = rows.get(0);
        OrderEntity oe = (OrderEntity) row[0];
        String rName = (String) row[1];
        String cUsername = (String) row[2];
        Order o = mapEntity(oe);
        o.setRestaurantName(rName);
        o.setCustomerUsername(cUsername);
        return o;
    }

    public List<OrderItem> findItemsByOrderId(int orderId) {
        Session session = getSession();
        List<Object[]> rows = session.createQuery(
                "SELECT oi, m.itemName FROM OrderItemEntity oi " +
                "LEFT JOIN MenuEntity m ON oi.menuId = m.menuId " +
                "WHERE oi.orderId = :oId", Object[].class
        ).setParameter("oId", orderId).getResultList();

        List<OrderItem> list = new ArrayList<>();
        for (Object[] row : rows) {
            OrderItemEntity entity = (OrderItemEntity) row[0];
            String itemName = (String) row[1];
            OrderItem item = new OrderItem();
            item.setOrderItemID(entity.getOrderItemId());
            item.setOrderID(entity.getOrderId());
            item.setMenuID(entity.getMenuId());
            item.setQuantity(entity.getQuantity());
            item.setItemTotal(entity.getItemTotal());
            item.setItemName(itemName);
            list.add(item);
        }
        return list;
    }

    public int countAll() {
        Session session = getSession();
        Long count = session.createQuery("SELECT COUNT(o) FROM OrderEntity o", Long.class).getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    public double totalRevenue() {
        Session session = getSession();
        BigDecimal total = session.createQuery(
                "SELECT SUM(o.totalAmount) FROM OrderEntity o WHERE o.status = 'delivered'", BigDecimal.class
        ).getSingleResult();
        return total != null ? total.doubleValue() : 0.0;
    }

    public int countPendingByRestaurantId(int restaurantId) {
        Session session = getSession();
        Long count = session.createQuery(
                "SELECT COUNT(o) FROM OrderEntity o WHERE o.restaurantId = :rId AND o.status IN ('pending', 'preparing')", Long.class
        ).setParameter("rId", restaurantId).getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    public double revenueByRestaurantId(int restaurantId) {
        Session session = getSession();
        BigDecimal total = session.createQuery(
                "SELECT SUM(o.totalAmount) FROM OrderEntity o WHERE o.restaurantId = :rId AND o.status = 'delivered'", BigDecimal.class
        ).setParameter("rId", restaurantId).getSingleResult();
        return total != null ? total.doubleValue() : 0.0;
    }

    public int countByRestaurantId(int restaurantId) {
        Session session = getSession();
        Long count = session.createQuery(
                "SELECT COUNT(o) FROM OrderEntity o WHERE o.restaurantId = :rId", Long.class
        ).setParameter("rId", restaurantId).getSingleResult();
        return count != null ? count.intValue() : 0;
    }
}
