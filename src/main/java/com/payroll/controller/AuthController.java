package com.payroll.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroll.dao.DBConnection;
import com.payroll.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.sql.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for user authentication and password reset workflows.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final EmailService emailService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.client-id:}")
    private String googleClientId;

    @Value("${app.reset-password-url:http://localhost:8080/reset-password.html}")
    private String passwordResetUrl;

    @Autowired
    public AuthController(EmailService emailService) {
        this.emailService = emailService;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

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
                response.put("email", rs.getString("email"));
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

    @PostMapping("/google")
    public Map<String, Object> googleLogin(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        String idToken = requestData.get("idToken");
        String selectedRole = requestData.get("role");

        if (idToken == null || idToken.isBlank()) {
            response.put("status", "error");
            response.put("message", "Google token is required.");
            return response;
        }

        if (selectedRole == null || selectedRole.isBlank()) {
            selectedRole = "EMPLOYEE";
        }

        try {
            String tokenInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            ResponseEntity<String> tokenResponse = restTemplate.getForEntity(tokenInfoUrl, String.class);

            if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                response.put("status", "error");
                response.put("message", "Unable to verify Google token.");
                return response;
            }

            JsonNode payload = objectMapper.readTree(tokenResponse.getBody());
            String audience = payload.path("aud").asText();
            String email = payload.path("email").asText();
            String name = payload.path("name").asText();

            if (googleClientId != null && !googleClientId.isBlank() && !googleClientId.equals("YOUR_GOOGLE_CLIENT_ID") && !googleClientId.equals(audience)) {
                response.put("status", "error");
                response.put("message", "Google client ID does not match configured application.");
                return response;
            }

            if (email == null || email.isBlank()) {
                response.put("status", "error");
                response.put("message", "Google response did not include an email address.");
                return response;
            }

            String selectUser = "SELECT * FROM users WHERE email = ?";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement pst = con.prepareStatement(selectUser)) {

                pst.setString(1, email);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    String existingRole = rs.getString("role");
                    if (!existingRole.equals(selectedRole)) {
                        response.put("status", "error");
                        response.put("message", "Role mismatch for this Google account.");
                        return response;
                    }

                    response.put("status", "success");
                    response.put("role", existingRole);
                    response.put("name", rs.getString("name"));
                    response.put("email", email);
                    return response;
                }

                if ("ADMIN".equals(selectedRole)) {
                    response.put("status", "error");
                    response.put("message", "Google login is not allowed for this admin account.");
                    return response;
                }

                String insertUser = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertPst = con.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                    insertPst.setString(1, name != null && !name.isBlank() ? name : email);
                    insertPst.setString(2, email);
                    insertPst.setString(3, UUID.randomUUID().toString());
                    insertPst.setString(4, "EMPLOYEE");
                    insertPst.executeUpdate();

                    ResultSet generatedKeys = insertPst.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        String insertEmployee = "INSERT INTO employees (user_id, type, base_salary, bonus, tax_percentage, leaves_taken, shift_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement insertEmp = con.prepareStatement(insertEmployee)) {
                            insertEmp.setInt(1, userId);
                            insertEmp.setString(2, "FullTime");
                            insertEmp.setDouble(3, 50000.0);
                            insertEmp.setDouble(4, 0.0);
                            insertEmp.setDouble(5, 10.0);
                            insertEmp.setInt(6, 0);
                            insertEmp.setString(7, "09:00 AM - 06:00 PM");
                            insertEmp.executeUpdate();
                        }
                    }
                }

                response.put("status", "success");
                response.put("role", "EMPLOYEE");
                response.put("name", name != null && !name.isBlank() ? name : email);
                response.put("email", email);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Google login failed: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/forgot-password")
    public Map<String, Object> forgotPassword(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        String email = requestData.get("email");
        String role = requestData.get("role");

        if (email == null || email.isBlank()) {
            response.put("status", "error");
            response.put("message", "Email is required.");
            return response;
        }

        String selectQuery = "SELECT id, name FROM users WHERE email = ? AND role = ?";
        String insertToken = "INSERT INTO password_reset_tokens (token, user_id, expires_at, used) VALUES (?, ?, ?, false)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement selectPst = con.prepareStatement(selectQuery)) {

            selectPst.setString(1, email);
            selectPst.setString(2, role != null && !role.isBlank() ? role : "EMPLOYEE");
            ResultSet rs = selectPst.executeQuery();

            if (!rs.next()) {
                response.put("status", "error");
                response.put("message", "No user found for the provided email and role.");
                return response;
            }

            int userId = rs.getInt("id");
            String userName = rs.getString("name");
            String token = UUID.randomUUID().toString();
            Timestamp expiry = Timestamp.from(Instant.now().plusSeconds(1800));
            String resetLink = passwordResetUrl + "?token=" + token;

            try (PreparedStatement insertPst = con.prepareStatement(insertToken)) {
                insertPst.setString(1, token);
                insertPst.setInt(2, userId);
                insertPst.setTimestamp(3, expiry);
                insertPst.executeUpdate();
            }

            emailService.sendPasswordResetEmail(email, userName, resetLink);
            response.put("status", "success");
            response.put("message", "Password reset instructions were sent to your email.");
        } catch (SQLException e) {
            response.put("status", "error");
            response.put("message", "Database error: " + e.getMessage());
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to send reset email: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        String token = requestData.get("token");
        String newPassword = requestData.get("newPassword");

        if (token == null || token.isBlank() || newPassword == null || newPassword.isBlank()) {
            response.put("status", "error");
            response.put("message", "Token and new password are required.");
            return response;
        }

        String selectToken = "SELECT t.user_id, t.expires_at, t.used FROM password_reset_tokens t WHERE t.token = ?";
        String updatePassword = "UPDATE users SET password = ? WHERE id = ?";
        String useToken = "UPDATE password_reset_tokens SET used = true WHERE token = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement selectPst = con.prepareStatement(selectToken)) {

            selectPst.setString(1, token);
            ResultSet rs = selectPst.executeQuery();

            if (!rs.next()) {
                response.put("status", "error");
                response.put("message", "Invalid or expired reset token.");
                return response;
            }

            boolean used = rs.getBoolean("used");
            Timestamp expiresAt = rs.getTimestamp("expires_at");
            int userId = rs.getInt("user_id");

            if (used || expiresAt.before(Timestamp.from(Instant.now()))) {
                response.put("status", "error");
                response.put("message", "The password reset link is no longer valid.");
                return response;
            }

            try (PreparedStatement updatePst = con.prepareStatement(updatePassword)) {
                updatePst.setString(1, newPassword);
                updatePst.setInt(2, userId);
                updatePst.executeUpdate();
            }

            try (PreparedStatement usePst = con.prepareStatement(useToken)) {
                usePst.setString(1, token);
                usePst.executeUpdate();
            }

            response.put("status", "success");
            response.put("message", "Your password has been updated successfully.");
        } catch (SQLException e) {
            response.put("status", "error");
            response.put("message", "Database error: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/update-password")
    public Map<String, Object> updatePassword(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        String email = requestData.get("email");
        String oldPassword = requestData.get("oldPassword");
        String newPassword = requestData.get("newPassword");

        if (email == null || email.isBlank() || oldPassword == null || oldPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            response.put("status", "error");
            response.put("message", "Email, current password and new password are required.");
            return response;
        }

        String selectQuery = "SELECT id FROM users WHERE email = ? AND password = ?";
        String updateQuery = "UPDATE users SET password = ? WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement selectPst = con.prepareStatement(selectQuery)) {

            selectPst.setString(1, email);
            selectPst.setString(2, oldPassword);
            ResultSet rs = selectPst.executeQuery();

            if (!rs.next()) {
                response.put("status", "error");
                response.put("message", "Current password is incorrect.");
                return response;
            }

            try (PreparedStatement updatePst = con.prepareStatement(updateQuery)) {
                updatePst.setString(1, newPassword);
                updatePst.setString(2, email);
                updatePst.executeUpdate();
            }

            response.put("status", "success");
            response.put("message", "Password updated successfully.");
        } catch (SQLException e) {
            response.put("status", "error");
            response.put("message", "Database error: " + e.getMessage());
        }

        return response;
    }
}
