package com.inventory.servlet;

import com.inventory.dao.CategoryDAO;
import com.inventory.dao.ProductDAO;
import com.inventory.model.Category;
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
 * Controller for Product Management: List, Search, Filter, Add, Edit, and Delete.
 */
@WebServlet(urlPatterns = {"/products"})
public class ProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        this.productDAO = new ProductDAO();
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

            // Normal list view with search / filter
            String keyword = request.getParameter("keyword");
            String catParam = request.getParameter("categoryId");
            Integer categoryId = null;
            if (catParam != null && !catParam.trim().isEmpty()) {
                try {
                    categoryId = Integer.parseInt(catParam.trim());
                } catch (NumberFormatException ignored) {}
            }

            List<Product> products = productDAO.searchAndFilter(keyword, categoryId);
            List<Category> categories = categoryDAO.getAllCategories();

            request.setAttribute("products", products);
            request.setAttribute("categories", categories);
            request.setAttribute("selectedCategory", categoryId);
            request.setAttribute("searchKeyword", keyword);

            // Forward to view
            request.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading products: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(request, response);
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
                response.sendRedirect(request.getContextPath() + "/products");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid numeric input. Please check price and stock values.");
            response.sendRedirect(request.getContextPath() + "/products");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Operation failed: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/products");
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        String name = request.getParameter("productName");
        String sku = request.getParameter("skuCode");
        String barcode = request.getParameter("barcode");
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        String description = request.getParameter("description");
        double purchasePrice = Double.parseDouble(request.getParameter("purchasePrice"));
        double sellingPrice = Double.parseDouble(request.getParameter("sellingPrice"));
        int currentStock = Integer.parseInt(request.getParameter("currentStock"));
        int minStockLevel = Integer.parseInt(request.getParameter("minStockLevel"));

        // Validate SKU uniqueness
        if (productDAO.isSkuExists(sku, 0)) {
            request.getSession().setAttribute("errorMessage", "Error: SKU code '" + sku + "' already exists!");
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        Product p = new Product(0, name, sku, barcode, categoryId, description,
                purchasePrice, sellingPrice, currentStock, minStockLevel);

        boolean success = productDAO.insertProduct(p);
        if (success) {
            request.getSession().setAttribute("successMessage", "Product '" + name + "' added successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to add product.");
        }
        response.sendRedirect(request.getContextPath() + "/products");
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int id = Integer.parseInt(request.getParameter("productId"));
        String name = request.getParameter("productName");
        String sku = request.getParameter("skuCode");
        String barcode = request.getParameter("barcode");
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        String description = request.getParameter("description");
        double purchasePrice = Double.parseDouble(request.getParameter("purchasePrice"));
        double sellingPrice = Double.parseDouble(request.getParameter("sellingPrice"));
        int minStockLevel = Integer.parseInt(request.getParameter("minStockLevel"));

        // Validate SKU uniqueness
        if (productDAO.isSkuExists(sku, id)) {
            request.getSession().setAttribute("errorMessage", "Error: SKU code '" + sku + "' is already assigned to another product!");
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        Product p = new Product();
        p.setProductId(id);
        p.setProductName(name);
        p.setSkuCode(sku);
        p.setBarcode(barcode);
        p.setCategoryId(categoryId);
        p.setDescription(description);
        p.setPurchasePrice(purchasePrice);
        p.setSellingPrice(sellingPrice);
        p.setMinStockLevel(minStockLevel);

        boolean success = productDAO.updateProduct(p);
        if (success) {
            request.getSession().setAttribute("successMessage", "Product updated successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to update product.");
        }
        response.sendRedirect(request.getContextPath() + "/products");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        try {
            boolean success = productDAO.deleteProduct(id);
            if (success) {
                request.getSession().setAttribute("successMessage", "Product deleted successfully.");
            } else {
                request.getSession().setAttribute("errorMessage", "Product not found or could not be deleted.");
            }
        } catch (SQLException e) {
            request.getSession().setAttribute("errorMessage", "Cannot delete product: associated with stock movement records.");
        }
        response.sendRedirect(request.getContextPath() + "/products");
    }
}

