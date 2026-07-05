package com.ruchikart.servlet.customer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ruchikart.dao.MenuDAO;
import com.ruchikart.dao.OrderDAO;
import com.ruchikart.model.Menu;
import com.ruchikart.model.Order;
import com.ruchikart.model.OrderItem;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/orders/*")
public class PlaceOrderServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();
    private final MenuDAO menuDAO = new MenuDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            JsonUtil.sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Not authenticated.");
            return;
        }

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);

            JsonObject body = JsonUtil.getGson().fromJson(sb.toString(), JsonObject.class);

            int restaurantId = body.has("restaurantId") ? body.get("restaurantId").getAsInt() : 0;
            String paymentMethod = body.has("paymentMethod") ? body.get("paymentMethod").getAsString() : "cash_on_delivery";
            JsonArray itemsArray = body.has("items") ? body.getAsJsonArray("items") : new JsonArray();

            if (restaurantId == 0 || itemsArray.size() == 0) {
                JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Restaurant ID and order items are required.");
                return;
            }

            // Calculate total and build order items
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<OrderItem> orderItems = new ArrayList<>();

            for (JsonElement element : itemsArray) {
                JsonObject itemJson = element.getAsJsonObject();
                int menuId = itemJson.get("menuId").getAsInt();
                int quantity = itemJson.get("quantity").getAsInt();

                Menu menu = menuDAO.findById(menuId);
                if (menu == null || !menu.isAvailable()) {
                    JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                            "Menu item " + menuId + " not available.");
                    return;
                }

                BigDecimal itemTotal = menu.getPrice().multiply(BigDecimal.valueOf(quantity));
                totalAmount = totalAmount.add(itemTotal);

                OrderItem orderItem = new OrderItem();
                orderItem.setMenuID(menuId);
                orderItem.setQuantity(quantity);
                orderItem.setItemTotal(itemTotal);
                orderItems.add(orderItem);
            }

            // Create order
            Order order = new Order();
            order.setUserID(user.getUserID());
            order.setRestaurantID(restaurantId);
            order.setTotalAmount(totalAmount);
            order.setPaymentMethod(paymentMethod);
            order.setStatus("pending");

            Order created = orderDAO.create(order);

            // Set orderID on all items
            for (OrderItem item : orderItems) {
                item.setOrderID(created.getOrderID());
            }
            orderDAO.addOrderItems(orderItems);

            JsonObject result = new JsonObject();
            result.addProperty("message", "Order placed successfully!");
            result.addProperty("orderID", created.getOrderID());
            result.addProperty("totalAmount", totalAmount);
            result.addProperty("status", "pending");

            response.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.sendJson(response, result);

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to place order: " + e.getMessage());
        }
    }

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
            List<Order> orders = orderDAO.findByUserId(user.getUserID());
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

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            JsonUtil.sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Not authenticated.");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Order ID required.");
            return;
        }

        try {
            // Path is /{id}/cancel
            String[] parts = pathInfo.split("/");
            if (parts.length < 2) {
                JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid path parameters.");
                return;
            }

            int orderId = Integer.parseInt(parts[1]);
            Order order = orderDAO.findById(orderId);

            if (order == null) {
                JsonUtil.sendError(response, HttpServletResponse.SC_NOT_FOUND, "Order not found.");
                return;
            }

            if (order.getUserID() != user.getUserID()) {
                JsonUtil.sendError(response, HttpServletResponse.SC_FORBIDDEN, "Not authorized to cancel this order.");
                return;
            }

            if (!"pending".equals(order.getStatus())) {
                JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Only pending orders can be cancelled.");
                return;
            }

            boolean updated = orderDAO.updateStatus(orderId, "cancelled");
            if (updated) {
                JsonUtil.sendSuccess(response, "Order cancelled successfully.");
            } else {
                JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to cancel order.");
            }

        } catch (NumberFormatException e) {
            JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID format.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: " + e.getMessage());
        }
    }
}
