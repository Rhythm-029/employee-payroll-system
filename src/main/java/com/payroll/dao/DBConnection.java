package com.payroll.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Helper class to manage database connections using JDBC.
 */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/payrolldb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "Yash";
    private static final String PASS = "Yash@123";

    /**
     * Get a connection to the database.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Register JDBC driver for MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found: " + e.getMessage());
        }
    }
}
