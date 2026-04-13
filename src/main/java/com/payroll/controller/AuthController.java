package com.payroll.controller;

import com.payroll.dao.DBConnection;
import org.springframework.web.bind.annotation.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for user authentication.
 * Follows RESTful principles.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Allow frontend access
public class AuthController {

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> response = new HashMap<>();
        String email = loginData.get("email");
        String password = loginData.get("password");
        String role = loginData.get("role");

        String query = "SELECT * FROM users WHERE email = ? AND password = ? AND role = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            
            pst.setString(1, email);
            pst.setString(2, password);
            pst.setString(3, role);
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                response.put("status", "success");
                response.put("name", rs.getString("name"));
                response.put("role", rs.getString("role"));
            } else {
                response.put("status", "error");
                response.put("message", "Invalid credentials or role mismatch.");
            }
        } catch (SQLException e) {
            response.put("status", "error");
            response.put("message", "Database error: " + e.getMessage());
        }
        
        return response;
    }
}
