package com.ruchikart.service;

import com.ruchikart.dao.OrderDAO;
import com.ruchikart.model.Order;
import com.ruchikart.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderDAO orderDAO;

    public Order create(Order order) {
        return orderDAO.create(order);
    }

    public void addOrderItems(List<OrderItem> items) {
        orderDAO.addOrderItems(items);
    }

    public List<Order> findByUserId(int userId) {
        List<Order> orders = orderDAO.findByUserId(userId);
        for (Order o : orders) {
            o.setItems(orderDAO.findItemsByOrderId(o.getOrderID()));
        }
        return orders;
    }

    public List<Order> findByRestaurantId(int restaurantId) {
        List<Order> orders = orderDAO.findByRestaurantId(restaurantId);
        for (Order o : orders) {
            o.setItems(orderDAO.findItemsByOrderId(o.getOrderID()));
        }
        return orders;
    }

    public List<Order> findPendingDeliveryOrders(int partnerId) {
        List<Order> orders = orderDAO.findPendingDeliveryOrders(partnerId);
        for (Order o : orders) {
            o.setItems(orderDAO.findItemsByOrderId(o.getOrderID()));
        }
        return orders;
    }

    public boolean assignDeliveryPartnerAndStatus(int orderId, int partnerId, String status) {
        return orderDAO.assignDeliveryPartnerAndStatus(orderId, partnerId, status);
    }

    public List<Order> findAll() {
        return orderDAO.findAll();
    }

    public boolean updateStatus(int orderId, String status) {
        return orderDAO.updateStatus(orderId, status);
    }

    public Order findById(int orderId) {
        Order order = orderDAO.findById(orderId);
        if (order != null) {
            order.setItems(orderDAO.findItemsByOrderId(orderId));
        }
        return order;
    }

    public List<OrderItem> findItemsByOrderId(int orderId) {
        return orderDAO.findItemsByOrderId(orderId);
    }

    public int countAll() {
        return orderDAO.countAll();
    }

    public double totalRevenue() {
        return orderDAO.totalRevenue();
    }

    public int countPendingByRestaurantId(int restaurantId) {
        return orderDAO.countPendingByRestaurantId(restaurantId);
    }

    public double revenueByRestaurantId(int restaurantId) {
        return orderDAO.revenueByRestaurantId(restaurantId);
    }

    public int countByRestaurantId(int restaurantId) {
        return orderDAO.countByRestaurantId(restaurantId);
    }
}
