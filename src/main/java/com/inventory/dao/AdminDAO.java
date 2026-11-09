package com.inventory.dao;

import com.inventory.model.Admin;
import com.inventory.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for Admin accounts using PreparedStatement queries.
 */
public class AdminDAO {

    /**
     * Finds an admin by username.
     */
    public Admin findByUsername(String username) throws SQLException {
        String sql = "SELECT admin_id, username, password_hash, full_name, email, created_at FROM admin WHERE username = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                Admin admin = new Admin();
                admin.setAdminId(rs.getInt("admin_id"));
                admin.setUsername(rs.getString("username"));
                admin.setPasswordHash(rs.getString("password_hash"));
                admin.setFullName(rs.getString("full_name"));
                admin.setEmail(rs.getString("email"));
                admin.setCreatedAt(rs.getTimestamp("created_at"));
                return admin;
            }
            return null;
        } finally {
            DBConnection.close(rs, ps, conn);
        }
    }

    /**
     * Updates an admin password.
     */
    public boolean updatePassword(int adminId, String newPasswordHash) throws SQLException {
        String sql = "UPDATE admin SET password_hash = ? WHERE admin_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, newPasswordHash);
            ps.setInt(2, adminId);
            return ps.executeUpdate() > 0;
        } finally {
            DBConnection.close(ps, conn);
        }
    }

    /**
     * Ensures default 'admin' exists in MySQL. If missing, auto-inserts default admin account.
     */
    public Admin createDefaultAdminIfNotExists() {
        String checkSql = "SELECT admin_id, username, password_hash, full_name, email, created_at FROM admin WHERE username = 'admin'";
        String insertSql = "INSERT INTO admin (username, password_hash, full_name, email) VALUES ('admin', ?, 'System Administrator', 'admin@warehouse.local')";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(checkSql);
            rs = ps.executeQuery();
            if (rs.next()) {
                Admin admin = new Admin();
                admin.setAdminId(rs.getInt("admin_id"));
                admin.setUsername(rs.getString("username"));
                admin.setPasswordHash(rs.getString("password_hash"));
                admin.setFullName(rs.getString("full_name"));
                admin.setEmail(rs.getString("email"));
                admin.setCreatedAt(rs.getTimestamp("created_at"));
                return admin;
            }
            DBConnection.close(rs, ps);

            String hash = "$2a$10$Peotn0oBQb3yHuF.eDoeR.k79/Bd30iem66SL64aqMJjrxwZjfqbq";
            ps = conn.prepareStatement(insertSql, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, hash);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            int newId = rs.next() ? rs.getInt(1) : 1;
            return new Admin(newId, "admin", hash, "System Administrator", "admin@warehouse.local", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DBConnection.close(rs, ps, conn);
        }
    }
}

