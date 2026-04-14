package com.payroll.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling user authentication via real database records.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AuthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> response = new HashMap<>();
        String email = loginData.get("email");
        String password = loginData.get("password");
        String selectedRole = loginData.get("role");

        // Real-time authentication via JdbcTemplate
        String query = "SELECT id, name, role, status FROM users WHERE email = ? AND password = ?";

        try {
            Map<String, Object> user = jdbcTemplate.queryForMap(query, email, password);

            if (user != null) {
                String dbRole = (String) user.get("role");
                String status = (String) user.get("status");

                // Check for Suspension
                if ("SUSPENDED".equals(status)) {
                    response.put("status", "error");
                    response.put("message", "Your account is suspended. Please contact HR.");
                    return response;
                }

                // Check for Role Mismatch
                if (!dbRole.equals(selectedRole)) {
                    response.put("status", "error");
                    response.put("message", "Incorrect portal selected for this account.");
                    return response;
                }

                response.put("status", "success");
                response.put("userId", user.get("id"));
                response.put("name", user.get("name"));
                response.put("role", dbRole);
                response.put("email", email);

            }
        } catch (Exception e) {
            // Usually EmptyResultDataAccessException if not found
            response.put("status", "error");
            response.put("message", "Invalid email or password.");
        }

        return response;
    }

    @PostMapping("/logout")
    public Map<String, String> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Logged out successfully.");
        return response;
    }
}

