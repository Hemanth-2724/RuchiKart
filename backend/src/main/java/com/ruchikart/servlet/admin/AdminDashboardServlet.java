package com.ruchikart.servlet.admin;

import com.google.gson.JsonObject;
import com.ruchikart.dao.OrderDAO;
import com.ruchikart.dao.RestaurantDAO;
import com.ruchikart.dao.UserDAO;
import com.ruchikart.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final RestaurantDAO restaurantDAO = new RestaurantDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int totalUsers = userDAO.findAll().size();
            int totalRestaurants = restaurantDAO.findAllForAdmin().size();
            int totalOrders = orderDAO.countAll();
            double totalRevenue = orderDAO.totalRevenue();

            JsonObject stats = new JsonObject();
            stats.addProperty("totalUsers", totalUsers);
            stats.addProperty("totalRestaurants", totalRestaurants);
            stats.addProperty("totalOrders", totalOrders);
            stats.addProperty("totalRevenue", totalRevenue);

            JsonUtil.sendJson(response, stats);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to load admin dashboard: " + e.getMessage());
        }
    }
}
