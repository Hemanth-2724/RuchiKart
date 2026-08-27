package com.ruchikart.controller;

import com.ruchikart.model.Restaurant;
import com.ruchikart.model.User;
import com.ruchikart.service.OrderService;
import com.ruchikart.service.RestaurantService;
import com.ruchikart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        int totalUsers = userService.findAll().size();
        int totalRestaurants = restaurantService.findAllForAdmin().size();
        int totalOrders = orderService.countAll();
        double totalRevenue = orderService.totalRevenue();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalRestaurants", totalRestaurants);
        stats.put("totalOrders", totalOrders);
        stats.put("totalRevenue", totalRevenue);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.findAll();
        users.forEach(u -> u.setPassword(null));
        return ResponseEntity.ok(users);
    }

    @PutMapping(value = {"/users/{id}", "/users"})
    public ResponseEntity<?> updateUserRole(
            @PathVariable(value = "id", required = false) Integer pathUserId,
            @RequestBody Map<String, Object> body) {

        int userId = (pathUserId != null && pathUserId != 0) ? pathUserId :
                (body.containsKey("userId") ? ((Number) body.get("userId")).intValue() : 0);
        String role = (String) body.get("role");

        if (userId == 0 || role == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID and role are required."));
        }

        boolean updated = userService.updateRole(userId, role);
        if (updated) {
            return ResponseEntity.ok(Map.of("message", "User role updated successfully to: " + role, "success", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found."));
        }
    }

    @DeleteMapping(value = {"/users/{id}", "/users"})
    public ResponseEntity<?> deleteUser(
            @PathVariable(value = "id", required = false) Integer pathUserId,
            @RequestParam(value = "userId", required = false) Integer paramUserId) {

        int userId = (pathUserId != null && pathUserId != 0) ? pathUserId :
                (paramUserId != null ? paramUserId : 0);

        if (userId == 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required."));
        }

        boolean deleted = userService.delete(userId);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "User deleted successfully.", "success", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found."));
        }
    }

    @GetMapping("/restaurants")
    public ResponseEntity<?> getAllRestaurantsAdmin() {
        List<Restaurant> restaurants = restaurantService.findAllForAdmin();
        return ResponseEntity.ok(restaurants);
    }

    @PutMapping("/restaurants/{id}")
    public ResponseEntity<?> updateRestaurant(@PathVariable("id") int id, @RequestBody Map<String, Object> body) {
        Restaurant existing = restaurantService.findById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Restaurant not found."));
        }

        if (body.containsKey("isActive")) {
            boolean isActive = (Boolean) body.get("isActive");
            restaurantService.toggleActive(id, isActive);
            existing.setActive(isActive);
        }

        if (body.containsKey("name")) existing.setName((String) body.get("name"));
        if (body.containsKey("cuisineType")) existing.setCuisineType((String) body.get("cuisineType"));
        if (body.containsKey("deliveryTime")) existing.setDeliveryTime(((Number) body.get("deliveryTime")).intValue());
        if (body.containsKey("address")) existing.setAddress((String) body.get("address"));
        if (body.containsKey("rating")) existing.setRating(new BigDecimal(body.get("rating").toString()));

        restaurantService.update(existing);

        return ResponseEntity.ok(existing);
    }

    @DeleteMapping("/restaurants/{id}")
    public ResponseEntity<?> deactivateRestaurant(@PathVariable("id") int id) {
        boolean updated = restaurantService.toggleActive(id, false);
        if (updated) {
            return ResponseEntity.ok(Map.of("message", "Restaurant deactivated successfully.", "success", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Restaurant not found."));
        }
    }
}
