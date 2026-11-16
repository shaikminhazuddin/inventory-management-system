package com.inventory.servlet;

import com.inventory.dao.SupplierDAO;
import com.inventory.model.Supplier;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller for Supplier Management: View, Add, Edit, and Delete.
 */
@WebServlet(urlPatterns = {"/suppliers"})
public class SupplierServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private SupplierDAO supplierDAO;

    @Override
    public void init() throws ServletException {
        this.supplierDAO = new SupplierDAO();
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

            List<Supplier> suppliers = supplierDAO.getAllSuppliers();
            request.setAttribute("suppliers", suppliers);
            request.getRequestDispatcher("/WEB-INF/views/suppliers.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error retrieving suppliers: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/suppliers.jsp").forward(request, response);
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
                response.sendRedirect(request.getContextPath() + "/suppliers");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid supplier ID specified.");
            response.sendRedirect(request.getContextPath() + "/suppliers");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Supplier operation failed: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/suppliers");
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        String name = request.getParameter("supplierName");
        String contact = request.getParameter("contactPerson");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String address = request.getParameter("address");

        if (name == null || name.trim().isEmpty()) {
            request.getSession().setAttribute("errorMessage", "Supplier name cannot be empty.");
            response.sendRedirect(request.getContextPath() + "/suppliers");
            return;
        }

        Supplier s = new Supplier(0, name, contact, phone, email, address);
        boolean success = supplierDAO.insertSupplier(s);

        if (success) {
            request.getSession().setAttribute("successMessage", "Supplier '" + name + "' registered successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to register supplier.");
        }
        response.sendRedirect(request.getContextPath() + "/suppliers");
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int id = Integer.parseInt(request.getParameter("supplierId"));
        String name = request.getParameter("supplierName");
        String contact = request.getParameter("contactPerson");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String address = request.getParameter("address");

        Supplier s = new Supplier(id, name, contact, phone, email, address);
        boolean success = supplierDAO.updateSupplier(s);

        if (success) {
            request.getSession().setAttribute("successMessage", "Supplier information updated!");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to update supplier.");
        }
        response.sendRedirect(request.getContextPath() + "/suppliers");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        try {
            boolean success = supplierDAO.deleteSupplier(id);
            if (success) {
                request.getSession().setAttribute("successMessage", "Supplier removed successfully.");
            } else {
                request.getSession().setAttribute("errorMessage", "Supplier not found or could not be removed.");
            }
        } catch (SQLException e) {
            request.getSession().setAttribute("errorMessage", "Cannot remove supplier: linked to stock transaction records.");
        }
        response.sendRedirect(request.getContextPath() + "/suppliers");
    }
}

