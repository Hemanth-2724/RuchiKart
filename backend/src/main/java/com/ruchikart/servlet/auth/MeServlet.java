package com.ruchikart.servlet.auth;

import com.google.gson.JsonObject;
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

@WebServlet("/api/auth/me")
public class MeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            JsonUtil.sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Not authenticated.");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            JsonUtil.sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Not authenticated.");
            return;
        }

        JsonObject responseBody = new JsonObject();
        responseBody.addProperty("userID", user.getUserID());
        responseBody.addProperty("username", user.getUsername());
        responseBody.addProperty("email", user.getEmail());
        responseBody.addProperty("role", user.getRole());
        responseBody.addProperty("address", user.getAddress());

        if ("restaurant_owner".equals(user.getRole())) {
            responseBody.addProperty("restaurantId",
                    OwnerRestaurantMapping.getRestaurantId(user.getUserID()));
        }

        JsonUtil.sendJson(response, responseBody);
    }
}
