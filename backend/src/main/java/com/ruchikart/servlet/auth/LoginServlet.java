package com.ruchikart.servlet.auth;

import com.google.gson.JsonObject;
import com.ruchikart.dao.UserDAO;
import com.ruchikart.model.User;
import com.ruchikart.util.JsonUtil;
import com.ruchikart.util.OwnerRestaurantMapping;
import com.ruchikart.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/auth/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Parse JSON body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);

            JsonObject body = JsonUtil.getGson().fromJson(sb.toString(), JsonObject.class);
            String email = body.has("email") ? body.get("email").getAsString() : null;
            String password = body.has("password") ? body.get("password").getAsString() : null;

            if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Email and password are required.");
                return;
            }

            User user = userDAO.findByEmail(email);
            if (user == null || !PasswordUtil.checkPassword(password, user.getPassword())) {
                JsonUtil.sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid email or password.");
                return;
            }

            // Update last login
            userDAO.updateLastLogin(user.getUserID());

            // Create session
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(3600); // 1 hour

            // Don't store password in session
            user.setPassword(null);
            session.setAttribute("user", user);

            // Add restaurantId for owner
            if ("restaurant_owner".equals(user.getRole())) {
                int restaurantId = OwnerRestaurantMapping.getRestaurantId(user.getUserID());
                session.setAttribute("restaurantId", restaurantId);
            }

            // Build response object (without password)
            JsonObject responseBody = new JsonObject();
            responseBody.addProperty("message", "Login successful");
            responseBody.addProperty("userID", user.getUserID());
            responseBody.addProperty("username", user.getUsername());
            responseBody.addProperty("email", user.getEmail());
            responseBody.addProperty("role", user.getRole());
            responseBody.addProperty("address", user.getAddress());

            if ("restaurant_owner".equals(user.getRole())) {
                responseBody.addProperty("restaurantId",
                        OwnerRestaurantMapping.getRestaurantId(user.getUserID()));
            }

            JsonUtil.sendJson(response, responseBody);

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Login failed: " + e.getMessage());
        }
    }
}
