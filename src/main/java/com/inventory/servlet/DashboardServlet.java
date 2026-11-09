package com.inventory.servlet;

import com.google.gson.Gson;
import com.inventory.dao.ProductDAO;
import com.inventory.dao.StockDAO;
import com.inventory.model.Product;
import com.inventory.model.StockTransaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Controller for the Executive Inventory Dashboard.
 * Gathers real-time statistics from the database and forwards to dashboard.jsp.
 */
@WebServlet(urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private StockDAO stockDAO;
    private ProductDAO productDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.stockDAO = new StockDAO();
        this.productDAO = new ProductDAO();
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Compute real-time KPI counts
            int totalProducts = stockDAO.getTotalProductsCount();
            int totalStockQty = stockDAO.getTotalStockQuantity();
            int lowStockCount = stockDAO.getLowStockProductsCount();
            int outOfStockCount = stockDAO.getOutOfStockProductsCount();
            double totalValuation = stockDAO.getTotalInventoryValuation();

            // Fetch recent movements and alert products
            List<StockTransaction> recentTransactions = stockDAO.getRecentTransactions(8);
            List<Product> alertProducts = productDAO.getAlertProducts();

            // Fetch Category stock distribution for Chart.js
            Map<String, Integer> categoryStock = stockDAO.getStockByCategory();
            String categoryLabelsJson = gson.toJson(categoryStock.keySet());
            String categoryDataJson = gson.toJson(categoryStock.values());

            // Set request attributes for JSP view
            request.setAttribute("totalProducts", totalProducts);
            request.setAttribute("totalStockQty", totalStockQty);
            request.setAttribute("lowStockCount", lowStockCount);
            request.setAttribute("outOfStockCount", outOfStockCount);
            request.setAttribute("totalValuation", totalValuation);
            request.setAttribute("recentTransactions", recentTransactions);
            request.setAttribute("alertProducts", alertProducts);
            request.setAttribute("categoryLabelsJson", categoryLabelsJson);
            request.setAttribute("categoryDataJson", categoryDataJson);

            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading dashboard metrics: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
        }
    }
}

