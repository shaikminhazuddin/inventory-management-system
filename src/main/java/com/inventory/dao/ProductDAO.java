package com.inventory.dao;

import com.inventory.model.Product;
import com.inventory.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Products.
 * Uses PreparedStatements for all database interactions.
 */
public class ProductDAO {

    /**
     * Maps a ResultSet row to a Product bean.
     */
    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setProductName(rs.getString("product_name"));
        p.setSkuCode(rs.getString("sku_code"));
        p.setBarcode(rs.getString("barcode"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setCategoryName(rs.getString("category_name"));
        p.setDescription(rs.getString("description"));
        p.setPurchasePrice(rs.getDouble("purchase_price"));
        p.setSellingPrice(rs.getDouble("selling_price"));
        p.setCurrentStock(rs.getInt("current_stock"));
        p.setMinStockLevel(rs.getInt("min_stock_level"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }

    /**
     * Retrieves all products joined with their category names.
     */
    public List<Product> getAllProducts() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.product_name, p.sku_code, p.barcode, p.category_id, " +
                     "c.category_name, p.description, p.purchase_price, p.selling_price, " +
                     "p.current_stock, p.min_stock_level, p.created_at " +
                     "FROM products p " +
                     "JOIN categories c ON p.category_id = c.category_id " +
                     "ORDER BY p.product_name ASC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return list;
    }

    /**
     * Retrieves product by ID.
     */
    public Product getProductById(int id) throws SQLException {
        String sql = "SELECT p.product_id, p.product_name, p.sku_code, p.barcode, p.category_id, " +
                     "c.category_name, p.description, p.purchase_price, p.selling_price, " +
                     "p.current_stock, p.min_stock_level, p.created_at " +
                     "FROM products p " +
                     "JOIN categories c ON p.category_id = c.category_id " +
                     "WHERE p.product_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } finally {
            DBConnection.close(rs, ps, conn);
        }
    }

    /**
     * Checks if a SKU code is already in use by another product.
     */
    public boolean isSkuExists(String skuCode, int excludeId) throws SQLException {
        String sql = "SELECT 1 FROM products WHERE LOWER(sku_code) = LOWER(?) AND product_id != ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, skuCode.trim());
            ps.setInt(2, excludeId);
            rs = ps.executeQuery();
            return rs.next();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
    }

    /**
     * Inserts a new product into the database.
     */
    public boolean insertProduct(Product p) throws SQLException {
        String sql = "INSERT INTO products (product_name, sku_code, barcode, category_id, description, " +
                     "purchase_price, selling_price, current_stock, min_stock_level) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, p.getProductName().trim());
            ps.setString(2, p.getSkuCode().trim().toUpperCase());
            ps.setString(3, p.getBarcode());
            ps.setInt(4, p.getCategoryId());
            ps.setString(5, p.getDescription());
            ps.setDouble(6, p.getPurchasePrice());
            ps.setDouble(7, p.getSellingPrice());
            ps.setInt(8, p.getCurrentStock());
            ps.setInt(9, p.getMinStockLevel());
            return ps.executeUpdate() > 0;
        } finally {
            DBConnection.close(ps, conn);
        }
    }

    /**
     * Updates an existing product.
     */
    public boolean updateProduct(Product p) throws SQLException {
        String sql = "UPDATE products SET product_name = ?, sku_code = ?, barcode = ?, category_id = ?, " +
                     "description = ?, purchase_price = ?, selling_price = ?, min_stock_level = ? " +
                     "WHERE product_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, p.getProductName().trim());
            ps.setString(2, p.getSkuCode().trim().toUpperCase());
            ps.setString(3, p.getBarcode());
            ps.setInt(4, p.getCategoryId());
            ps.setString(5, p.getDescription());
            ps.setDouble(6, p.getPurchasePrice());
            ps.setDouble(7, p.getSellingPrice());
            ps.setInt(8, p.getMinStockLevel());
            ps.setInt(9, p.getProductId());
            return ps.executeUpdate() > 0;
        } finally {
            DBConnection.close(ps, conn);
        }
    }

    /**
     * Deletes a product by ID.
     */
    public boolean deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } finally {
            DBConnection.close(ps, conn);
        }
    }

    /**
     * Searches products by keyword (name, SKU, barcode) and optional category filter.
     */
    public List<Product> searchAndFilter(String keyword, Integer categoryId) throws SQLException {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.product_id, p.product_name, p.sku_code, p.barcode, p.category_id, " +
                "c.category_name, p.description, p.purchase_price, p.selling_price, " +
                "p.current_stock, p.min_stock_level, p.created_at " +
                "FROM products p " +
                "JOIN categories c ON p.category_id = c.category_id WHERE 1=1 "
        );

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(p.product_name) LIKE ? OR LOWER(p.sku_code) LIKE ? OR LOWER(p.barcode) LIKE ?) ");
        }

        if (categoryId != null && categoryId > 0) {
            sql.append("AND p.category_id = ? ");
        }

        sql.append("ORDER BY p.product_name ASC");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql.toString());

            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
            }

            if (categoryId != null && categoryId > 0) {
                ps.setInt(paramIndex++, categoryId);
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return list;
    }

    /**
     * Retrieves all products where stock is at or below minimum threshold.
     */
    public List<Product> getAlertProducts() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.product_name, p.sku_code, p.barcode, p.category_id, " +
                     "c.category_name, p.description, p.purchase_price, p.selling_price, " +
                     "p.current_stock, p.min_stock_level, p.created_at " +
                     "FROM products p " +
                     "JOIN categories c ON p.category_id = c.category_id " +
                     "WHERE p.current_stock <= p.min_stock_level " +
                     "ORDER BY p.current_stock ASC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return list;
    }
}

