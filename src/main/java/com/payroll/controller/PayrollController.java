package com.payroll.controller;

import com.payroll.dao.EmployeeDAO;
import com.payroll.model.Employee;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.List;

/**
 * REST Controller for Employee and Payroll management.
 * Follows layered architecture Controller -> Service -> DAO.
 */
@RestController
@RequestMapping("/api/management")
@CrossOrigin(origins = "*")
public class PayrollController {
    
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    /**
     * Get all employees (REST Endpoint).
     */
    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        try {
            return employeeDAO.getAllEmployees();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Add employee (REST Endpoint).
     */
    @PostMapping("/employees")
    public String addEmployee(@RequestBody Employee employee) {
        try {
            employeeDAO.addEmployee(employee);
            return "Employee added successfully!";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }
}
