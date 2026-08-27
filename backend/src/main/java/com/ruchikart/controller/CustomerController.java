package com.ruchikart.controller;

import com.ruchikart.model.Menu;
import com.ruchikart.model.Order;
import com.ruchikart.model.OrderItem;
import com.ruchikart.model.Restaurant;
import com.ruchikart.model.User;
import com.ruchikart.service.MenuService;
import com.ruchikart.service.OrderService;
import com.ruchikart.service.RestaurantService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api")
public class CustomerController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/restaurants")
    public ResponseEntity<?> getAllRestaurants() {
        List<Restaurant> list = restaurantService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/restaurants/{id}")
    public ResponseEntity<?> getRestaurantDetail(@PathVariable("id") int id) {
        Restaurant restaurant = restaurantService.findById(id);
        if (restaurant == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Restaurant not found."));
        }

        List<Menu> menuItems = menuService.findAvailableByRestaurantId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("restaurant", restaurant);
        result.put("menu", menuItems);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/orders")
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated."));
        }

        int restaurantId = body.containsKey("restaurantId") ? ((Number) body.get("restaurantId")).intValue() : 0;
        String paymentMethod = (String) body.getOrDefault("paymentMethod", "cash_on_delivery");
        List<Map<String, Object>> itemsList = (List<Map<String, Object>>) body.get("items");

        if (restaurantId == 0 || itemsList == null || itemsList.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Restaurant ID and order items are required."));
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Map<String, Object> itemMap : itemsList) {
            int menuId = ((Number) itemMap.get("menuId")).intValue();
            int quantity = ((Number) itemMap.get("quantity")).intValue();

            Menu menu = menuService.findById(menuId);
            if (menu == null || !menu.isAvailable()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Menu item " + menuId + " not available."));
            }

            BigDecimal itemTotal = menu.getPrice().multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuID(menuId);
            orderItem.setQuantity(quantity);
            orderItem.setItemTotal(itemTotal);
            orderItems.add(orderItem);
        }

        Order order = new Order();
        order.setUserID(user.getUserID());
        order.setRestaurantID(restaurantId);
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("pending");

        Order created = orderService.create(order);

        for (OrderItem item : orderItems) {
            item.setOrderID(created.getOrderID());
        }
        orderService.addOrderItems(orderItems);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Order placed successfully!");
        result.put("orderID", created.getOrderID());
        result.put("totalAmount", totalAmount);
        result.put("status", "pending");

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getCustomerOrders(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated."));
        }

        List<Order> orders = orderService.findByUserId(user.getUserID());
        return ResponseEntity.ok(orders);
    }

    @PutMapping(value = {"/orders/{id}/cancel", "/orders/{id}"})
    public ResponseEntity<?> cancelOrder(@PathVariable("id") int id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated."));
        }

        Order order = orderService.findById(id);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Order not found."));
        }

        if (order.getUserID() != user.getUserID()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Not authorized to cancel this order."));
        }

        if (!"pending".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only pending orders can be cancelled."));
        }

        boolean updated = orderService.updateStatus(id, "cancelled");
        if (updated) {
            return ResponseEntity.ok(Map.of("message", "Order cancelled successfully.", "success", true));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to cancel order."));
        }
    }
}
