package com.ruchikart.controller;

import com.ruchikart.model.Order;
import com.ruchikart.model.User;
import com.ruchikart.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    public ResponseEntity<?> getDeliveryOrders(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized."));
        }
        User user = (User) session.getAttribute("user");
        int partnerId = user.getUserID();

        List<Order> orders = orderService.findPendingDeliveryOrders(partnerId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/orders")
    public ResponseEntity<?> updateDeliveryStatus(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized."));
        }
        User user = (User) session.getAttribute("user");
        int partnerId = user.getUserID();

        int orderId = body.containsKey("orderId") ? ((Number) body.get("orderId")).intValue() : 0;
        String status = (String) body.get("status");

        if (orderId == 0 || status == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Order ID and status are required."));
        }

        if (!status.equals("out_for_delivery") && !status.equals("delivered")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status must be 'out_for_delivery' or 'delivered'."));
        }

        if (status.equals("out_for_delivery")) {
            boolean accepted = orderService.assignDeliveryPartnerAndStatus(orderId, partnerId, status);
            if (accepted) {
                return ResponseEntity.ok(Map.of("message", "Order accepted and marked out for delivery!", "success", true));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Failed to accept order. It may have already been accepted by another partner."));
            }
        } else {
            Order order = orderService.findById(orderId);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Order not found."));
            }
            if (order.getDeliveryPartnerID() != null && order.getDeliveryPartnerID() != partnerId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "You are not authorized to update this order."));
            }
            boolean updated = orderService.updateStatus(orderId, status);
            if (updated) {
                return ResponseEntity.ok(Map.of("message", "Order successfully delivered!", "success", true));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Order not found."));
            }
        }
    }
}
