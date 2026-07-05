package com.ruchikart.filter;

import com.ruchikart.model.User;
import com.ruchikart.util.JsonUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {
        "/api/restaurants/*",
        "/api/orders/*",
        "/api/owner/*",
        "/api/delivery/*",
        "/api/admin/*"
})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Skip OPTIONS preflight - handled by CORSFilter
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            JsonUtil.sendError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized. Please log in.");
            return;
        }

        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = uri.substring(contextPath.length());

        // Role-based access control
        if (path.startsWith("/api/owner/") && !"restaurant_owner".equals(user.getRole())) {
            JsonUtil.sendError(httpResponse, HttpServletResponse.SC_FORBIDDEN,
                    "Access denied. Restaurant owner role required.");
            return;
        }

        if (path.startsWith("/api/delivery/") && !"delivery_partner".equals(user.getRole())) {
            JsonUtil.sendError(httpResponse, HttpServletResponse.SC_FORBIDDEN,
                    "Access denied. Delivery partner role required.");
            return;
        }

        if (path.startsWith("/api/admin/") && !"admin".equals(user.getRole())) {
            JsonUtil.sendError(httpResponse, HttpServletResponse.SC_FORBIDDEN,
                    "Access denied. Admin role required.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
