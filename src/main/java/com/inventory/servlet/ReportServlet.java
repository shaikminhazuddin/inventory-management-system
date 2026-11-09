package com.inventory.servlet;

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
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller for Inventory Reports and CSV Data Export.
 */
@WebServlet(urlPatterns = {"/reports", "/reports/export"})
public class ReportServlet extends HttpServlet {
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

        String path = request.getServletPath();
        String reportType = request.getParameter("type");
        if (reportType == null || reportType.trim().isEmpty()) {
            reportType = "STOCK_SUMMARY";
        }

        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String export = request.getParameter("export");

        try {
            if ("csv".equalsIgnoreCase(export) || "/reports/export".equals(path)) {
                handleCsvExport(reportType, startDate, endDate, response);
                return;
            }

            // Normal reports page rendering
            List<Product> products = productDAO.getAllProducts();
            List<StockTransaction> transactions = stockDAO.getFilteredTransactions("ALL", startDate, endDate);
            double totalValuation = stockDAO.getTotalInventoryValuation();
            int totalStockUnits = stockDAO.getTotalStockQuantity();

            request.setAttribute("reportType", reportType);
            request.setAttribute("startDate", startDate);
            request.setAttribute("endDate", endDate);
            request.setAttribute("products", products);
            request.setAttribute("transactions", transactions);
            request.setAttribute("totalValuation", totalValuation);
            request.setAttribute("totalStockUnits", totalStockUnits);

            request.getRequestDispatcher("/WEB-INF/views/reports.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error compiling report: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/reports.jsp").forward(request, response);
        }
    }

    private void handleCsvExport(String reportType, String startDate, String endDate,
                                 HttpServletResponse response) throws SQLException, IOException {

        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        String filename = "inventory_report_" + reportType.toLowerCase() + "_" + System.currentTimeMillis() + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        PrintWriter writer = response.getWriter();

        if ("STOCK_IN".equalsIgnoreCase(reportType) || "STOCK_OUT".equalsIgnoreCase(reportType)) {
            String filterType = "STOCK_IN".equalsIgnoreCase(reportType) ? "IN" : "OUT";
            List<StockTransaction> list = stockDAO.getFilteredTransactions(filterType, startDate, endDate);

            writer.println("Transaction ID,Date,Type,Product Name,SKU,Quantity,Supplier,Reference,Reason");
            for (StockTransaction t : list) {
                writer.printf("\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%d\",\"%s\",\"%s\",\"%s\"%n",
                        t.getTransactionId(),
                        t.getTransactionDate(),
                        t.getTransactionType(),
                        escapeCsv(t.getProductName()),
                        escapeCsv(t.getSkuCode()),
                        t.getQuantity(),
                        escapeCsv(t.getSupplierName() != null ? t.getSupplierName() : "N/A"),
                        escapeCsv(t.getReferenceNumber() != null ? t.getReferenceNumber() : ""),
                        escapeCsv(t.getReason() != null ? t.getReason() : "")
                );
            }
        } else {
            // Products stock catalog report
            List<Product> list = productDAO.getAllProducts();

            writer.println("Product ID,Product Name,SKU,Barcode,Category,Purchase Price,Selling Price,Current Stock,Min Stock,Status,Valuation");
            for (Product p : list) {
                writer.printf("\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%.2f\",\"%.2f\",\"%d\",\"%d\",\"%s\",\"%.2f\"%n",
                        p.getProductId(),
                        escapeCsv(p.getProductName()),
                        escapeCsv(p.getSkuCode()),
                        escapeCsv(p.getBarcode() != null ? p.getBarcode() : ""),
                        escapeCsv(p.getCategoryName()),
                        p.getPurchasePrice(),
                        p.getSellingPrice(),
                        p.getCurrentStock(),
                        p.getMinStockLevel(),
                        p.getAlertStatus(),
                        p.getInventoryValue()
                );
            }
        }

        writer.flush();
    }

    private String escapeCsv(String input) {
        if (input == null) return "";
        return input.replace("\"", "\"\"");
    }
}

