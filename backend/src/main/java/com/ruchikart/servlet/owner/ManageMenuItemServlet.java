package com.ruchikart.servlet.owner;

import com.google.gson.JsonObject;
import com.ruchikart.dao.MenuDAO;
import com.ruchikart.model.Menu;
import com.ruchikart.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/owner/menu/*")
public class ManageMenuItemServlet extends HttpServlet {

    private final MenuDAO menuDAO = new MenuDAO();

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Menu ID required.");
            return;
        }

        try {
            int menuId = Integer.parseInt(pathInfo.substring(1));

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);

            JsonObject body = JsonUtil.getGson().fromJson(sb.toString(), JsonObject.class);

            Menu existing = menuDAO.findById(menuId);
            if (existing == null) {
                JsonUtil.sendError(response, HttpServletResponse.SC_NOT_FOUND, "Menu item not found.");
                return;
            }

            if (body.has("itemName")) existing.setItemName(body.get("itemName").getAsString());
            if (body.has("description")) existing.setDescription(body.get("description").getAsString());
            if (body.has("price")) existing.setPrice(body.get("price").getAsBigDecimal());
            if (body.has("isAvailable")) existing.setAvailable(body.get("isAvailable").getAsBoolean());
            if (body.has("imagePath")) existing.setImagePath(body.get("imagePath").getAsString());
            if (body.has("isVeg")) existing.setVeg(body.get("isVeg").getAsBoolean());

            boolean updated = menuDAO.update(existing);
            if (updated) {
                JsonUtil.sendJson(response, existing);
            } else {
                JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Failed to update menu item.");
            }

        } catch (NumberFormatException e) {
            JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid menu ID.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to update menu item: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Menu ID required.");
            return;
        }

        try {
            int menuId = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = menuDAO.delete(menuId);
            if (deleted) {
                JsonUtil.sendSuccess(response, "Menu item deleted successfully.");
            } else {
                JsonUtil.sendError(response, HttpServletResponse.SC_NOT_FOUND, "Menu item not found.");
            }
        } catch (NumberFormatException e) {
            JsonUtil.sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid menu ID.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to delete menu item: " + e.getMessage());
        }
    }
}
