package com.inventory.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class to manage MySQL and TiDB Cloud JDBC database connections.
 * Securely loads credentials from environment variables, system properties, or a local .env file.
 * Supports TLS certificate verification for TiDB Cloud and enforces strict security in production.
 */
public class DBConnection {

    private static final String DEFAULT_LOCAL_URL =
            "jdbc:mysql://localhost:3306/inventory_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true";
    private static final String DEFAULT_LOCAL_USER = "root";

    private static final Map<String, String> dotEnv = loadDotEnv();

    static {
        try {
            // Load MySQL 8+ / TiDB compatible JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL: MySQL JDBC Driver not found in classpath!");
            e.printStackTrace();
        }
    }

    /**
     * Checks if the application is running in a production cloud environment (e.g. Render).
     */
    public static boolean isProductionEnvironment() {
        if (System.getenv("RENDER") != null) return true;
        if (System.getenv("DYNO") != null) return true;
        String env = System.getenv("ENVIRONMENT");
        if (env != null && env.equalsIgnoreCase("production")) return true;
        String appEnv = System.getenv("APP_ENV");
        if (appEnv != null && appEnv.equalsIgnoreCase("production")) return true;
        return false;
    }

    /**
     * Resolves configuration key from:
     * 1. OS Environment Variables (DB_URL, DB_USER, DB_PASSWORD, etc.)
     * 2. JVM System Properties (-DDB_URL=...)
     * 3. Local .env file
     */
    private static String getEnvValue(String key) {
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
        return null;
    }

    /**
     * Parses local .env file if available (for local development without committing secrets).
     */
    private static Map<String, String> loadDotEnv() {
        Map<String, String> map = new HashMap<>();
        File[] candidateFiles = {
            new File(".env"),
            new File(System.getProperty("user.dir", "."), ".env"),
            new File(System.getProperty("catalina.base", "."), ".env"),
            new File("D:/Inventory Management System/.env"),
            new File("D:\\Inventory Management System\\.env")
        };

        for (File file : candidateFiles) {
            if (file != null && file.exists() && file.isFile()) {
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
     * Resolves the database connection URL.
     * Supports standard JDBC URLs, URI-style DATABASE_URL, and automatically
     * secures TiDB Cloud connections with TLS certificate verification.
     */
    public static String getEffectiveUrl() throws SQLException {
        String url = getEnvValue("DB_URL");
        if (url == null || url.trim().isEmpty()) {
            url = getEnvValue("DATABASE_URL");
        }

        boolean isProd = isProductionEnvironment();

        if (url == null || url.trim().isEmpty()) {
            if (isProd) {
                throw new SQLException("CRITICAL: Production deployment detected, but DB_URL is not configured! " +
                        "Please set DB_URL in your Render environment variables.");
            }
            url = DEFAULT_LOCAL_URL;
        }

        // Convert standard URI (e.g. mysql://...) to JDBC format (jdbc:mysql://...)
        if (url.startsWith("mysql://")) {
            try {
                URI uri = new URI(url);
                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : 3306;
                String path = uri.getPath();
                String query = uri.getQuery();
                url = "jdbc:mysql://" + host + ":" + port + (path != null ? path : "/inventory_management") +
                        (query != null ? "?" + query : "");
            } catch (Exception e) {
                url = "jdbc:" + url;
            }
        } else if (!url.startsWith("jdbc:mysql://")) {
            url = "jdbc:mysql://" + url;
        }

        // Check if connecting to TiDB Cloud or remote database
        boolean isTiDB = url.toLowerCase().contains("tidbcloud.com") || url.contains(":4000");

        if (isTiDB) {
            // Enforce secure TLS certificate verification for TiDB Cloud
            if (!url.toLowerCase().contains("sslmode=")) {
                String separator = url.contains("?") ? "&" : "?";
                url = url + separator + "sslMode=VERIFY_IDENTITY&enabledTLSProtocols=TLSv1.2,TLSv1.3";
            }
        }

        // Guard against silently falling back to localhost in production
        if (isProd && (url.toLowerCase().contains("localhost") || url.contains("127.0.0.1"))) {
            throw new SQLException("CRITICAL: Production environment detected on Render, but DB_URL points to localhost! " +
                    "Please configure your TiDB Cloud database connection string in Render environment variables.");
        }

        return url;
    }

    /**
     * Resolves database username.
     */
    public static String getEffectiveUsername() throws SQLException {
        String user = getEnvValue("DB_USER");
        if (user == null || user.trim().isEmpty()) {
            user = getEnvValue("DATABASE_USER");
        }
        if (user == null || user.trim().isEmpty()) {
            user = getEnvValue("TIDB_USER");
        }

        if (user == null || user.trim().isEmpty()) {
            if (isProductionEnvironment()) {
                throw new SQLException("CRITICAL: Production environment detected, but DB_USER is not configured in Render environment variables!");
            }
            return DEFAULT_LOCAL_USER;
        }
        return user.trim();
    }

    /**
     * Resolves database password. Never falls back to default empty password in production.
     */
    public static String getEffectivePassword() throws SQLException {
        String pass = getEnvValue("DB_PASSWORD");
        if (pass == null) {
            pass = getEnvValue("DATABASE_PASSWORD");
        }
        if (pass == null) {
            pass = getEnvValue("TIDB_PASSWORD");
        }

        if (pass == null || pass.trim().isEmpty()) {
            if (isProductionEnvironment()) {
                throw new SQLException("CRITICAL: Production environment detected, but DB_PASSWORD is not configured in Render environment variables!");
            }
            // For local development, check if password was specified in .env
            return pass != null ? pass : "";
        }
        return pass;
    }

    /**
     * Obtains a fresh database connection.
     * Enforces TiDB Cloud TLS certificate verification and secure credential passing.
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        String url = getEffectiveUrl();
        String user = getEffectiveUsername();
        String password = getEffectivePassword();

        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);

        // Explicitly set TLS configuration for TiDB Cloud
        if (url.toLowerCase().contains("tidbcloud.com") || url.contains(":4000")) {
            props.setProperty("sslMode", "VERIFY_IDENTITY");
            props.setProperty("enabledTLSProtocols", "TLSv1.2,TLSv1.3");
        }

        return DriverManager.getConnection(url, props);
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
