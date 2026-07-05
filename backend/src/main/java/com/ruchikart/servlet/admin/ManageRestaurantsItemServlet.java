package com.ruchikart.servlet.admin;

import com.google.gson.JsonObject;
import com.ruchikart.dao.RestaurantDAO;
import com.ruchikart.model.Restaurant;
import com.ruchikart.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/admin/restaurants/*")
public class ManageRestaurantsItemServlet extends HttpServlet {

    private final RestaurantDAO restaurantDAO = new RestaurantDAO();

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Restaurant ID required.");
            return;
        }

        try {
            int restaurantId = Integer.parseInt(pathInfo.substring(1));

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);

            JsonObject body = JsonUtil.getGson().fromJson(sb.toString(), JsonObject.class);

            Restaurant existing = restaurantDAO.findById(restaurantId);
            if (existing == null) {
                JsonUtil.sendError(response, HttpServletResponse.SC_NOT_FOUND, "Restaurant not found.");
                return;
            }

            // Toggle active or update fields
            if (body.has("isActive")) {
                boolean isActive = body.get("isActive").getAsBoolean();
                restaurantDAO.toggleActive(restaurantId, isActive);
                existing.setActive(isActive);
            }
            if (body.has("name")) existing.setName(body.get("name").getAsString());
            if (body.has("cuisineType")) existing.setCuisineType(body.get("cuisineType").getAsString());
            if (body.has("deliveryTime")) existing.setDeliveryTime(body.get("deliveryTime").getAsInt());
            if (body.has("address")) existing.setAddress(body.get("address").getAsString());
            if (body.has("rating")) existing.setRating(body.get("rating").getAsBigDecimal());

            restaurantDAO.update(existing);

            JsonUtil.sendJson(response, existing);

        } catch (NumberFormatException e) {
            JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid restaurant ID.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to update restaurant: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Restaurant ID required.");
            return;
        }

        try {
            int restaurantId = Integer.parseInt(pathInfo.substring(1));
            // Just deactivate rather than hard delete
            boolean updated = restaurantDAO.toggleActive(restaurantId, false);
            if (updated) {
                JsonUtil.sendSuccess(response, "Restaurant deactivated successfully.");
            } else {
                JsonUtil.sendError(response, HttpServletResponse.SC_NOT_FOUND, "Restaurant not found.");
            }
        } catch (NumberFormatException e) {
            JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid restaurant ID.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to deactivate restaurant: " + e.getMessage());
        }
    }
}
