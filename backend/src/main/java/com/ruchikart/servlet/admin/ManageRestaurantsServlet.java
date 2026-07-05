package com.ruchikart.servlet.admin;

import com.ruchikart.dao.RestaurantDAO;
import com.ruchikart.model.Restaurant;
import com.ruchikart.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/admin/restaurants")
public class ManageRestaurantsServlet extends HttpServlet {

    private final RestaurantDAO restaurantDAO = new RestaurantDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Restaurant> restaurants = restaurantDAO.findAllForAdmin();
            JsonUtil.sendJson(response, restaurants);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to fetch restaurants: " + e.getMessage());
        }
    }
}
