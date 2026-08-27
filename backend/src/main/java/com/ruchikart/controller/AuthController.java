package com.ruchikart.controller;

import com.ruchikart.model.User;
import com.ruchikart.service.UserService;
import com.ruchikart.util.OwnerRestaurantMapping;
import com.ruchikart.util.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required."));
        }

        User user = userService.findByEmail(email);
        if (user == null || !PasswordUtil.checkPassword(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password."));
        }

        userService.updateLastLogin(user.getUserID());

        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(3600);
        user.setPassword(null);
        session.setAttribute("user", user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("userID", user.getUserID());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("address", user.getAddress());

        if ("restaurant_owner".equals(user.getRole())) {
            int restaurantId = OwnerRestaurantMapping.getRestaurantId(user.getUserID());
            session.setAttribute("restaurantId", restaurantId);
            response.put("restaurantId", restaurantId);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String email = body.get("email");
        String address = body.getOrDefault("address", "");
        String role = body.getOrDefault("role", "customer");

        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is required."));
        }
        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password is required."));
        }
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required."));
        }

        if (userService.findByUsername(username) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Username already taken."));
        }
        if (userService.findByEmail(email) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email already registered."));
        }

        if (!role.equals("customer") && !role.equals("restaurant_owner") && !role.equals("delivery_partner")) {
            role = "customer";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setEmail(email);
        user.setAddress(address);
        user.setRole(role);

        User created = userService.create(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration successful");
        response.put("userID", created.getUserID());
        response.put("username", created.getUsername());
        response.put("email", created.getEmail());
        response.put("role", created.getRole());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated."));
        }

        User user = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();
        response.put("userID", user.getUserID());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("address", user.getAddress());

        if ("restaurant_owner".equals(user.getRole())) {
            response.put("restaurantId", OwnerRestaurantMapping.getRestaurantId(user.getUserID()));
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> body, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated."));
        }

        User sessionUser = (User) session.getAttribute("user");
        String username = body.getOrDefault("username", sessionUser.getUsername());
        String email = body.getOrDefault("email", sessionUser.getEmail());
        String address = body.getOrDefault("address", sessionUser.getAddress());

        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username cannot be empty."));
        }
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email cannot be empty."));
        }

        if (!username.equalsIgnoreCase(sessionUser.getUsername())) {
            if (userService.findByUsername(username) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Username already taken."));
            }
        }
        if (!email.equalsIgnoreCase(sessionUser.getEmail())) {
            if (userService.findByEmail(email) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email already registered."));
            }
        }

        sessionUser.setUsername(username);
        sessionUser.setEmail(email);
        sessionUser.setAddress(address);

        boolean success = userService.update(sessionUser);
        if (success) {
            session.setAttribute("user", sessionUser);
            return ResponseEntity.ok(Map.of("message", "Profile updated successfully."));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to update profile database records."));
        }
    }
}
