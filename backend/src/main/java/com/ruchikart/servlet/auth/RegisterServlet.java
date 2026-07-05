package com.ruchikart.servlet.auth;

import com.google.gson.JsonObject;
import com.ruchikart.dao.UserDAO;
import com.ruchikart.model.User;
import com.ruchikart.util.JsonUtil;
import com.ruchikart.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/auth/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);

            JsonObject body = JsonUtil.getGson().fromJson(sb.toString(), JsonObject.class);

            String username = body.has("username") ? body.get("username").getAsString() : null;
            String password = body.has("password") ? body.get("password").getAsString() : null;
            String email = body.has("email") ? body.get("email").getAsString() : null;
            String address = body.has("address") ? body.get("address").getAsString() : "";
            String role = body.has("role") ? body.get("role").getAsString() : "customer";

            // Validation
            if (username == null || username.isEmpty()) {
                JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Username is required.");
                return;
            }
            if (password == null || password.isEmpty()) {
                JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Password is required.");
                return;
            }
            if (email == null || email.isEmpty()) {
                JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Email is required.");
                return;
            }

            // Check if username or email already taken
            if (userDAO.findByUsername(username) != null) {
                JsonUtil.sendError(response, HttpServletResponse.SC_CONFLICT, "Username already taken.");
                return;
            }
            if (userDAO.findByEmail(email) != null) {
                JsonUtil.sendError(response, HttpServletResponse.SC_CONFLICT, "Email already registered.");
                return;
            }

            // Only allow customer and restaurant_owner roles for self-registration
            if (!role.equals("customer") && !role.equals("restaurant_owner") && !role.equals("delivery_partner")) {
                role = "customer";
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(PasswordUtil.hashPassword(password));
            user.setEmail(email);
            user.setAddress(address);
            user.setRole(role);

            User created = userDAO.create(user);

            JsonObject responseBody = new JsonObject();
            responseBody.addProperty("message", "Registration successful");
            responseBody.addProperty("userID", created.getUserID());
            responseBody.addProperty("username", created.getUsername());
            responseBody.addProperty("email", created.getEmail());
            responseBody.addProperty("role", created.getRole());

            response.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.sendJson(response, responseBody);

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Registration failed: " + e.getMessage());
        }
    }
}
