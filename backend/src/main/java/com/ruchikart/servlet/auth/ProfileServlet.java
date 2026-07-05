package com.ruchikart.servlet.auth;

import com.google.gson.JsonObject;
import com.ruchikart.dao.UserDAO;
import com.ruchikart.model.User;
import com.ruchikart.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/auth/profile")
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            JsonUtil.sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Not authenticated.");
            return;
        }

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            JsonUtil.sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Not authenticated.");
            return;
        }

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JsonObject body = JsonUtil.getGson().fromJson(sb.toString(), JsonObject.class);

            String username = body.has("username") ? body.get("username").getAsString() : sessionUser.getUsername();
            String email = body.has("email") ? body.get("email").getAsString() : sessionUser.getEmail();
            String address = body.has("address") ? body.get("address").getAsString() : sessionUser.getAddress();

            if (username == null || username.trim().isEmpty()) {
                JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Username cannot be empty.");
                return;
            }
            if (email == null || email.trim().isEmpty()) {
                JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Email cannot be empty.");
                return;
            }

            // Check duplicate username if changed
            if (!username.equalsIgnoreCase(sessionUser.getUsername())) {
                if (userDAO.findByUsername(username) != null) {
                    JsonUtil.sendError(response, HttpServletResponse.SC_CONFLICT, "Username already taken.");
                    return;
                }
            }

            // Check duplicate email if changed
            if (!email.equalsIgnoreCase(sessionUser.getEmail())) {
                if (userDAO.findByEmail(email) != null) {
                    JsonUtil.sendError(response, HttpServletResponse.SC_CONFLICT, "Email already registered.");
                    return;
                }
            }

            sessionUser.setUsername(username);
            sessionUser.setEmail(email);
            sessionUser.setAddress(address);

            boolean success = userDAO.update(sessionUser);
            if (success) {
                session.setAttribute("user", sessionUser);
                JsonObject responseBody = new JsonObject();
                responseBody.addProperty("message", "Profile updated successfully.");
                JsonUtil.sendJson(response, responseBody);
            } else {
                JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update profile database records.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update profile: " + e.getMessage());
        }
    }
}
