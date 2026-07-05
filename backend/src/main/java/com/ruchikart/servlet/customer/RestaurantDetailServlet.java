package com.ruchikart.servlet.customer;

import com.google.gson.JsonObject;
import com.ruchikart.dao.MenuDAO;
import com.ruchikart.dao.RestaurantDAO;
import com.ruchikart.model.Menu;
import com.ruchikart.model.Restaurant;
import com.ruchikart.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/restaurants/*")
public class RestaurantDetailServlet extends HttpServlet {

    private final RestaurantDAO restaurantDAO = new RestaurantDAO();
    private final MenuDAO menuDAO = new MenuDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Restaurant ID is required.");
            return;
        }

        try {
            int restaurantId = Integer.parseInt(pathInfo.substring(1));
            Restaurant restaurant = restaurantDAO.findById(restaurantId);

            if (restaurant == null) {
                JsonUtil.sendError(response, HttpServletResponse.SC_NOT_FOUND,
                        "Restaurant not found.");
                return;
            }

            List<Menu> menuItems = menuDAO.findAvailableByRestaurantId(restaurantId);

            JsonObject result = new JsonObject();
            result.add("restaurant", JsonUtil.getGson().toJsonTree(restaurant));
            result.add("menu", JsonUtil.getGson().toJsonTree(menuItems));

            JsonUtil.sendJson(response, result);

        } catch (NumberFormatException e) {
            JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid restaurant ID format.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to fetch restaurant details: " + e.getMessage());
        }
    }
}
