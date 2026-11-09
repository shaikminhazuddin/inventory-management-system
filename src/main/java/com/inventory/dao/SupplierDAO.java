package com.inventory.dao;

import com.inventory.model.Supplier;
import com.inventory.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Suppliers.
 */
public class SupplierDAO {

    /**
     * Retrieves all suppliers ordered by name.
     */
    public List<Supplier> getAllSuppliers() throws SQLException {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT supplier_id, supplier_name, contact_person, phone, email, address, created_at " +
                     "FROM suppliers ORDER BY supplier_name ASC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Supplier s = new Supplier();
                s.setSupplierId(rs.getInt("supplier_id"));
                s.setSupplierName(rs.getString("supplier_name"));
                s.setContactPerson(rs.getString("contact_person"));
                s.setPhone(rs.getString("phone"));
                s.setEmail(rs.getString("email"));
                s.setAddress(rs.getString("address"));
                s.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(s);
            }
        } finally {
            DBConnection.close(rs, ps, conn);
        }
        return list;
    }

    /**
     * Retrieves supplier by ID.
     */
    public Supplier getSupplierById(int id) throws SQLException {
        String sql = "SELECT supplier_id, supplier_name, contact_person, phone, email, address, created_at " +
                     "FROM suppliers WHERE supplier_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                Supplier s = new Supplier();
                s.setSupplierId(rs.getInt("supplier_id"));
                s.setSupplierName(rs.getString("supplier_name"));
                s.setContactPerson(rs.getString("contact_person"));
                s.setPhone(rs.getString("phone"));
                s.setEmail(rs.getString("email"));
                s.setAddress(rs.getString("address"));
                s.setCreatedAt(rs.getTimestamp("created_at"));
                return s;
            }
            return null;
        } finally {
            DBConnection.close(rs, ps, conn);
        }
    }

    /**
     * Inserts a new supplier.
     */
    public boolean insertSupplier(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO suppliers (supplier_name, contact_person, phone, email, address) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, supplier.getSupplierName().trim());
            ps.setString(2, supplier.getContactPerson());
            ps.setString(3, supplier.getPhone());
            ps.setString(4, supplier.getEmail());
            ps.setString(5, supplier.getAddress());
            return ps.executeUpdate() > 0;
        } finally {
            DBConnection.close(ps, conn);
        }
    }

    /**
     * Updates an existing supplier.
     */
    public boolean updateSupplier(Supplier supplier) throws SQLException {
        String sql = "UPDATE suppliers SET supplier_name = ?, contact_person = ?, phone = ?, email = ?, address = ? WHERE supplier_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, supplier.getSupplierName().trim());
            ps.setString(2, supplier.getContactPerson());
            ps.setString(3, supplier.getPhone());
            ps.setString(4, supplier.getEmail());
            ps.setString(5, supplier.getAddress());
            ps.setInt(6, supplier.getSupplierId());
            return ps.executeUpdate() > 0;
        } finally {
            DBConnection.close(ps, conn);
        }
    }

    /**
     * Deletes a supplier by ID.
     */
    public boolean deleteSupplier(int id) throws SQLException {
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
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

