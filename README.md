<div align="center">
  <h1>💼 Employee Payroll System</h1>
  <p>A comprehensive, robust, and full-stack enterprise payroll management solution.</p>

  [![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
  [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
  [![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
  [![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
</div>

<hr/>

## 📖 Overview

The **Employee Payroll System** is a full-stack web application designed to streamline the management of employee salaries, attendance, and administrative tasks. Built with modern enterprise technologies, it offers a secure, scalable, and user-friendly interface suited for HR administrators and employees alike. 

This project aims to automate core HR workflows by providing robust capabilities such as automated salary calculation, secure PDF payslip generation, attendance tracking, and dynamic role-based access.

---

## ✨ Key Features

### 🛡️ Role-Based Access Control (RBAC)
- **Admin Role**: Complete access to the HR dashboard to manage employees, payroll, and view organization-wide metrics.
- **Employee Role**: Self-service portal to download payslips, check attendance, and update profile information.

### 💰 Automated Payroll & Tax Calculations
Ensure accurate processing of employee compensation including:
- Base salary management
- Dynamically calculated bonuses
- Tax deductions based on individual tax brackets
- Leave deductions from accurate attendance fetching 

### 🖨️ Secure PDF Salary Slips
Automatically compute payroll outbounds and generate detailed, downloadable PDF salary slips utilizing **OpenPDF**. Employees can instantly download and view an official record of their monthly compensation.

### 🔑 Advanced Authentication & Security
- Secure session management and password hashing.
- **Password Reset Mechanism**: Fully functional reset flow integrating **Spring Boot Mail** for zero-trust password recovery.

### 📊 Comprehensive Attendance Tracking
Monitor presence and absenteeism through a daily attendance register, effectively linking it directly to the payroll logic.

---

## 🛠️ Technology Stack

| Component | Technology | Description |
|-----------|-----------|-------------|
| **Backend Framework** | Spring Boot 3.1.5 | Handles REST APIs, security, and business layer |
| **Language** | Java 17 | Core programming language |
| **Database** | MySQL & H2 (In-Memory) | Relational database modeling using JDBC |
| **Frontend UI** | HTML5, CSS3, ES6 JS | Clean, vanilla client-side implementation |
| **PDF Generation** | OpenPDF (1.3.30) | Creating high-quality dynamic salary slips |
| **Email Service** | Spring Boot Started Mail| Mailing configurations for system notices |
| **Tooling** | Maven & Lombok | Dependency management and boilerplate reduction|

---

## 🚀 Getting Started

### Prerequisites
Before running the application, ensure you have the following installed:
- [Java Development Kit (JDK) 17+](https://www.oracle.com/java/technologies/downloads/)
- [Maven 3.8+](https://maven.apache.org/download.cgi)
- [MySQL Server 8.0+](https://dev.mysql.com/downloads/mysql/) (Optional, runs on embedded H2 out of the box)

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/Employee-Payroll.git
   cd Employee-Payroll/employee-payroll-system
   ```

2. **Database Configuration**
   By default, the application is shipped with zero-configuration **H2 Database**. However, if you wish to use **MySQL**, update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/payroll_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

3. **Mail Configuration**
   To leverage the password reset service, configure your SMTP properties in `application.properties`:
   ```properties
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   ```

4. **Build and Run**
   Navigate to the project root and execute the Maven wrapper:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Access the application:**
   Open your browser and navigate to `http://localhost:8080/`.

---

## 🔒 Default Credentials

The database is pre-seeded with sample metadata. Use the following to access the platform:

**Admin Account**
- **Email**: `admin@hr.com`
- **Password**: `admin123`

**Sample Employee Account**
- **Email**: `prathamesh@hr.com`
- **Password**: `prathamesh`
*(or refer to `schema.sql` for 40+ other dummy active users!)*

---

## 📂 Project Architecture

```
employee-payroll-system/
├── src/
│   ├── main/
│   │   ├── java/com/payroll/         # Spring Boot backend, controllers, and services
│   │   └── resources/
│   │       ├── static/               # Frontend (HTML, CSS, JS) UI Pages
│   │       ├── application.properties# System configurations
│   │       └── schema.sql            # Core Relational schema & seeder
├── pom.xml                           # Maven dependencies
└── README.md                         # Project documentation
```

---

## 🤝 Contribution Guidelines

Contributions are welcome and highly appreciated! If you want to contribute to this project:
1. Fork the repository.
2. Create a new branch: `git checkout -b feature-branch-name`.
3. Commit your changes: `git commit -m 'Added cool feature'`.
4. Push to the branch: `git push origin feature-branch-name`.
5. Open a Pull Request.

---

> _Developed enthusiastically for modern HR solutions._
