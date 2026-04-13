package com.payroll.model;

/**
 * Abstract class representing a generic Employee.
 * Demonstrates Abstraction and Encapsulation.
 */
public abstract class Employee implements Payable {
    private int id;
    private String name;
    private String email;
    protected double baseSalary;
    protected double bonus;

    public Employee(int id, String name, String email, double baseSalary) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.baseSalary = baseSalary;
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

    // Abstract method to be overridden by subclasses (Polymorphism)
    @Override
    public abstract double calculateSalary();
}
