package com.inventory.servlet;

import com.inventory.dao.ProductDAO;
import com.inventory.dao.StockDAO;
import com.inventory.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller for Warehouse Low-Stock and Out-of-Stock Alerts.
 */
@WebServlet(urlPatterns = {"/alerts"})
public class AlertServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ProductDAO productDAO;
    private StockDAO stockDAO;

    @Override
    public void init() throws ServletException {
        this.productDAO = new ProductDAO();
        this.stockDAO = new StockDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Product> alertProducts = productDAO.getAlertProducts();
            int lowStockCount = stockDAO.getLowStockProductsCount();
            int outOfStockCount = stockDAO.getOutOfStockProductsCount();

            request.setAttribute("alertProducts", alertProducts);
            request.setAttribute("lowStockCount", lowStockCount);
            request.setAttribute("outOfStockCount", outOfStockCount);

            request.getRequestDispatcher("/WEB-INF/views/alerts.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error fetching stock alerts: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/alerts.jsp").forward(request, response);
        }
    }
}

