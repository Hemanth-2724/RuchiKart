package com.ruchikart.controller;

import com.ruchikart.model.Menu;
import com.ruchikart.model.Order;
import com.ruchikart.model.Restaurant;
import com.ruchikart.model.User;
import com.ruchikart.service.MenuService;
import com.ruchikart.service.OrderService;
import com.ruchikart.service.RestaurantService;
import com.ruchikart.util.OwnerRestaurantMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/owner")
public class OwnerController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MenuService menuService;

    private int getRestaurantId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return 1;
        Object rid = session.getAttribute("restaurantId");
        if (rid != null) return (int) rid;
        User user = (User) session.getAttribute("user");
        if (user != null) return OwnerRestaurantMapping.getRestaurantId(user.getUserID());
        return 1;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated."));
        }

        int restaurantId = OwnerRestaurantMapping.getRestaurantId(user.getUserID());
        Object sessionRestId = session.getAttribute("restaurantId");
        if (sessionRestId != null) {
            restaurantId = (int) sessionRestId;
        }

        Restaurant restaurant = restaurantService.findById(restaurantId);

        int totalOrders = orderService.countByRestaurantId(restaurantId);
        int pendingOrders = orderService.countPendingByRestaurantId(restaurantId);
        double totalRevenue = orderService.revenueByRestaurantId(restaurantId);
        int totalMenuItems = menuService.countByRestaurantId(restaurantId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("restaurantId", restaurantId);
        stats.put("restaurantName", restaurant != null ? restaurant.getName() : "Unknown");
        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalMenuItems", totalMenuItems);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getOwnerOrders(HttpServletRequest request) {
        int restaurantId = getRestaurantId(request);
        List<Order> orders = orderService.findByRestaurantId(restaurantId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping(value = {"/orders/{id}", "/orders"})
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable(value = "id", required = false) Integer pathOrderId,
            @RequestBody Map<String, Object> body) {

        int orderId = (pathOrderId != null && pathOrderId != 0) ? pathOrderId :
                (body.containsKey("orderId") ? ((Number) body.get("orderId")).intValue() : 0);

        String status = (String) body.get("status");

        if (orderId == 0 || status == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Order ID and status are required."));
        }

        boolean updated = orderService.updateStatus(orderId, status);
        if (updated) {
            return ResponseEntity.ok(Map.of("message", "Order status updated to: " + status, "success", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Order not found."));
        }
    }

    @GetMapping("/menu")
    public ResponseEntity<?> getOwnerMenu(HttpServletRequest request) {
        int restaurantId = getRestaurantId(request);
        List<Menu> menuItems = menuService.findByRestaurantId(restaurantId);
        return ResponseEntity.ok(menuItems);
    }

    @PostMapping("/menu")
    public ResponseEntity<?> createMenuItem(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        int restaurantId = getRestaurantId(request);

        String itemName = (String) body.get("itemName");
        if (itemName == null || itemName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Item name is required."));
        }

        Menu menu = new Menu();
        menu.setRestaurantID(restaurantId);
        menu.setItemName(itemName);
        menu.setDescription((String) body.getOrDefault("description", ""));
        menu.setPrice(body.containsKey("price") ? new BigDecimal(body.get("price").toString()) : BigDecimal.ZERO);
        menu.setAvailable(body.containsKey("isAvailable") ? (Boolean) body.get("isAvailable") : true);
        menu.setImagePath((String) body.getOrDefault("imagePath", ""));
        menu.setVeg(body.containsKey("isVeg") ? (Boolean) body.get("isVeg") : true);

        Menu created = menuService.create(menu);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/menu/{id}")
    public ResponseEntity<?> updateMenuItem(@PathVariable("id") int id, @RequestBody Map<String, Object> body) {
        Menu existing = menuService.findById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Menu item not found."));
        }

        if (body.containsKey("itemName")) existing.setItemName((String) body.get("itemName"));
        if (body.containsKey("description")) existing.setDescription((String) body.get("description"));
        if (body.containsKey("price")) existing.setPrice(new BigDecimal(body.get("price").toString()));
        if (body.containsKey("isAvailable")) existing.setAvailable((Boolean) body.get("isAvailable"));
        if (body.containsKey("imagePath")) existing.setImagePath((String) body.get("imagePath"));
        if (body.containsKey("isVeg")) existing.setVeg((Boolean) body.get("isVeg"));

        boolean updated = menuService.update(existing);
        if (updated) {
            return ResponseEntity.ok(existing);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to update menu item."));
        }
    }

    @DeleteMapping("/menu/{id}")
    public ResponseEntity<?> deleteMenuItem(@PathVariable("id") int id) {
        boolean deleted = menuService.delete(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Menu item deleted successfully.", "success", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Menu item not found."));
        }
    }
}
