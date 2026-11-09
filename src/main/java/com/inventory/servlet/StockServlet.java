package com.inventory.servlet;

import com.inventory.dao.ProductDAO;
import com.inventory.dao.StockDAO;
import com.inventory.dao.SupplierDAO;
import com.inventory.model.Product;
import com.inventory.model.StockTransaction;
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
 * Controller for Warehouse Stock Operations: Stock IN, Stock OUT, and Movement Logs.
 * Coordinates database operations with transactional integrity.
 */
@WebServlet(urlPatterns = {"/stock"})
public class StockServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private StockDAO stockDAO;
    private ProductDAO productDAO;
    private SupplierDAO supplierDAO;

    @Override
    public void init() throws ServletException {
        this.stockDAO = new StockDAO();
        this.productDAO = new ProductDAO();
        this.supplierDAO = new SupplierDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Product> products = productDAO.getAllProducts();
            List<Supplier> suppliers = supplierDAO.getAllSuppliers();
            List<StockTransaction> transactions = stockDAO.getRecentTransactions(50);

            request.setAttribute("products", products);
            request.setAttribute("suppliers", suppliers);
            request.setAttribute("transactions", transactions);

            request.getRequestDispatcher("/WEB-INF/views/stock.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading stock management data: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/stock.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("in".equalsIgnoreCase(action)) {
                handleStockIn(request, response);
            } else if ("out".equalsIgnoreCase(action)) {
                handleStockOut(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/stock");
            }
        } catch (IllegalArgumentException e) {
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/stock");
        } catch (SQLException e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Transaction error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/stock");
        }
    }

    private void handleStockIn(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String supplierParam = request.getParameter("supplierId");
        Integer supplierId = (supplierParam != null && !supplierParam.trim().isEmpty())
                ? Integer.parseInt(supplierParam.trim())
                : null;
        String referenceNumber = request.getParameter("referenceNumber");
        String reason = request.getParameter("reason");

        boolean success = stockDAO.processStockIn(productId, quantity, supplierId, referenceNumber, reason);

        if (success) {
            request.getSession().setAttribute("successMessage",
                    "Stock IN recorded successfully! Added " + quantity + " units.");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to record Stock IN.");
        }
        response.sendRedirect(request.getContextPath() + "/stock");
    }

    private void handleStockOut(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String referenceNumber = request.getParameter("referenceNumber");
        String reason = request.getParameter("reason");

        boolean success = stockDAO.processStockOut(productId, quantity, referenceNumber, reason);

        if (success) {
            request.getSession().setAttribute("successMessage",
                    "Stock OUT processed successfully! Deducted " + quantity + " units.");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to process Stock OUT.");
        }
        response.sendRedirect(request.getContextPath() + "/stock");
    }
}

