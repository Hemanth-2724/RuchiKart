package com.ruchikart.servlet.admin;

import com.google.gson.JsonObject;
import com.ruchikart.dao.UserDAO;
import com.ruchikart.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/admin/users/*")
public class ManageUsersItemServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "User ID required.");
            return;
        }

        try {
            int userId = Integer.parseInt(pathInfo.substring(1));

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);

            JsonObject body = JsonUtil.getGson().fromJson(sb.toString(), JsonObject.class);
            String role = body.has("role") ? body.get("role").getAsString() : null;

            if (role == null) {
                JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Role is required.");
                return;
            }

            boolean updated = userDAO.updateRole(userId, role);
            if (updated) {
                JsonUtil.sendSuccess(response, "User role updated to: " + role);
            } else {
                JsonUtil.sendError(response, HttpServletResponse.SC_NOT_FOUND, "User not found.");
            }
        } catch (NumberFormatException e) {
            JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to update user: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "User ID required.");
            return;
        }

        try {
            int userId = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = userDAO.delete(userId);
            if (deleted) {
                JsonUtil.sendSuccess(response, "User deleted successfully.");
            } else {
                JsonUtil.sendError(response, HttpServletResponse.SC_NOT_FOUND, "User not found.");
            }
        } catch (NumberFormatException e) {
            JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to delete user: " + e.getMessage());
        }
    }
}
