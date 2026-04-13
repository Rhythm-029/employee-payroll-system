document.addEventListener('DOMContentLoaded', () => {
    // Populate user names from localStorage
    const userName = localStorage.getItem('userName') || 'Admin User';
    const adminNameElem = document.getElementById('adminName');
    const empNameElem = document.getElementById('empName');
    
    if (adminNameElem) adminNameElem.textContent = userName;
    if (empNameElem) empNameElem.textContent = `Welcome, ${userName}`;

    // Initialize Dashboard data if on Admin page
    if (document.getElementById('employeesTableBody')) {
        renderEmployees();
    }
});

// Mock Data for 42 Indian Employees
const employees = [
    { id: 'PAY-101', name: 'Rhythm Singhal', role: 'EMPLOYEE', designation: 'Senior Developer', type: 'FullTime', salary: 85000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-102', name: 'Yashwardhan Singh', role: 'ADMIN', designation: 'Project Manager', type: 'FullTime', salary: 95000, status: 'Active', shift: '10:00 - 19:00' },
    { id: 'PAY-103', name: 'Prathamesh Bhandare', role: 'EMPLOYEE', designation: 'Tech Lead', type: 'FullTime', salary: 90000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-104', name: 'Anshika Gupta', role: 'EMPLOYEE', designation: 'Accountant', type: 'FullTime', salary: 65000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-105', name: 'Soha Patel', role: 'EMPLOYEE', designation: 'HR Specialist', type: 'FullTime', salary: 60000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-106', name: 'Hetanshi Vora', role: 'EMPLOYEE', designation: 'Data Analyst', type: 'PartTime', salary: 45000, status: 'Active', shift: '14:00 - 19:00' },
    { id: 'PAY-107', name: 'Tanishka Shukla', role: 'EMPLOYEE', designation: 'UX Designer', type: 'FullTime', salary: 75000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-108', name: 'Aarav Sharma', role: 'EMPLOYEE', designation: 'System Admin', type: 'FullTime', salary: 70000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-109', name: 'Vihaan Verma', role: 'EMPLOYEE', designation: 'Junior Developer', type: 'FullTime', salary: 45000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-110', name: 'Aditya Rao', role: 'EMPLOYEE', designation: 'Marketing Lead', type: 'FullTime', salary: 80000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-111', name: 'Arjun Nair', role: 'EMPLOYEE', designation: 'Operations', type: 'FullTime', salary: 55000, status: 'Active', shift: '08:00 - 17:00' },
    { id: 'PAY-112', name: 'Sai Reddy', role: 'EMPLOYEE', designation: 'Database Admin', type: 'FullTime', salary: 78000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-113', name: 'Ishan Malhotra', role: 'EMPLOYEE', designation: 'DevOps', type: 'FullTime', salary: 88000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-114', name: 'Krishna Iyer', role: 'EMPLOYEE', designation: 'Senior Engineer', type: 'FullTime', salary: 92000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-115', name: 'Aryan Dubey', role: 'EMPLOYEE', designation: 'QA Lead', type: 'FullTime', salary: 72000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-116', name: 'Shaurya Pratap', role: 'EMPLOYEE', designation: 'Security Analyst', type: 'FullTime', salary: 84000, status: 'Active', shift: '22:00 - 06:00' },
    { id: 'PAY-117', name: 'Kabir Kapoor', role: 'EMPLOYEE', designation: 'Product Manager', type: 'FullTime', salary: 110000, status: 'Active', shift: '10:00 - 19:00' },
    { id: 'PAY-118', name: 'Atharv Kulkarni', role: 'EMPLOYEE', designation: 'Frontend Dev', type: 'FullTime', salary: 68000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-119', name: 'Reyansh Joshi', role: 'EMPLOYEE', designation: 'Backend Dev', type: 'FullTime', salary: 74000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-120', name: 'Advait Deshmukh', role: 'EMPLOYEE', designation: 'Cloud Architect', type: 'FullTime', salary: 125000, status: 'Active', shift: '08:00 - 17:00' },
    { id: 'PAY-121', name: 'Ananya Pandey', role: 'EMPLOYEE', designation: 'HR Executive', type: 'FullTime', salary: 52000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-122', name: 'Diya Mukherjee', role: 'EMPLOYEE', designation: 'Financial Analyst', type: 'FullTime', salary: 67000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-123', name: 'Myra Khan', role: 'EMPLOYEE', designation: 'Content Strategist', type: 'FullTime', salary: 58000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-124', name: 'Shanaya Kapoor', role: 'EMPLOYEE', designation: 'Social Media', type: 'FullTime', salary: 48000, status: 'Active', shift: '10:00 - 19:00' },
    { id: 'PAY-125', name: 'Sia Goel', role: 'EMPLOYEE', designation: 'Legal Counsel', type: 'FullTime', salary: 115000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-126', name: 'Vanya Sethi', role: 'EMPLOYEE', designation: 'PR Lead', type: 'FullTime', salary: 72000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-127', name: 'Zoya Mirza', role: 'EMPLOYEE', designation: 'Copywriter', type: 'FullTime', salary: 42000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-128', name: 'Aarush Gill', role: 'EMPLOYEE', designation: 'Mobile Developer', type: 'FullTime', salary: 76000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-129', name: 'Vivaan Das', role: 'EMPLOYEE', designation: 'AI Engineer', type: 'FullTime', salary: 135000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-130', name: 'Prisha Roy', role: 'EMPLOYEE', designation: 'Business Analyst', type: 'FullTime', salary: 69000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-131', name: 'Navya Bhat', role: 'EMPLOYEE', designation: 'Office Manager', type: 'FullTime', salary: 45000, status: 'Active', shift: '08:00 - 17:00' },
    { id: 'PAY-132', name: 'Saanvi Chawla', role: 'EMPLOYEE', designation: 'Receptionist', type: 'FullTime', salary: 32000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-133', name: 'Advik Saxena', role: 'EMPLOYEE', designation: 'Support Engineer', type: 'FullTime', salary: 40000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-134', name: 'Ayaan Sheikh', role: 'EMPLOYEE', designation: 'Network Admin', type: 'FullTime', salary: 62000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-135', name: 'Hridaan Bose', role: 'EMPLOYEE', designation: 'Graphic Designer', type: 'FullTime', salary: 51000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-136', name: 'Kyra Oberoi', role: 'EMPLOYEE', designation: 'Event Planner', type: 'FullTime', salary: 59000, status: 'Active', shift: '10:00 - 19:00' },
    { id: 'PAY-137', name: 'Nyra Singhania', role: 'EMPLOYEE', designation: 'Internal Auditor', type: 'FullTime', salary: 81000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-138', name: 'Ruhi Bajaj', role: 'EMPLOYEE', designation: 'Recruiter', type: 'FullTime', salary: 55000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-139', name: 'Sara Ali', role: 'EMPLOYEE', designation: 'Training Lead', type: 'FullTime', salary: 73000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-140', name: 'Tara Dsouza', role: 'EMPLOYEE', designation: 'Facility Manager', type: 'FullTime', salary: 47000, status: 'Active', shift: '07:00 - 16:00' },
    { id: 'PAY-141', name: 'Zara Khurana', role: 'EMPLOYEE', designation: 'Procurement', type: 'FullTime', salary: 61000, status: 'Active', shift: '09:00 - 18:00' },
    { id: 'PAY-142', name: 'Kavya Menon', role: 'EMPLOYEE', designation: 'Compliance Officer', type: 'FullTime', salary: 77000, status: 'Active', shift: '09:00 - 18:00' }
];

/**
 * Render workers table in Admin Portal
 */
function renderEmployees() {
    const tableBody = document.getElementById('employeesTableBody');
    if (!tableBody) return;

    tableBody.innerHTML = '';
    employees.forEach(emp => {
        const row = `
            <tr>
                <td>${emp.id}</td>
                <td>
                    <div class="d-flex align-items-center">
                        <img src="https://ui-avatars.com/api/?name=${encodeURIComponent(emp.name)}&background=random" class="rounded-circle me-2" width="30">
                        ${emp.name}
                    </div>
                </td>
                <td><span class="badge bg-secondary-subtle text-secondary small">${emp.role}</span></td>
                <td>${emp.designation}</td>
                <td><span class="badge bg-success">${emp.status}</span></td>
                <td>
                    <button class="btn btn-sm btn-outline-info me-1" onclick="viewEmployeeDetails('${emp.id}')">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            </tr>
        `;
        tableBody.innerHTML += row;
    });
}

/**
 * Open employee details Modal
 */
function viewEmployeeDetails(id) {
    const emp = employees.find(e => e.id === id);
    if (!emp) return;
    
    // Set dynamic content in Modal (assuming Modal IDs exist in HTML)
    document.getElementById('modalEmpName').textContent = emp.name;
    document.getElementById('modalEmpId').textContent = emp.id;
    document.getElementById('modalEmpDesignation').textContent = emp.designation;
    document.getElementById('modalEmpSalary').textContent = `₹ ${emp.salary.toLocaleString('en-IN')}`;
    document.getElementById('modalEmpShift').textContent = emp.shift;
    
    const myModal = new bootstrap.Modal(document.getElementById('employeeDetailsModal'));
    myModal.show();
}

/**
 * Switch dashboard sections
 */
function showSection(sectionId) {
    const sections = document.querySelectorAll('#contentSections section');
    const sidebarLinks = document.querySelectorAll('.sidebar-link');

    sections.forEach(sec => {
        sec.classList.add('d-none');
        sec.classList.remove('animated');
    });

    const targetSection = document.getElementById(sectionId);
    if (targetSection) {
        targetSection.classList.remove('d-none');
        setTimeout(() => targetSection.classList.add('animated'), 10);
    }

    sidebarLinks.forEach(link => {
        link.classList.remove('active');
        const onclickAttr = link.getAttribute('onclick');
        if (onclickAttr && onclickAttr.includes(`'${sectionId}'`)) {
            link.classList.add('active');
        }
    });
}
