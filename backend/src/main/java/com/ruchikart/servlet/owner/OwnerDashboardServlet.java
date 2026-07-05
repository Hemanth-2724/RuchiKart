package com.ruchikart.servlet.owner;

import com.google.gson.JsonObject;
import com.ruchikart.dao.MenuDAO;
import com.ruchikart.dao.OrderDAO;
import com.ruchikart.dao.RestaurantDAO;
import com.ruchikart.model.Restaurant;
import com.ruchikart.model.User;
import com.ruchikart.util.JsonUtil;
import com.ruchikart.util.OwnerRestaurantMapping;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/api/owner/dashboard")
public class OwnerDashboardServlet extends HttpServlet {

    private final RestaurantDAO restaurantDAO = new RestaurantDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final MenuDAO menuDAO = new MenuDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            JsonUtil.sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Not authenticated.");
            return;
        }

        try {
            int restaurantId = OwnerRestaurantMapping.getRestaurantId(user.getUserID());

            // Also check session override
            Object sessionRestId = session.getAttribute("restaurantId");
            if (sessionRestId != null) {
                restaurantId = (int) sessionRestId;
            }

            Restaurant restaurant = restaurantDAO.findById(restaurantId);

            int totalOrders = orderDAO.countByRestaurantId(restaurantId);
            int pendingOrders = orderDAO.countPendingByRestaurantId(restaurantId);
            double totalRevenue = orderDAO.revenueByRestaurantId(restaurantId);
            int totalMenuItems = menuDAO.countByRestaurantId(restaurantId);

            JsonObject stats = new JsonObject();
            stats.addProperty("restaurantId", restaurantId);
            stats.addProperty("restaurantName", restaurant != null ? restaurant.getName() : "Unknown");
            stats.addProperty("totalOrders", totalOrders);
            stats.addProperty("pendingOrders", pendingOrders);
            stats.addProperty("totalRevenue", totalRevenue);
            stats.addProperty("totalMenuItems", totalMenuItems);

            JsonUtil.sendJson(response, stats);

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to load dashboard: " + e.getMessage());
        }
    }
}
