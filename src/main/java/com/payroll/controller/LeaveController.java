package com.payroll.controller;

import com.payroll.dao.LeaveDAO;
import com.payroll.model.LeaveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/management/leaves")
public class LeaveController {

    @Autowired
    private LeaveDAO leaveDAO;

    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAllLeaves() {
        return ResponseEntity.ok(leaveDAO.getAllLeaves());
    }

    @GetMapping("/mine/{userId}")
    public ResponseEntity<List<LeaveRequest>> getMyLeaves(@PathVariable int userId) {
        return ResponseEntity.ok(leaveDAO.getLeavesByUserId(userId));
    }

    @PostMapping("/apply/{userId}")
    public ResponseEntity<?> applyLeave(@PathVariable int userId, @RequestBody LeaveRequest request) {
        try {
            leaveDAO.applyLeave(request, userId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Leave applied successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PatchMapping("/{leaveId}/status")
    public ResponseEntity<?> updateLeaveStatus(@PathVariable int leaveId, @RequestBody Map<String, String> body) {
        try {
            String status = body.get("status");
            leaveDAO.updateLeaveStatus(leaveId, status);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Leave status updated to " + status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
