package com.ruchikart.servlet.delivery;

import com.google.gson.JsonObject;
import com.ruchikart.dao.OrderDAO;
import com.ruchikart.model.Order;
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
import java.util.List;

@WebServlet("/api/delivery/orders")
public class DeliveryOrdersServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                JsonUtil.sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized.");
                return;
            }
            User user = (User) session.getAttribute("user");
            int partnerId = user.getUserID();

            List<Order> orders = orderDAO.findPendingDeliveryOrders(partnerId);
            for (Order order : orders) {
                order.setItems(orderDAO.findItemsByOrderId(order.getOrderID()));
            }
            JsonUtil.sendJson(response, orders);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to fetch delivery orders: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                JsonUtil.sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized.");
                return;
            }
            User user = (User) session.getAttribute("user");
            int partnerId = user.getUserID();

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);

            JsonObject body = JsonUtil.getGson().fromJson(sb.toString(), JsonObject.class);
            int orderId = body.has("orderId") ? body.get("orderId").getAsInt() : 0;
            String status = body.has("status") ? body.get("status").getAsString() : null;

            if (orderId == 0 || status == null) {
                JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Order ID and status are required.");
                return;
            }

            // Delivery partner can only set these two statuses
            if (!status.equals("out_for_delivery") && !status.equals("delivered")) {
                JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Status must be 'out_for_delivery' or 'delivered'.");
                return;
            }

            if (status.equals("out_for_delivery")) {
                // Accepting order
                boolean accepted = orderDAO.assignDeliveryPartnerAndStatus(orderId, partnerId, status);
                if (accepted) {
                    JsonUtil.sendSuccess(response, "Order accepted and marked out for delivery!");
                } else {
                    JsonUtil.sendError(response, HttpServletResponse.SC_CONFLICT,
                            "Failed to accept order. It may have already been accepted by another partner.");
                }
            } else {
                // Mark as delivered
                Order order = orderDAO.findById(orderId);
                if (order == null) {
                    JsonUtil.sendError(response, HttpServletResponse.SC_NOT_FOUND, "Order not found.");
                    return;
                }
                if (order.getDeliveryPartnerID() != null && order.getDeliveryPartnerID() != partnerId) {
                    JsonUtil.sendError(response, HttpServletResponse.SC_FORBIDDEN,
                            "You are not authorized to update this order.");
                    return;
                }
                boolean updated = orderDAO.updateStatus(orderId, status);
                if (updated) {
                    JsonUtil.sendSuccess(response, "Order successfully delivered!");
                } else {
                    JsonUtil.sendError(response, HttpServletResponse.SC_NOT_FOUND, "Order not found.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to update delivery status: " + e.getMessage());
        }
    }
}
