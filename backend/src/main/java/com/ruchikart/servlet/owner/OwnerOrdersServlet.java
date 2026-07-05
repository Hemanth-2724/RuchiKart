package com.ruchikart.servlet.owner;

import com.google.gson.JsonObject;
import com.ruchikart.dao.OrderDAO;
import com.ruchikart.model.Order;
import com.ruchikart.model.User;
import com.ruchikart.util.JsonUtil;
import com.ruchikart.util.OwnerRestaurantMapping;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/owner/orders/*")
public class OwnerOrdersServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    private int getRestaurantId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return 1;
        Object rid = session.getAttribute("restaurantId");
        if (rid != null) return (int) rid;
        User user = (User) session.getAttribute("user");
        if (user != null) return OwnerRestaurantMapping.getRestaurantId(user.getUserID());
        return 1;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int restaurantId = getRestaurantId(request);
            List<Order> orders = orderDAO.findByRestaurantId(restaurantId);
            for (Order order : orders) {
                order.setItems(orderDAO.findItemsByOrderId(order.getOrderID()));
            }
            JsonUtil.sendJson(response, orders);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to fetch orders: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();
            int orderId = 0;
            if (pathInfo != null && !pathInfo.equals("/")) {
                String[] parts = pathInfo.split("/");
                if (parts.length >= 2) {
                    orderId = Integer.parseInt(parts[1]);
                }
            }

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);

            JsonObject body = JsonUtil.getGson().fromJson(sb.toString(), JsonObject.class);

            if (orderId == 0 && body.has("orderId")) {
                orderId = body.get("orderId").getAsInt();
            }
            String status = body.has("status") ? body.get("status").getAsString() : null;

            if (orderId == 0 || status == null) {
                JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Order ID and status are required.");
                return;
            }

            boolean updated = orderDAO.updateStatus(orderId, status);
            if (updated) {
                JsonUtil.sendSuccess(response, "Order status updated to: " + status);
            } else {
                JsonUtil.sendError(response, HttpServletResponse.SC_NOT_FOUND, "Order not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to update order status: " + e.getMessage());
        }
    }
}
