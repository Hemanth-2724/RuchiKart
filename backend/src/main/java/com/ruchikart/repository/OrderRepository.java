package com.ruchikart.repository;

import com.ruchikart.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
    List<OrderEntity> findByUserIdOrderByOrderIdDesc(Integer userId);
    List<OrderEntity> findByRestaurantIdOrderByOrderIdDesc(Integer restaurantId);
    
    @Query("FROM OrderEntity WHERE status = 'out_for_delivery' AND deliveryPartnerId = :partnerId ORDER BY orderId DESC")
    List<OrderEntity> findPendingDeliveryOrders(@Param("partnerId") Integer partnerId);

    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.restaurantId = :restaurantId AND o.status = 'pending'")
    int countPendingByRestaurantId(@Param("restaurantId") Integer restaurantId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM OrderEntity o WHERE o.restaurantId = :restaurantId AND o.status = 'delivered'")
    BigDecimal revenueByRestaurantId(@Param("restaurantId") Integer restaurantId);

    int countByRestaurantId(Integer restaurantId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM OrderEntity o WHERE o.status = 'delivered'")
    BigDecimal totalRevenue();
}
