package com.inventory.servlet;

import com.inventory.dao.CategoryDAO;
import com.inventory.model.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller for Category Management: View, Add, Edit, and Delete.
 */
@WebServlet(urlPatterns = {"/categories"})
public class CategoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        this.categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("delete".equalsIgnoreCase(action)) {
                handleDelete(request, response);
                return;
            }

            List<Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/WEB-INF/views/categories.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading categories: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/categories.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("add".equalsIgnoreCase(action)) {
                handleAdd(request, response);
            } else if ("edit".equalsIgnoreCase(action)) {
                handleEdit(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/categories");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid category ID specified.");
            response.sendRedirect(request.getContextPath() + "/categories");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Category operation failed: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/categories");
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        String name = request.getParameter("categoryName");
        String description = request.getParameter("description");

        if (name == null || name.trim().isEmpty()) {
            request.getSession().setAttribute("errorMessage", "Category name cannot be empty.");
            response.sendRedirect(request.getContextPath() + "/categories");
            return;
        }

        if (categoryDAO.isCategoryNameExists(name, 0)) {
            request.getSession().setAttribute("errorMessage", "Category '" + name + "' already exists!");
            response.sendRedirect(request.getContextPath() + "/categories");
            return;
        }

        Category c = new Category(0, name, description);
        boolean success = categoryDAO.insertCategory(c);

        if (success) {
            request.getSession().setAttribute("successMessage", "Category '" + name + "' created successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to create category.");
        }
        response.sendRedirect(request.getContextPath() + "/categories");
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int id = Integer.parseInt(request.getParameter("categoryId"));
        String name = request.getParameter("categoryName");
        String description = request.getParameter("description");

        if (name == null || name.trim().isEmpty()) {
            request.getSession().setAttribute("errorMessage", "Category name cannot be empty.");
            response.sendRedirect(request.getContextPath() + "/categories");
            return;
        }

        if (categoryDAO.isCategoryNameExists(name, id)) {
            request.getSession().setAttribute("errorMessage", "Category '" + name + "' already exists!");
            response.sendRedirect(request.getContextPath() + "/categories");
            return;
        }

        Category c = new Category(id, name, description);
        boolean success = categoryDAO.updateCategory(c);

        if (success) {
            request.getSession().setAttribute("successMessage", "Category updated successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to update category.");
        }
        response.sendRedirect(request.getContextPath() + "/categories");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        try {
            boolean success = categoryDAO.deleteCategory(id);
            if (success) {
                request.getSession().setAttribute("successMessage", "Category deleted successfully.");
            } else {
                request.getSession().setAttribute("errorMessage", "Category not found or could not be deleted.");
            }
        } catch (SQLException e) {
            request.getSession().setAttribute("errorMessage", "Cannot delete category: products are assigned to it.");
        }
        response.sendRedirect(request.getContextPath() + "/categories");
    }
}

