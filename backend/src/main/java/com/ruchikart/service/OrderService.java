package com.ruchikart.service;

import com.ruchikart.entity.OrderEntity;
import com.ruchikart.entity.OrderItemEntity;
import com.ruchikart.model.Order;
import com.ruchikart.model.OrderItem;
import com.ruchikart.repository.OrderItemRepository;
import com.ruchikart.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private Order mapOrderEntity(OrderEntity e) {
        if (e == null) return null;
        Order o = new Order();
        o.setOrderID(e.getOrderId());
        o.setUserID(e.getUserId());
        o.setOrderDate(e.getOrderDate());
        o.setTotalAmount(e.getTotalAmount());
        o.setStatus(e.getStatus());
        o.setPaymentMethod(e.getPaymentMethod());
        o.setRestaurantID(e.getRestaurantId());
        o.setDeliveryPartnerID(e.getDeliveryPartnerId() != null ? e.getDeliveryPartnerId() : 0);
        return o;
    }

    private OrderItem mapItemEntity(OrderItemEntity e) {
        if (e == null) return null;
        OrderItem item = new OrderItem();
        item.setOrderItemID(e.getOrderItemId());
        item.setOrderID(e.getOrderId());
        item.setMenuID(e.getMenuId());
        item.setQuantity(e.getQuantity());
        item.setItemTotal(e.getItemTotal());
        return item;
    }

    public Order create(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setUserId(order.getUserID());
        entity.setOrderDate(order.getOrderDate() != null ? order.getOrderDate() : LocalDateTime.now());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setStatus(order.getStatus() != null ? order.getStatus() : "pending");
        entity.setPaymentMethod(order.getPaymentMethod());
        entity.setRestaurantId(order.getRestaurantID());
        entity.setDeliveryPartnerId(order.getDeliveryPartnerID() != null && order.getDeliveryPartnerID() > 0 ? order.getDeliveryPartnerID() : null);
        entity = orderRepository.save(entity);
        order.setOrderID(entity.getOrderId());
        return order;
    }

    public void addOrderItems(List<OrderItem> items) {
        if (items == null) return;
        List<OrderItemEntity> entities = new ArrayList<>();
        for (OrderItem item : items) {
            OrderItemEntity entity = new OrderItemEntity();
            entity.setOrderId(item.getOrderID());
            entity.setMenuId(item.getMenuID());
            entity.setQuantity(item.getQuantity());
            entity.setItemTotal(item.getItemTotal());
            entities.add(entity);
        }
        orderItemRepository.saveAll(entities);
    }

    public List<Order> findByUserId(int userId) {
        List<OrderEntity> entities = orderRepository.findByUserIdOrderByOrderIdDesc(userId);
        List<Order> orders = new ArrayList<>();
        for (OrderEntity e : entities) {
            Order o = mapOrderEntity(e);
            o.setItems(findItemsByOrderId(o.getOrderID()));
            orders.add(o);
        }
        return orders;
    }

    public List<Order> findByRestaurantId(int restaurantId) {
        List<OrderEntity> entities = orderRepository.findByRestaurantIdOrderByOrderIdDesc(restaurantId);
        List<Order> orders = new ArrayList<>();
        for (OrderEntity e : entities) {
            Order o = mapOrderEntity(e);
            o.setItems(findItemsByOrderId(o.getOrderID()));
            orders.add(o);
        }
        return orders;
    }

    public List<Order> findPendingDeliveryOrders(int partnerId) {
        List<OrderEntity> entities = orderRepository.findPendingDeliveryOrders(partnerId);
        List<Order> orders = new ArrayList<>();
        for (OrderEntity e : entities) {
            Order o = mapOrderEntity(e);
            o.setItems(findItemsByOrderId(o.getOrderID()));
            orders.add(o);
        }
        return orders;
    }

    public boolean assignDeliveryPartnerAndStatus(int orderId, int partnerId, String status) {
        Optional<OrderEntity> opt = orderRepository.findById(orderId);
        if (opt.isEmpty()) return false;
        OrderEntity entity = opt.get();
        entity.setDeliveryPartnerId(partnerId);
        entity.setStatus(status);
        orderRepository.save(entity);
        return true;
    }

    public List<Order> findAll() {
        List<OrderEntity> entities = orderRepository.findAll();
        List<Order> orders = new ArrayList<>();
        for (OrderEntity e : entities) {
            Order o = mapOrderEntity(e);
            o.setItems(findItemsByOrderId(o.getOrderID()));
            orders.add(o);
        }
        return orders;
    }

    public boolean updateStatus(int orderId, String status) {
        Optional<OrderEntity> opt = orderRepository.findById(orderId);
        if (opt.isEmpty()) return false;
        OrderEntity entity = opt.get();
        entity.setStatus(status);
        orderRepository.save(entity);
        return true;
    }

    public Order findById(int orderId) {
        Optional<OrderEntity> opt = orderRepository.findById(orderId);
        if (opt.isEmpty()) return null;
        Order order = mapOrderEntity(opt.get());
        order.setItems(findItemsByOrderId(orderId));
        return order;
    }

    public List<OrderItem> findItemsByOrderId(int orderId) {
        List<OrderItemEntity> entities = orderItemRepository.findByOrderId(orderId);
        List<OrderItem> items = new ArrayList<>();
        entities.forEach(e -> items.add(mapItemEntity(e)));
        return items;
    }

    public int countAll() {
        return (int) orderRepository.count();
    }

    public double totalRevenue() {
        BigDecimal revenue = orderRepository.totalRevenue();
        return revenue != null ? revenue.doubleValue() : 0.0;
    }

    public int countPendingByRestaurantId(int restaurantId) {
        return orderRepository.countPendingByRestaurantId(restaurantId);
    }

    public double revenueByRestaurantId(int restaurantId) {
        BigDecimal revenue = orderRepository.revenueByRestaurantId(restaurantId);
        return revenue != null ? revenue.doubleValue() : 0.0;
    }

    public int countByRestaurantId(int restaurantId) {
        return orderRepository.countByRestaurantId(restaurantId);
    }
}
