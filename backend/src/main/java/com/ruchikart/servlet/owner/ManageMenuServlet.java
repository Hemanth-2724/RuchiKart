package com.ruchikart.servlet.owner;

import com.google.gson.JsonObject;
import com.ruchikart.dao.MenuDAO;
import com.ruchikart.model.Menu;
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
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/api/owner/menu")
public class ManageMenuServlet extends HttpServlet {

    private final MenuDAO menuDAO = new MenuDAO();

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
            List<Menu> menuItems = menuDAO.findByRestaurantId(restaurantId);
            JsonUtil.sendJson(response, menuItems);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to fetch menu: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int restaurantId = getRestaurantId(request);

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);

            JsonObject body = JsonUtil.getGson().fromJson(sb.toString(), JsonObject.class);

            String itemName = body.has("itemName") ? body.get("itemName").getAsString() : null;
            if (itemName == null || itemName.isEmpty()) {
                JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Item name is required.");
                return;
            }

            Menu menu = new Menu();
            menu.setRestaurantID(restaurantId);
            menu.setItemName(itemName);
            menu.setDescription(body.has("description") ? body.get("description").getAsString() : "");
            menu.setPrice(body.has("price") ? body.get("price").getAsBigDecimal() : BigDecimal.ZERO);
            menu.setAvailable(body.has("isAvailable") ? body.get("isAvailable").getAsBoolean() : true);
            menu.setImagePath(body.has("imagePath") ? body.get("imagePath").getAsString() : "");
            menu.setVeg(body.has("isVeg") ? body.get("isVeg").getAsBoolean() : true);

            Menu created = menuDAO.create(menu);
            response.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.sendJson(response, created);

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to create menu item: " + e.getMessage());
        }
    }
}
