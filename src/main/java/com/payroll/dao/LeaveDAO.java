package com.payroll.dao;

import com.payroll.model.LeaveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LeaveDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LeaveDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<LeaveRequest> leaveRequestRowMapper = (rs, rowNum) -> {
        return LeaveRequest.builder()
                .id(rs.getInt("id"))
                .employeeId(rs.getInt("employee_id"))
                .employeeName(rs.getString("employee_name"))
                .startDate(rs.getDate("start_date").toLocalDate())
                .endDate(rs.getDate("end_date").toLocalDate())
                .leaveType(rs.getString("leave_type"))
                .status(rs.getString("status"))
                .reason(rs.getString("reason"))
                .build();
    };

    public List<LeaveRequest> getAllLeaves() {
        String query = "SELECT lr.*, u.name as employee_name FROM leave_requests lr " +
                       "JOIN employees e ON lr.employee_id = e.id " +
                       "JOIN users u ON e.user_id = u.id ORDER BY lr.start_date DESC";
        return jdbcTemplate.query(query, leaveRequestRowMapper);
    }

    public List<LeaveRequest> getLeavesByUserId(int userId) {
        String query = "SELECT lr.*, u.name as employee_name FROM leave_requests lr " +
                       "JOIN employees e ON lr.employee_id = e.id " +
                       "JOIN users u ON e.user_id = u.id WHERE u.id = ? ORDER BY lr.start_date DESC";
        return jdbcTemplate.query(query, leaveRequestRowMapper, userId);
    }

    public void applyLeave(LeaveRequest request, int userId) {
        String findEmployeeQuery = "SELECT id FROM employees WHERE user_id = ?";
        Integer employeeId = jdbcTemplate.queryForObject(findEmployeeQuery, Integer.class, userId);

        String insertQuery = "INSERT INTO leave_requests (employee_id, start_date, end_date, leave_type, status, reason) " +
                             "VALUES (?, ?, ?, ?, 'PENDING', ?)";
        jdbcTemplate.update(insertQuery, employeeId, request.getStartDate(), request.getEndDate(), request.getLeaveType(), request.getReason());
    }

    public void updateLeaveStatus(int leaveId, String status) {
        String query = "UPDATE leave_requests SET status = ? WHERE id = ?";
        jdbcTemplate.update(query, status, leaveId);
    }
}
