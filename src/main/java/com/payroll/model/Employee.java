package com.payroll.model;

/**
 * Abstract class representing a generic Employee.
 * Demonstrates Abstraction and Encapsulation.
 */
public abstract class Employee implements Payable {
    private int id;
    private String name;
    private String email;
    private int userId;
    private String status;
    private String phone;
    private String address;
    private String profilePicture;
    private String department;
    private String designation;
    private String shiftTime;
    protected double baseSalary;
    protected double bonus;
    protected double taxPercentage;

    public Employee(int id, String name, String email, double baseSalary) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.baseSalary = baseSalary;
        this.status = "ACTIVE"; // Default
        this.taxPercentage = 10.0; // Default
    }

    // Encapsulation: standard getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getBaseSalary() { return baseSalary; }
    public void setBaseSalary(double baseSalary) { this.baseSalary = baseSalary; }

    public double getBonus() { return bonus; }
    public void setBonus(double bonus) { this.bonus = bonus; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(double tax) { this.taxPercentage = tax; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getShiftTime() { return shiftTime; }
    public void setShiftTime(String shiftTime) { this.shiftTime = shiftTime; }

    public double getTaxAmount() { return (baseSalary + bonus) * (taxPercentage / 100); }

    // Abstract method to be overridden by subclasses (Polymorphism)
    @Override
    public abstract double calculateSalary();
}
