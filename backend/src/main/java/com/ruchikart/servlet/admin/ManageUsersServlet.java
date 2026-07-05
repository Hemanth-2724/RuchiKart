package com.ruchikart.servlet.admin;

import com.google.gson.JsonObject;
import com.ruchikart.dao.UserDAO;
import com.ruchikart.model.User;
import com.ruchikart.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/admin/users")
public class ManageUsersServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<User> users = userDAO.findAll();
            // Remove passwords from response
            users.forEach(u -> u.setPassword(null));
            JsonUtil.sendJson(response, users);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to fetch users: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);

            JsonObject body = JsonUtil.getGson().fromJson(sb.toString(), JsonObject.class);
            int userId = body.has("userId") ? body.get("userId").getAsInt() : 0;
            String role = body.has("role") ? body.get("role").getAsString() : null;

            if (userId == 0 || role == null) {
                JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "User ID and role are required.");
                return;
            }

            boolean updated = userDAO.updateRole(userId, role);
            if (updated) {
                JsonUtil.sendSuccess(response, "User role updated successfully.");
            } else {
                JsonUtil.sendError(response, HttpServletResponse.SC_NOT_FOUND, "User not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to update user: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String userIdParam = request.getParameter("userId");
            if (userIdParam == null) {
                JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "User ID required.");
                return;
            }
            int userId = Integer.parseInt(userIdParam);
            boolean deleted = userDAO.delete(userId);
            if (deleted) {
                JsonUtil.sendSuccess(response, "User deleted successfully.");
            } else {
                JsonUtil.sendError(response, HttpServletResponse.SC_NOT_FOUND, "User not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to delete user: " + e.getMessage());
        }
    }
}
