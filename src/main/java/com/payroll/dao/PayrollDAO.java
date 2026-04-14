package com.payroll.dao;

import com.payroll.model.Employee;
import java.sql.*;

/**
 * Data Access Object for Payroll operations.
 */
public class PayrollDAO {
    
    /**
     * Generate a payroll record for an employee.
     */
    public void generatePayroll(int employeeId, String month, double finalSalary) throws SQLException {
        String query = "INSERT INTO payroll (employee_id, month_name, final_salary) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            
            pst.setInt(1, employeeId);
            pst.setString(2, month);
            pst.setDouble(3, finalSalary);
            pst.executeUpdate();
        }
    }
}
