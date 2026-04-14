SET FOREIGN_KEY_CHECKS = 0;

-- Drop tables if they exist to allow clean re-runs
DROP TABLE IF EXISTS password_reset_tokens;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS payroll;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS users;

-- 1. Authentication Table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'EMPLOYEE'
);

-- 2. Employee Details Table
CREATE TABLE employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    base_salary DOUBLE NOT NULL,
    bonus DOUBLE DEFAULT 0.0,
    tax_percentage DOUBLE DEFAULT 10.0,
    leaves_taken INT DEFAULT 0,
    shift_time VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Payroll Records Table
CREATE TABLE payroll (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    month_name VARCHAR(20) NOT NULL,
    final_salary DOUBLE NOT NULL,
    generated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- 4. Attendance Table
CREATE TABLE attendance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'Present',
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- SEED DATA for initial login
-- Password is 'admin123' and 'emp123'
INSERT INTO users (name, email, password, role) VALUES 
('HR Administrator', 'admin@hr.com', 'admin123', 'ADMIN'),
('Rhythm Singhal', 'emp@hr.com', 'emp123', 'EMPLOYEE');

-- Link users to employees (Rhythm Singhal is EMP-1)
INSERT INTO employees (user_id, type, base_salary, bonus, tax_percentage, leaves_taken, shift_time) 
VALUES (2, 'FullTime', 85000.0, 5000.0, 10.0, 2, '09:00 AM - 06:00 PM');

-- Password reset tokens for email-based reset workflow
CREATE TABLE password_reset_tokens (
    id INT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);