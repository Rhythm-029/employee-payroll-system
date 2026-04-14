package com.payroll.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Helper class to manage database connections using JDBC.
 */
public class DBConnection {
    // Database credentials
    private static final String URL = "jdbc:h2:mem:payrolldb;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_UPPER=false";
    private static final String USER = "sa";
    private static final String PASS = "";

    /**
     * Get a connection to the database.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Register JDBC driver for embedded H2
            Class.forName("org.h2.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found: " + e.getMessage());
        }
    }
}
