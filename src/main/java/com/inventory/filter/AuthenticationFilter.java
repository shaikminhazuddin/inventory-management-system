package com.inventory.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter that protects secured administrative resources from unauthorized access.
 * Checks for a valid "admin" session attribute and enforces browser cache controls.
 */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // Whitelisted public resources
        boolean isPublicResource = path.equals("/login") ||
                                   path.equals("/login.jsp") ||
                                   path.equals("/index.jsp") ||
                                   path.equals("/") ||
                                   path.isEmpty() ||
                                   path.startsWith("/assets/") ||
                                   path.equals("/favicon.ico");

        HttpSession session = httpRequest.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("admin") != null);

        if (isLoggedIn || isPublicResource) {
            // Prevent back-button viewing of secured pages after logout
            if (!isPublicResource) {
                httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                httpResponse.setHeader("Pragma", "no-cache");
                httpResponse.setDateHeader("Expires", 0);
            }
            chain.doFilter(request, response);
        } else {
            // Redirect unauthenticated requests to login page
            httpResponse.sendRedirect(contextPath + "/login.jsp?msg=auth_required");
        }
    }

    @Override
    public void destroy() {}
}

