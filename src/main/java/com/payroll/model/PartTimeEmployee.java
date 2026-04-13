package com.payroll.model;

/**
 * Concrete class for Part-time employees.
 * Demonstrates Inheritance and Polymorphism.
 */
public class PartTimeEmployee extends Employee {
    private double hourlyRate;
    private int hoursWorked;

    public PartTimeEmployee(int id, String name, String email, double baseSalary, double hourlyRate, int hoursWorked) {
        super(id, name, email, baseSalary);
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }

    // Encapsulation
    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public int getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(int hoursWorked) { this.hoursWorked = hoursWorked; }

    /**
     * Polymorphism: Specific implementation of salary calculation for PartTimeEmployee.
     */
    @Override
    public double calculateSalary() {
        return (hourlyRate * hoursWorked) + bonus;
    }
}
