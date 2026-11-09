package com.inventory.dao;

import com.inventory.model.Category;
import com.inventory.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Product Categories.
 */
public class CategoryDAO {

    /**
     * Retrieves all categories along with their respective product counts.
     */
    public List<Category> getAllCategories() throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT c.category_id, c.category_name, c.description, c.created_at, " +
                     "COUNT(p.product_id) AS product_count " +
                     "FROM categories c " +
                     "LEFT JOIN products p ON c.category_id = p.category_id " +
                     "GROUP BY c.category_id, c.category_name, c.description, c.created_at " +
                     "ORDER BY c.category_name ASC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Category c = new Category();
                c.setCategoryId(rs.getInt("category_id"));
                c.setCategoryName(rs.getString("category_name"));
                c.setDescription(rs.getString("description"));
                c.setCreatedAt(rs.getTimestamp("created_at"));
                c.setProductCount(rs.getInt("product_count"));
                list.add(c);
            }
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return list;
    }

    /**
     * Retrieves category by primary key.
     */
    public Category getCategoryById(int id) throws SQLException {
        String sql = "SELECT category_id, category_name, description, created_at FROM categories WHERE category_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                Category c = new Category();
                c.setCategoryId(rs.getInt("category_id"));
                c.setCategoryName(rs.getString("category_name"));
                c.setDescription(rs.getString("description"));
                c.setCreatedAt(rs.getTimestamp("created_at"));
                return c;
            }
            return null;
        } finally {
            DBConnection.close(rs, ps, conn);
        }
    }

    /**
     * Checks if category name already exists (excluding the current category ID when editing).
     */
    public boolean isCategoryNameExists(String name, int excludeId) throws SQLException {
        String sql = "SELECT 1 FROM categories WHERE LOWER(category_name) = LOWER(?) AND category_id != ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name.trim());
            ps.setInt(2, excludeId);
            rs = ps.executeQuery();
            return rs.next();
        } finally {
            DBConnection.close(rs, ps, conn);
        }
    }

    /**
     * Inserts a new category.
     */
    public boolean insertCategory(Category category) throws SQLException {
        String sql = "INSERT INTO categories (category_name, description) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, category.getCategoryName().trim());
            ps.setString(2, category.getDescription());
            return ps.executeUpdate() > 0;
        } finally {
            DBConnection.close(ps, conn);
        }
    }

    /**
     * Updates an existing category.
     */
    public boolean updateCategory(Category category) throws SQLException {
        String sql = "UPDATE categories SET category_name = ?, description = ? WHERE category_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, category.getCategoryName().trim());
            ps.setString(2, category.getDescription());
            ps.setInt(3, category.getCategoryId());
            return ps.executeUpdate() > 0;
        } finally {
            DBConnection.close(ps, conn);
        }
    }

    /**
     * Deletes a category if no products are associated with it.
     */
    public boolean deleteCategory(int id) throws SQLException {
        String sql = "DELETE FROM categories WHERE category_id = ?";
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
}

