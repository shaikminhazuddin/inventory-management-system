package com.inventory.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to manage MySQL JDBC database connections.
 * Securely loads credentials from environment variables, system properties, or a local .env file.
 */
public class DBConnection {

    private static final Map<String, String> dotEnv = loadDotEnv();

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/inventory_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static final String URL = getEnv("DB_URL", DEFAULT_URL);
    private static final String USERNAME = getEnv("DB_USER", DEFAULT_USER);
    private static final String PASSWORD = getEnv("DB_PASSWORD", DEFAULT_PASSWORD);

    static {
        try {
            // Load MySQL 8+ JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL: MySQL JDBC Driver not found in classpath!");
            e.printStackTrace();
        }
    }

    /**
     * Resolves configuration key from:
     * 1. OS Environment Variables (DB_URL, DB_USER, DB_PASSWORD)
     * 2. JVM System Properties (-DDB_URL=...)
     * 3. Local .env file
     * 4. Safe default value
     */
    private static String getEnv(String key, String fallback) {
        String val = System.getenv(key);
        if (val != null && !val.trim().isEmpty()) {
            return val.trim();
        }
        val = System.getProperty(key);
        if (val != null && !val.trim().isEmpty()) {
            return val.trim();
        }
        val = dotEnv.get(key);
        if (val != null && !val.trim().isEmpty()) {
            return val.trim();
        }
        return fallback;
    }

    /**
     * Parses local .env file if available (for development without committing secrets).
     */
    private static Map<String, String> loadDotEnv() {
        Map<String, String> map = new HashMap<>();
        File[] candidateFiles = {
            new File(".env"),
            new File(System.getProperty("user.dir", "."), ".env")
        };

        for (File file : candidateFiles) {
            if (file.exists() && file.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (!line.isEmpty() && !line.startsWith("#") && line.contains("=")) {
                            int idx = line.indexOf('=');
                            String k = line.substring(0, idx).trim();
                            String v = line.substring(idx + 1).trim();
                            if ((v.startsWith("\"") && v.endsWith("\"")) || (v.startsWith("'") && v.endsWith("'"))) {
                                v = v.substring(1, v.length() - 1);
                            }
                            map.put(k, v);
                        }
                    }
                    break;
                } catch (Exception ignored) {
                }
            }
        }
        return map;
    }

    /**
     * Obtains a fresh database connection.
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Safely closes one or more database resources (Connection, Statement, ResultSet).
     */
    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception ignored) {
                    // Ignore exception during resource closing
                }
            }
        }
    }
}
