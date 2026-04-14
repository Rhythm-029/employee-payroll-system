package com.payroll.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Data Access Object for Payroll operations.
 */
@Repository
public class PayrollDAO {
    
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PayrollDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Generate a payroll record for an employee.
     */
    public void generatePayroll(int employeeId, String month, double finalSalary) {
        String query = "INSERT INTO payroll (employee_id, month_name, final_salary) VALUES (?, ?, ?)";
        jdbcTemplate.update(query, employeeId, month, finalSalary);
    }
}

