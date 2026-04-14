package com.payroll.dao;

import com.payroll.model.Employee;
import com.payroll.model.FullTimeEmployee;
import com.payroll.model.PartTimeEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * Data Access Object (DAO) for Employee-related database operations.
 * Uses Spring JdbcTemplate for robust DB interaction.
 */
@Repository
public class EmployeeDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EmployeeDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Employee> employeeRowMapper = (rs, rowNum) -> {
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
                rs.getDouble("base_salary"), 
                40 
            );
        }
        emp.setBonus(rs.getDouble("bonus"));
        emp.setUserId(rs.getInt("user_id"));
        emp.setStatus(rs.getString("status"));
        emp.setPhone(rs.getString("phone"));
        emp.setAddress(rs.getString("address"));
        emp.setProfilePicture(rs.getString("profile_picture"));
        emp.setDepartment(rs.getString("department"));
        emp.setDesignation(rs.getString("designation"));
        emp.setShiftTime(rs.getString("shift_time"));
        return emp;
    };

    public List<Employee> getAllEmployees() {
        String query = "SELECT e.*, u.name, u.email, u.role, u.status, u.phone, u.address, u.profile_picture FROM employees e JOIN users u ON e.user_id = u.id";
        return jdbcTemplate.query(query, employeeRowMapper);
    }

    public void addEmployeeWithUser(Employee emp, String password) {
        String userQuery = "INSERT INTO users (name, email, password, role, status, profile_picture) VALUES (?, ?, ?, 'EMPLOYEE', 'ACTIVE', ?)";
        String empQuery = "INSERT INTO employees (user_id, type, base_salary, bonus, tax_percentage, department, designation) VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Auto-generate credentials if not provided
        String firstName = emp.getName().split(" ")[0].toLowerCase();
        String finalEmail = (emp.getEmail() == null || emp.getEmail().isEmpty()) ? firstName + "@hr.com" : emp.getEmail();
        String finalPass = (password == null || password.isEmpty()) ? firstName : password;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, emp.getName());
            ps.setString(2, finalEmail);
            ps.setString(3, finalPass);
            ps.setString(4, "https://ui-avatars.com/api/?name=" + emp.getName().replace(" ", "+") + "&background=random");
            return ps;
        }, keyHolder);

        int userId = keyHolder.getKey().intValue();

        jdbcTemplate.update(empQuery,
            userId,
            (emp instanceof FullTimeEmployee) ? "FullTime" : "PartTime",
            emp.getBaseSalary(),
            emp.getBonus(),
            emp.getTaxPercentage(),
            emp.getDepartment() != null ? emp.getDepartment() : "General",
            emp.getDesignation() != null ? emp.getDesignation() : "Software Engineer"
        );
    }

    public void updateSalary(int empId, double base, double bonus, double tax) {
        String query = "UPDATE employees SET base_salary = ?, bonus = ?, tax_percentage = ? WHERE id = ?";
        jdbcTemplate.update(query, base, bonus, tax, empId);
    }

    public void updateEmployeeDetails(int empId, double base, double bonus, double tax, String dept, String desig) {
        String query = "UPDATE employees SET base_salary = ?, bonus = ?, tax_percentage = ?, department = ?, designation = ? WHERE id = ?";
        jdbcTemplate.update(query, base, bonus, tax, dept, desig, empId);
    }

    public void updateStatus(int userId, String status) {
        String query = "UPDATE users SET status = ? WHERE id = ?";
        jdbcTemplate.update(query, status, userId);
    }

    public void updateProfile(int userId, String name, String phone, String address, String profilePicture) {
        String query = "UPDATE users SET name = ?, phone = ?, address = ?, profile_picture = ? WHERE id = ?";
        jdbcTemplate.update(query, name, phone, address, profilePicture, userId);
    }

    public void updateProfileImage(int userId, byte[] imageData) {
        String query = "UPDATE users SET profile_image_data = ? WHERE id = ?";
        jdbcTemplate.update(query, imageData, userId);
    }

    public byte[] getProfileImage(int userId) {
        String query = "SELECT profile_image_data FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(query, byte[].class, userId);
        } catch (Exception e) {
            return null;
        }
    }

    public java.util.Map<String, Object> getStaffSummary() {
        String query = "SELECT COUNT(*) as total, SUM(e.base_salary + e.bonus) as total_budget FROM employees e JOIN users u ON e.user_id = u.id WHERE u.status = 'ACTIVE'";
        return jdbcTemplate.queryForMap(query);
    }

    public Employee getEmployeeByUserId(int userId) {
        String query = "SELECT e.*, u.name, u.email, u.role, u.status, u.phone, u.address, u.profile_picture FROM employees e JOIN users u ON e.user_id = u.id WHERE u.id = ?";
        List<Employee> results = jdbcTemplate.query(query, employeeRowMapper, userId);
        return results.isEmpty() ? null : results.get(0);
    }

    public void deleteEmployee(int empId) {
        // Query user_id first
        String getUserSql = "SELECT user_id FROM employees WHERE id = ?";
        Integer userId = jdbcTemplate.queryForObject(getUserSql, Integer.class, empId);
        
        if (userId != null) {
            // Deleting user will cascade delete employee due to ON DELETE CASCADE in schema
            String deleteUserSql = "DELETE FROM users WHERE id = ?";
            jdbcTemplate.update(deleteUserSql, userId);
        }
    }
}

