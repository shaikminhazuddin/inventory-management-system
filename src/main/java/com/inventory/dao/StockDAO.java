package com.inventory.dao;

import com.inventory.model.StockTransaction;
import com.inventory.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Stock Operations and Dashboard Statistics.
 * Employs strict JDBC Transactions (commit & rollback) for data integrity.
 */
public class StockDAO {

    /**
     * Executes a Stock IN operation within a single JDBC transaction.
     * Updates product stock and logs the movement.
     */
    public boolean processStockIn(int productId, int quantity, Integer supplierId,
                                  String referenceNumber, String reason) throws SQLException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Stock IN quantity must be greater than zero.");
        }

        Connection conn = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psInsert = null;

        String updateSql = "UPDATE products SET current_stock = current_stock + ? WHERE product_id = ?";
        String insertSql = "INSERT INTO stock_transactions (product_id, transaction_type, quantity, " +
                           "supplier_id, reference_number, reason) VALUES (?, 'IN', ?, ?, ?, ?)";

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Update product stock
            psUpdate = conn.prepareStatement(updateSql);
            psUpdate.setInt(1, quantity);
            psUpdate.setInt(2, productId);
            int rowsUpdated = psUpdate.executeUpdate();

            if (rowsUpdated == 0) {
                conn.rollback();
                return false;
            }

            // 2. Insert audit transaction
            psInsert = conn.prepareStatement(insertSql);
            psInsert.setInt(1, productId);
            psInsert.setInt(2, quantity);
            if (supplierId != null && supplierId > 0) {
                psInsert.setInt(3, supplierId);
            } else {
                psInsert.setNull(3, Types.INTEGER);
            }
            psInsert.setString(4, referenceNumber);
            psInsert.setString(5, reason);
            psInsert.executeUpdate();

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            DBConnection.close(psUpdate, psInsert, conn);
        }
    }

    /**
     * Executes a Stock OUT operation within a single JDBC transaction.
     * Prevents negative inventory by ensuring current_stock >= requested quantity.
     */
    public boolean processStockOut(int productId, int quantity,
                                   String referenceNumber, String reason) throws SQLException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Stock OUT quantity must be greater than zero.");
        }

        Connection conn = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psInsert = null;

        // Concurrency-safe: atomic decrement conditioned on sufficient stock
        String updateSql = "UPDATE products SET current_stock = current_stock - ? " +
                           "WHERE product_id = ? AND current_stock >= ?";
        String insertSql = "INSERT INTO stock_transactions (product_id, transaction_type, quantity, " +
                           "supplier_id, reference_number, reason) VALUES (?, 'OUT', ?, NULL, ?, ?)";

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Attempt atomic deduction
            psUpdate = conn.prepareStatement(updateSql);
            psUpdate.setInt(1, quantity);
            psUpdate.setInt(2, productId);
            psUpdate.setInt(3, quantity);
            int rowsUpdated = psUpdate.executeUpdate();

            if (rowsUpdated == 0) {
                // Stock was insufficient or product was not found!
                conn.rollback();
                throw new SQLException("Insufficient stock! Cannot deduct " + quantity + " units.");
            }

            // 2. Insert audit log
            psInsert = conn.prepareStatement(insertSql);
            psInsert.setInt(1, productId);
            psInsert.setInt(2, quantity);
            psInsert.setString(3, referenceNumber);
            psInsert.setString(4, reason);
            psInsert.executeUpdate();

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            DBConnection.close(psUpdate, psInsert, conn);
        }
    }

    /**
     * Retrieves recent transactions for dashboard display.
     */
    public List<StockTransaction> getRecentTransactions(int limit) throws SQLException {
        List<StockTransaction> list = new ArrayList<>();
        String sql = "SELECT t.transaction_id, t.product_id, p.product_name, p.sku_code, " +
                     "t.transaction_type, t.quantity, t.supplier_id, s.supplier_name, " +
                     "t.reference_number, t.reason, t.transaction_date " +
                     "FROM stock_transactions t " +
                     "JOIN products p ON t.product_id = p.product_id " +
                     "LEFT JOIN suppliers s ON t.supplier_id = s.supplier_id " +
                     "ORDER BY t.transaction_date DESC LIMIT ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
            rs = ps.executeQuery();

            while (rs.next()) {
                StockTransaction st = new StockTransaction();
                st.setTransactionId(rs.getInt("transaction_id"));
                st.setProductId(rs.getInt("product_id"));
                st.setProductName(rs.getString("product_name"));
                st.setSkuCode(rs.getString("sku_code"));
                st.setTransactionType(rs.getString("transaction_type"));
                st.setQuantity(rs.getInt("quantity"));
                st.setSupplierId(rs.getObject("supplier_id") != null ? rs.getInt("supplier_id") : null);
                st.setSupplierName(rs.getString("supplier_name"));
                st.setReferenceNumber(rs.getString("reference_number"));
                st.setReason(rs.getString("reason"));
                st.setTransactionDate(rs.getTimestamp("transaction_date"));
                list.add(st);
            }
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return list;
    }

    /**
     * Retrieves transactions filtered by type and optional date range.
     */
    public List<StockTransaction> getFilteredTransactions(String type, String startDate, String endDate) throws SQLException {
        List<StockTransaction> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT t.transaction_id, t.product_id, p.product_name, p.sku_code, " +
                "t.transaction_type, t.quantity, t.supplier_id, s.supplier_name, " +
                "t.reference_number, t.reason, t.transaction_date " +
                "FROM stock_transactions t " +
                "JOIN products p ON t.product_id = p.product_id " +
                "LEFT JOIN suppliers s ON t.supplier_id = s.supplier_id WHERE 1=1 "
        );

        if (type != null && !type.trim().isEmpty() && !type.equalsIgnoreCase("ALL")) {
            sql.append("AND t.transaction_type = ? ");
        }
        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append("AND DATE(t.transaction_date) >= ? ");
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append("AND DATE(t.transaction_date) <= ? ");
        }

        sql.append("ORDER BY t.transaction_date DESC");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql.toString());

            int idx = 1;
            if (type != null && !type.trim().isEmpty() && !type.equalsIgnoreCase("ALL")) {
                ps.setString(idx++, type.trim().toUpperCase());
            }
            if (startDate != null && !startDate.trim().isEmpty()) {
                ps.setString(idx++, startDate.trim());
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                ps.setString(idx++, endDate.trim());
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                StockTransaction st = new StockTransaction();
                st.setTransactionId(rs.getInt("transaction_id"));
                st.setProductId(rs.getInt("product_id"));
                st.setProductName(rs.getString("product_name"));
                st.setSkuCode(rs.getString("sku_code"));
                st.setTransactionType(rs.getString("transaction_type"));
                st.setQuantity(rs.getInt("quantity"));
                st.setSupplierId(rs.getObject("supplier_id") != null ? rs.getInt("supplier_id") : null);
                st.setSupplierName(rs.getString("supplier_name"));
                st.setReferenceNumber(rs.getString("reference_number"));
                st.setReason(rs.getString("reason"));
                st.setTransactionDate(rs.getTimestamp("transaction_date"));
                list.add(st);
            }
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // DASHBOARD METRIC AGGREGATIONS (Direct live MySQL computations)
    // -------------------------------------------------------------------------

    public int getTotalProductsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM products";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int getTotalStockQuantity() throws SQLException {
        String sql = "SELECT COALESCE(SUM(current_stock), 0) FROM products";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int getLowStockProductsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM products WHERE current_stock <= min_stock_level AND current_stock > 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int getOutOfStockProductsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM products WHERE current_stock = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public double getTotalInventoryValuation() throws SQLException {
        String sql = "SELECT COALESCE(SUM(current_stock * purchase_price), 0.0) FROM products";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    /**
     * Aggregates stock quantities per category for Chart.js rendering.
     */
    public Map<String, Integer> getStockByCategory() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT c.category_name, COALESCE(SUM(p.current_stock), 0) AS total_stock " +
                     "FROM categories c " +
                     "LEFT JOIN products p ON c.category_id = p.category_id " +
                     "GROUP BY c.category_id, c.category_name " +
                     "ORDER BY total_stock DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("category_name"), rs.getInt("total_stock"));
            }
        }
        return map;
    }
}

