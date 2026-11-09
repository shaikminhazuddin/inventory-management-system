package com.inventory.servlet;

import com.inventory.dao.AdminDAO;
import com.inventory.model.Admin;
import com.inventory.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Handles Administrator authentication: login validation, session creation, and logout.
 */
@WebServlet(urlPatterns = {"/login", "/logout"})
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private AdminDAO adminDAO;

    @Override
    public void init() throws ServletException {
        this.adminDAO = new AdminDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/logout".equals(path)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate(); // Destroy session
            }
            response.sendRedirect(request.getContextPath() + "/login.jsp?msg=logged_out");
            return;
        }

        // If already logged in, redirect straight to dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("admin") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Username and password cannot be empty.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            Admin admin = adminDAO.findByUsername(username.trim());

            // If admin account doesn't exist yet in the database, automatically provision default admin
            if (admin == null && "admin".equalsIgnoreCase(username.trim()) && "admin123".equals(password.trim())) {
                admin = adminDAO.createDefaultAdminIfNotExists();
                if (admin == null) {
                    admin = new Admin(1, "admin", "$2a$10$Peotn0oBQb3yHuF.eDoeR.k79/Bd30iem66SL64aqMJjrxwZjfqbq",
                            "System Administrator", "admin@warehouse.local", null);
                }
            }

            if (admin != null && PasswordUtil.checkPassword(password, admin.getPasswordHash())) {
                // Authentication successful
                HttpSession session = request.getSession(true);
                session.setAttribute("admin", admin);
                session.setMaxInactiveInterval(30 * 60); // 30 minutes session timeout

                response.sendRedirect(request.getContextPath() + "/dashboard");
            } else {
                request.setAttribute("errorMessage", "Invalid username or password.");
                request.setAttribute("enteredUsername", username);
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database connection error: " + e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}

