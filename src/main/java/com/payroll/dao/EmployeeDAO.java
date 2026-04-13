package com.payroll.dao;

import com.payroll.model.Employee;
import com.payroll.model.FullTimeEmployee;
import com.payroll.model.PartTimeEmployee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Employee-related database operations.
 * Uses raw JDBC for CRUD.
 */
public class EmployeeDAO {
    
    /**
     * Fetch all employees joined with their user details.
     */
    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT e.*, u.name, u.email FROM employees e JOIN users u ON e.user_id = u.id";
        
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Employee emp;
                String type = rs.getString("type");
                if ("FullTime".equals(type)) {
                    emp = new FullTimeEmployee(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getDouble("base_salary"),
                        rs.getDouble("tax_percentage")
                    );
                } else {
                    emp = new PartTimeEmployee(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getDouble("base_salary"),
                        100.0, // Mock hourly rate
                        40      // Mock hours
                    );
                }
                emp.setBonus(rs.getDouble("bonus"));
                employees.add(emp);
            }
        }
        return employees;
    }

    /**
     * Add a new employee to the database (raw JDBC).
     */
    public void addEmployee(Employee emp) throws SQLException {
        String query = "INSERT INTO employees (user_id, type, base_salary, bonus) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            
            pst.setInt(1, 1); // Mock user_id for demonstration
            pst.setString(2, (emp instanceof FullTimeEmployee) ? "FullTime" : "PartTime");
            pst.setDouble(3, emp.getBaseSalary());
            pst.setDouble(4, emp.getBonus());
            pst.executeUpdate();
        }
    }
}
