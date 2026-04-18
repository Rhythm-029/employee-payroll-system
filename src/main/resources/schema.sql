-- 0. Compatibility and cleanup
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS password_reset_tokens;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS payroll;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. Authentication Table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'EMPLOYEE',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    profile_picture VARCHAR(255) DEFAULT 'https://ui-avatars.com/api/?background=random&color=fff',
    profile_image_data LONGBLOB, -- Store actual image data from local upload
    phone VARCHAR(20),
    address VARCHAR(255)
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
    shift_time VARCHAR(50) DEFAULT '09:00 AM - 06:00 PM',
    department VARCHAR(50) DEFAULT 'General',
    designation VARCHAR(50) DEFAULT 'Software Engineer',
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

-- 5. Password Reset Tokens
CREATE TABLE password_reset_tokens (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- SEED DATA - 1 Admin + 42 Employees
INSERT INTO users (name, email, password, role, status) VALUES 
('HR Administrator', 'admin@hr.com', 'admin123', 'ADMIN', 'ACTIVE');

-- 0. Nimesh Bumb (Mentor)
INSERT INTO users (name, email, password, role) VALUES ('Nimesh Bumb', 'nimesh@hr.com', 'nimesh', 'EMPLOYEE');
-- 1. Rhythm Singhal (firstname@hr.com / firstname)
INSERT INTO users (name, email, password, role) VALUES ('Rhythm Singhal', 'rhythm@hr.com', 'rhythm', 'EMPLOYEE');
-- 2. Yashwardhan Singh
INSERT INTO users (name, email, password, role) VALUES ('Yashwardhan Singh', 'yashwardhan@hr.com', 'yashwardhan', 'EMPLOYEE');
-- 3. Prathamesh Bhandare
INSERT INTO users (name, email, password, role) VALUES ('Prathamesh Bhandare', 'prathamesh@hr.com', 'prathamesh', 'EMPLOYEE');
-- 4. Anshika Gupta
INSERT INTO users (name, email, password, role) VALUES ('Anshika Gupta', 'anshika@hr.com', 'anshika', 'EMPLOYEE');
-- 5. Soha Patel
INSERT INTO users (name, email, password, role) VALUES ('Soha Patel', 'soha@hr.com', 'soha', 'EMPLOYEE');
-- 6. Hetanshi Vora
INSERT INTO users (name, email, password, role) VALUES ('Hetanshi Vora', 'hetanshi@hr.com', 'hetanshi', 'EMPLOYEE');
-- 7. Tanishka Shukla
INSERT INTO users (name, email, password, role) VALUES ('Tanishka Shukla', 'tanishka@hr.com', 'tanishka', 'EMPLOYEE');

-- Adding remaining 35 professional names
INSERT INTO users (name, email, password, role) VALUES 
('Aditya Verma', 'aditya@hr.com', 'aditya', 'EMPLOYEE'),
('Sneha Iyer', 'sneha@hr.com', 'sneha', 'EMPLOYEE'),
('Rohan Das', 'rohan@hr.com', 'rohan', 'EMPLOYEE'),
('Ishani Roy', 'ishani@hr.com', 'ishani', 'EMPLOYEE'),
('Kabir Malhotra', 'kabir@hr.com', 'kabir', 'EMPLOYEE'),
('Zoya Khan', 'zoya@hr.com', 'zoya', 'EMPLOYEE'),
('Aravind Nair', 'aravind@hr.com', 'aravind', 'EMPLOYEE'),
('Megha Bose', 'megha@hr.com', 'megha', 'EMPLOYEE'),
('Kunal Shah', 'kunal@hr.com', 'kunal', 'EMPLOYEE'),
('Tanya Mittal', 'tanya@hr.com', 'tanya', 'EMPLOYEE'),
('Arjun Saxena', 'arjun@hr.com', 'arjun', 'EMPLOYEE'),
('Nitya Reddy', 'nitya@hr.com', 'nitya', 'EMPLOYEE'),
('Siddharth Joshi', 'siddharth@hr.com', 'siddharth', 'EMPLOYEE'),
('Kavya Menon', 'kavya@hr.com', 'kavya', 'EMPLOYEE'),
('Varun Tripathi', 'varun@hr.com', 'varun', 'EMPLOYEE'),
('Shruti Mishra', 'shruti@hr.com', 'shruti', 'EMPLOYEE'),
('Manish Pandey', 'manish@hr.com', 'manish', 'EMPLOYEE'),
('Riya Sengupta', 'riya@hr.com', 'riya', 'EMPLOYEE'),
('Vikrant Singh', 'vikrant@hr.com', 'vikrant', 'EMPLOYEE'),
('Deepa Rao', 'deepa@hr.com', 'deepa', 'EMPLOYEE'),
('Sandeep Kumar', 'sandeep@hr.com', 'sandeep', 'EMPLOYEE'),
('Jyoti Sharma', 'jyoti@hr.com', 'jyoti', 'EMPLOYEE'),
('Rajesh Khanna', 'rajesh@hr.com', 'rajesh', 'EMPLOYEE'),
('Poonam Kaur', 'poonam@hr.com', 'poonam', 'EMPLOYEE'),
('Harsh Vardhan', 'harsh@hr.com', 'harsh', 'EMPLOYEE'),
('Simran Gill', 'simran@hr.com', 'simran', 'EMPLOYEE'),
('Abhishek Jain', 'abhishek@hr.com', 'abhishek', 'EMPLOYEE'),
('Neeta Gupta', 'neeta@hr.com', 'neeta', 'EMPLOYEE'),
('Pankaj Advani', 'pankaj@hr.com', 'pankaj', 'EMPLOYEE'),
('Shweta Singh', 'shweta@hr.com', 'shweta', 'EMPLOYEE'),
('Vivek Oberoi', 'vivek@hr.com', 'vivek', 'EMPLOYEE'),
('Anindita Paul', 'anindita@hr.com', 'anindita', 'EMPLOYEE'),
('Gaurav Chopra', 'gaurav@hr.com', 'gaurav', 'EMPLOYEE'),
('Preeti Zinta', 'preeti@hr.com', 'preeti', 'EMPLOYEE'),
('Sanjay Dutt', 'sanjay@hr.com', 'sanjay', 'EMPLOYEE');

-- Link all 42 users to employees table
INSERT INTO employees (user_id, type, base_salary, bonus, tax_percentage, department, designation)
SELECT id, 'FullTime', 55000 + (id * 500), 4500, 10.0, 
       CASE 
            WHEN name = 'Nimesh Bumb' THEN 'Management'
            WHEN name IN ('Rhythm Singhal', 'Prathamesh Bhandare', 'Yashwardhan Singh') THEN 'Executive'
            WHEN id % 5 = 0 THEN 'Engineering' WHEN id % 5 = 1 THEN 'Product' WHEN id % 5 = 2 THEN 'HR' WHEN id % 5 = 3 THEN 'Marketing' ELSE 'Finance' END,
       CASE 
            WHEN name = 'Nimesh Bumb' THEN 'Mentor'
            WHEN name IN ('Rhythm Singhal', 'Prathamesh Bhandare', 'Yashwardhan Singh') THEN 'Founder'
            WHEN id % 3 = 0 THEN 'Lead Specialist' WHEN id % 3 = 1 THEN 'Senior Associate' ELSE 'Process Manager' END
FROM users WHERE role = 'EMPLOYEE';
