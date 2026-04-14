package com.payroll.controller;

import com.payroll.dao.EmployeeDAO;
import com.payroll.model.Employee;
import com.payroll.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Employee and Payroll management.
 */
@RestController
@RequestMapping("/api/management")
@CrossOrigin(origins = "*")
public class PayrollController {
    
    private final EmployeeDAO employeeDAO;
    private final PDFService pdfService;

    @Autowired
    public PayrollController(EmployeeDAO employeeDAO, PDFService pdfService) {
        this.employeeDAO = employeeDAO;
        this.pdfService = pdfService;
    }

    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        return employeeDAO.getAllEmployees();
    }

    @PostMapping("/employees")
    public ResponseEntity<Map<String, String>> addEmployee(@RequestBody Map<String, Object> data) {
        Map<String, String> response = new HashMap<>();
        try {
            // Map JSON to Employee Object
            Employee emp = new com.payroll.model.FullTimeEmployee(0, 
                (String) data.get("name"), 
                (String) data.get("email"), 
                Double.parseDouble(data.get("salary").toString()), 
                10.0);
            
            String password = data.getOrDefault("password", "emp123").toString();
            employeeDAO.addEmployeeWithUser(emp, password);
            
            response.put("status", "success");
            response.put("message", "Employee created successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<Map<String, String>> updateEmployee(@PathVariable int id, @RequestBody Map<String, Object> data) {
        Map<String, String> response = new HashMap<>();
        try {
            employeeDAO.updateEmployeeDetails(id, 
                Double.parseDouble(data.get("baseSalary").toString()), 
                Double.parseDouble(data.get("bonus").toString()), 
                Double.parseDouble(data.get("taxPercentage").toString()),
                (String) data.get("department"),
                (String) data.get("designation")
            );
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PatchMapping("/employees/status/{userId}")
    public ResponseEntity<Map<String, String>> updateStatus(@PathVariable int userId, @RequestBody Map<String, String> data) {
        Map<String, String> response = new HashMap<>();
        try {
            employeeDAO.updateStatus(userId, data.get("status"));
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Map<String, String>> deleteEmployee(@PathVariable int id) {
        try {
            employeeDAO.deleteEmployee(id);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/employees/profile/{userId}")
    public Employee getProfile(@PathVariable int userId) {
        return employeeDAO.getEmployeeByUserId(userId);
    }

    @PutMapping("/employees/profile")
    public ResponseEntity<Map<String, String>> updateProfile(@RequestBody Map<String, Object> data) {
        try {
            int userId = Integer.parseInt(data.get("userId").toString());
            employeeDAO.updateProfile(userId, 
                (String) data.get("name"), 
                (String) data.get("phone"), 
                (String) data.get("address"), 
                (String) data.get("profilePicture"));
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return employeeDAO.getStaffSummary();
    }

    @PostMapping("/employees/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("userId") int userId, @RequestParam("file") MultipartFile file) {
        try {
            employeeDAO.updateProfileImage(userId, file.getBytes());
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/employees/profile-image/{userId}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable int userId) {
        byte[] image = employeeDAO.getProfileImage(userId);
        if (image == null || image.length == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(image);
    }

    @GetMapping("/employees/export")
    public ResponseEntity<byte[]> exportEmployees() {
        try {
            List<Employee> list = employeeDAO.getAllEmployees();
            StringBuilder csv = new StringBuilder("ID,Name,Email,Department,Designation,Base Salary,Bonus\n");
            for (Employee e : list) {
                csv.append(String.format("PAY-%d,\"%s\",%s,%s,%s,%.2f,%.2f\n", 
                    e.getId(), e.getName(), e.getEmail(), e.getDepartment(), e.getDesignation(), e.getBaseSalary(), e.getBonus()));
            }
            byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "EmployeeList.csv");
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/payroll/{empId}/slip")
    public ResponseEntity<byte[]> downloadSlip(@PathVariable int empId) {
        try {
            // Fetch employee data
            List<Employee> all = employeeDAO.getAllEmployees();
            Employee emp = all.stream().filter(e -> e.getId() == empId).findFirst().orElse(null);
            
            if (emp == null) return ResponseEntity.notFound().build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", emp.getName());
            data.put("id", "PAY-" + emp.getId());
            data.put("month", LocalDate.now().getMonth().name() + " " + LocalDate.now().getYear());
            data.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            data.put("baseSalary", emp.getBaseSalary());
            data.put("bonus", emp.getBonus());
            data.put("taxAmount", emp.getTaxAmount());
            data.put("finalSalary", emp.calculateSalary());

            byte[] pdfBytes = pdfService.generateSalarySlip(data);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "SalarySlip_" + emp.getName() + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

