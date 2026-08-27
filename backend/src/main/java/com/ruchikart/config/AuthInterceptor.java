package com.ruchikart.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruchikart.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized. Please log in.");
            return false;
        }

        String path = request.getRequestURI();

        if (path.contains("/api/owner/") && !"restaurant_owner".equals(user.getRole())) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied. Restaurant owner role required.");
            return false;
        }

        if (path.contains("/api/delivery/") && !"delivery_partner".equals(user.getRole())) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied. Delivery partner role required.");
            return false;
        }

        if (path.contains("/api/admin/") && !"admin".equals(user.getRole())) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin role required.");
            return false;
        }

        return true;
    }

    private void sendError(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, String> err = new HashMap<>();
        err.put("error", message);
        err.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(err));
    }
}
