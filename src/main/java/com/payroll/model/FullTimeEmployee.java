package com.payroll.model;

/**
 * Concrete class for Full-time employees.
 * Demonstrates Inheritance and Polymorphism.
 */
public class FullTimeEmployee extends Employee {
    private double taxPercentage;

    public FullTimeEmployee(int id, String name, String email, double baseSalary, double taxPercentage) {
        super(id, name, email, baseSalary);
        this.taxPercentage = taxPercentage;
    }

    // Encapsulation
    public double getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(double taxPercentage) { this.taxPercentage = taxPercentage; }

    /**
     * Polymorphism: Specific implementation of salary calculation for FullTimeEmployee.
     */
    @Override
    public double calculateSalary() {
        double taxAmount = (baseSalary + bonus) * (taxPercentage / 100);
        return (baseSalary + bonus) - taxAmount;
    }
}
