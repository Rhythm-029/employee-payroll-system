/**
 * Core business logic for Admin and Employee dashboards.
 * Fetches real-time data from Spring Boot REST endpoints.
 */

let currentEmployees = []; // Global store for the current list (Real data from DB)

document.addEventListener('DOMContentLoaded', () => {
    const userId = localStorage.getItem('userId');
    const role = localStorage.getItem('userRole');
    const userName = localStorage.getItem('userName');

    // Set Welcome Name
    const adminNameElem = document.getElementById('adminName');
    const empNameElem = document.getElementById('empName');
    if (adminNameElem) adminNameElem.textContent = userName;
    if (empNameElem) empNameElem.textContent = `Welcome, ${userName}`;

    // Load dynamic data based on page/role
    if (document.getElementById('employeesTableBody')) {
        loadEmployeesTable(); // Admin view
        updateAdminStats(); // Real-time stats
    } else if (document.getElementById('empProfileSection')) {
        loadEmployeeProfile(userId); // Employee view
    }
});

/**
 * ADMIN: Fetch and update statistical summary
 */
async function updateAdminStats() {
    try {
        const response = await fetch('/api/management/summary');
        const stats = await response.json();
        const totalCountElem = document.getElementById('totalEmployeesCount');
        const totalBudgetElem = document.getElementById('totalPayrollBudget');
        
        if (totalCountElem) totalCountElem.textContent = stats.total || 0;
        if (totalBudgetElem) totalBudgetElem.textContent = `₹ ${(stats.total_budget || 0).toLocaleString('en-IN')}`;
    } catch (error) {
        console.error('Failed to fetch summary:', error);
    }
}

/**
 * ADMIN: Load all employees from database
 */
async function loadEmployeesTable() {
    try {
        const response = await fetch('/api/management/employees');
        if (response.ok) {
            currentEmployees = await response.json();
            renderEmployees(currentEmployees);
            renderRecentPayrollActivities(currentEmployees);
            updateAdminStats();
            if (typeof generateMockAttendance === 'function') generateMockAttendance();
        }
    } catch (error) {
        console.error('Failed to load employees:', error);
    }
}

/**
 * EMPLOYEE: Load specific user data for dashboard
 */
async function loadEmployeeProfile(userId) {
    try {
        console.log('Loading profile for User ID:', userId);
        const response = await fetch(`/api/management/employees/profile/${userId}`);
        const emp = await response.json();
        
        if (!emp) {
            console.error('No employee data found for ID:', userId);
            return;
        }

        // Update Nav/Header
        const profileImg = document.getElementById('empProfileImg');
        const customImgUrl = `/api/management/employees/profile-image/${userId}`;
        
        // Try loading custom image if it exists, else fallback to avatars
        if (profileImg) {
            profileImg.src = customImgUrl + '?t=' + new Date().getTime(); // Anti-cache
            profileImg.onerror = () => {
                profileImg.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(emp.name)}&background=3498db&color=fff`;
            };
        }

        // Dashboard/Profile Page Display
        const mainDisplay = document.getElementById('mainProfileDisplay');
        if (mainDisplay) {
            mainDisplay.src = customImgUrl + '?t=' + new Date().getTime();
            mainDisplay.onerror = () => {
                mainDisplay.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(emp.name)}&background=3498db&color=fff`;
            };
        }

        // Restore Profile Info
        const lastSalaryElem = document.getElementById('lastSalary');
        const leavesLeftElem = document.getElementById('leavesLeft');
        if (lastSalaryElem) lastSalaryElem.textContent = `₹ ${(emp.baseSalary || 0).toLocaleString('en-IN')}`;
        if (leavesLeftElem) leavesLeftElem.textContent = emp.leavesTaken || 0;
        
        const profileNameElem = document.getElementById('profileName');
        const profileEmpIdElem = document.getElementById('profileEmpId');
        const profileEmailElem = document.getElementById('profileEmail');
        const profileShiftElem = document.getElementById('profileShift');
        
        if (profileNameElem) profileNameElem.textContent = emp.name;
        if (profileEmpIdElem) profileEmpIdElem.textContent = `PAY-${emp.id}`;
        if (profileEmailElem) profileEmailElem.textContent = emp.email;
        if (profileShiftElem) profileShiftElem.textContent = emp.shiftTime || '09:00 AM - 06:00 PM';
        
        const phoneElem = document.getElementById('profilePhone');
        const addressElem = document.getElementById('profileAddress');
        if (phoneElem) phoneElem.textContent = emp.phone || '+91 XXXXX XXXXX';
        if (addressElem) addressElem.textContent = emp.address || 'Not Provided';
        
        // Update Salary Breakdown
        const bBase = document.getElementById('breakdownBase');
        const bBonus = document.getElementById('breakdownBonus');
        const bTax = document.getElementById('breakdownTax');
        const netSal = document.getElementById('netSalary');

        if (bBase) bBase.textContent = `₹ ${(emp.baseSalary || 0).toLocaleString('en-IN')}`;
        if (bBonus) bBonus.textContent = `₹ ${(emp.bonus || 0).toLocaleString('en-IN')}`;
        
        const taxAmount = ((emp.baseSalary || 0) + (emp.bonus || 0)) * ((emp.taxPercentage || 10) / 100);
        if (bTax) bTax.textContent = `- ₹ ${taxAmount.toLocaleString('en-IN')}`;
        
        if (netSal) netSal.textContent = `₹ ${((emp.baseSalary || 0) + (emp.bonus || 0) - taxAmount).toLocaleString('en-IN')}`;

        const downloadBtn = document.getElementById('downloadSlipBtn');
        if (downloadBtn) downloadBtn.onclick = () => downloadRealSlip(emp.id);

    } catch (error) {
        console.error('Profile loading error:', error);
    }
}

/**
 * Edit Personal Profile (Employee Only)
 */
async function updateProfile() {
    const userId = localStorage.getItem('userId');
    const name = document.getElementById('profileName').textContent;
    const phone = document.getElementById('profilePhone').textContent;
    const address = document.getElementById('profileAddress').textContent;
    const currentImg = document.querySelector('#profile img').src;

    const { value: formValues } = await Swal.fire({
        title: 'Edit Personal Profile',
        html:
            `<input id="edit-name" class="swal2-input" placeholder="Full Name" value="${name}">` +
            `<input id="edit-phone" class="swal2-input" placeholder="Phone Number" value="${phone}">` +
            `<textarea id="edit-address" class="swal2-textarea" placeholder="Address">${address}</textarea>` +
            `<input id="edit-pic" class="swal2-input" placeholder="Profile Picture URL" value="${currentImg}">`,
        focusConfirm: false,
        preConfirm: () => {
            return {
                userId: userId,
                name: document.getElementById('edit-name').value,
                phone: document.getElementById('edit-phone').value,
                address: document.getElementById('edit-address').value,
                profilePicture: document.getElementById('edit-pic').value
            }
        }
    });

    if (formValues) {
        try {
            const response = await fetch('/api/management/employees/profile', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formValues)
            });
            if (response.ok) {
                showToast('Success', 'Profile updated successfully!', 'success');
                localStorage.setItem('userName', formValues.name);
                location.reload();
            }
        } catch (error) {
            showToast('Error', 'Update failed.', 'error');
        }
    }
}

/**
 * Render workers table in Admin Portal
 */
function renderEmployees(employees) {
    const tableBody = document.getElementById('employeesTableBody');
    if (!tableBody) return;

    tableBody.innerHTML = '';
    employees.forEach((emp, index) => {
        const badgeClass = emp.status === 'ACTIVE' ? 'bg-success' : 'bg-danger';
        const row = `
            <tr>
                <td>PAY-${emp.id}</td>
                <td>
                    <div class="d-flex align-items-center">
                        <img src="https://ui-avatars.com/api/?name=${encodeURIComponent(emp.name)}&background=random" class="rounded-circle me-2" width="30">
                        ${emp.name}
                    </div>
                </td>
                <td><span class="badge bg-secondary-subtle text-secondary small">EMPLOYEE</span></td>
                <td>${emp.email}</td>
                <td><span class="badge ${badgeClass}">${emp.status}</span></td>
                <td>
                    <button class="btn btn-sm btn-outline-info me-1" onclick="viewEmployeeDetailsByIndex(${index})">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="confirmDeleteEmployee(${emp.id})">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            </tr>
        `;
        tableBody.innerHTML += row;
    });
}

function viewEmployeeDetailsByIndex(index) {
    viewEmployeeDetails(currentEmployees[index]);
}

/**
 * ADMIN: Add Employee (Real Database Insertion)
 */
async function simulateAddEmployee() {
    const { value: formValues } = await Swal.fire({
        title: 'Add New Employee',
        html:
            '<input id="swal-name" class="swal2-input" placeholder="Full Name">' +
            '<input id="swal-email" class="swal2-input" placeholder="Email Address">' +
            '<input id="swal-salary" class="swal2-input" placeholder="Base Salary">' +
            '<input id="swal-pass" type="password" class="swal2-input" placeholder="Initial Password">',
        focusConfirm: false,
        preConfirm: () => {
            return {
                name: document.getElementById('swal-name').value,
                email: document.getElementById('swal-email').value,
                salary: document.getElementById('swal-salary').value,
                password: document.getElementById('swal-pass').value
            }
        }
    });

    if (formValues) {
        try {
            const response = await fetch('/api/management/employees', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formValues)
            });
            const result = await response.json();
            if (result.status === 'success') {
                showToast('Success', 'Employee added and user account created!', 'success');
                loadEmployeesTable();
            }
        } catch (error) {
            showToast('Error', 'Failed to add employee.', 'error');
        }
    }
}

/**
 * ADMIN: Update salary details
 */
async function updateAdminSalary(empId) {
    const emp = currentEmployees.find(e => e.id === empId);
    const { value: form } = await Swal.fire({
        title: 'Update Employee Structure',
        html:
            `<label class="swal2-label">Base Salary</label><input id="swal-base" class="swal2-input" value="${emp.baseSalary}">` +
            `<label class="swal2-label">Bonus</label><input id="swal-bonus" class="swal2-input" value="${emp.bonus}">` +
            `<label class="swal2-label">Tax %</label><input id="swal-tax" class="swal2-input" value="${emp.taxPercentage}">` +
            `<label class="swal2-label">Dept</label><input id="swal-dept" class="swal2-input" value="${emp.department}">` +
            `<label class="swal2-label">Desig</label><input id="swal-desig" class="swal2-input" value="${emp.designation}">`,
        preConfirm: () => {
            return {
                baseSalary: document.getElementById('swal-base').value,
                bonus: document.getElementById('swal-bonus').value,
                taxPercentage: document.getElementById('swal-tax').value,
                department: document.getElementById('swal-dept').value,
                designation: document.getElementById('swal-desig').value
            }
        }
    });

    if (form) {
        try {
            await fetch(`/api/management/employees/${empId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(form)
            });
            showToast('Success', 'Profile updated in database!', 'success');
            loadEmployeesTable();
        } catch (error) {
            showToast('Error', 'Update failed.', 'error');
        }
    }
}
async function handleImageUpload(input) {
    if (!input.files || !input.files[0]) return;
    const userId = localStorage.getItem('userId');
    const formData = new FormData();
    formData.append('userId', userId);
    formData.append('file', input.files[0]);

    showToast('Uploading', 'Saving image to backup...', 'info');
    try {
        const response = await fetch('/api/management/employees/upload-image', {
            method: 'POST',
            body: formData
        });
        if (response.ok) {
            showToast('Success', 'Profile picture updated!', 'success');
            setTimeout(() => location.reload(), 1500);
        }
    } catch (error) {
        showToast('Error', 'Upload failed.', 'error');
    }
}
function exportEmployeeList() {
    window.location.href = '/api/management/employees/export';
}

/**
 * ADMIN: Suspend/Reactivate user
 */
async function toggleUserStatus(userId, currentStatus) {
    const newStatus = currentStatus === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE';
    try {
        await fetch(`/api/management/employees/status/${userId}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
        });
        showToast('Updated', `Account is now ${newStatus}`, 'info');
        loadEmployeesTable();
        bootstrap.Modal.getInstance(document.getElementById('employeeDetailsModal')).hide();
    } catch (error) {
        showToast('Error', 'Status update failed.', 'error');
    }
}

/**
 * PDF: Download Salary Slip
 */
function downloadRealSlip(empId) {
    showToast('Processing', 'Generating PDF from real-time data...', 'info');
    window.location.href = `/api/management/payroll/${empId}/slip`;
}

/**
 * UI Helpers
 */
function viewEmployeeDetails(emp) {
    document.getElementById('modalEmpName').textContent = emp.name;
    document.getElementById('modalEmpId').textContent = `PAY-${emp.id}`;
    document.getElementById('modalEmpSalary').textContent = `₹ ${emp.baseSalary.toLocaleString('en-IN')}`;
    
    const suspendBtn = document.getElementById('suspendActionBtn');
    suspendBtn.textContent = emp.status === 'ACTIVE' ? 'Suspend Account' : 'Reactivate Account';
    suspendBtn.className = emp.status === 'ACTIVE' ? 'btn btn-outline-danger w-100 mb-2' : 'btn btn-outline-success w-100 mb-2';
    suspendBtn.onclick = () => toggleUserStatus(emp.userId, emp.status);

    const updateBtn = document.getElementById('updateSalaryBtn');
    updateBtn.onclick = () => updateAdminSalary(emp.id);

    new bootstrap.Modal(document.getElementById('employeeDetailsModal')).show();
}

/**
 * Filter Employees for the search bar (Rewired to real data)
 */
function filterEmployees() {
    const input = document.getElementById('employeeSearchInput');
    if (!input) return;
    const filter = input.value.toLowerCase();
    const filteredList = currentEmployees.filter(emp => 
        emp.name.toLowerCase().includes(filter) || 
        emp.email.toLowerCase().includes(filter) ||
        `PAY-${emp.id}`.toLowerCase().includes(filter)
    );
    renderEmployees(filteredList);
}

/**
 * Render Recent Payroll Activities (Rewired to real data)
 */
function renderRecentPayrollActivities(dataList) {
    const payrollBody = document.getElementById('payrollActivitiesTableBody');
    if (!payrollBody) return;
    
    payrollBody.innerHTML = '';
    const list = dataList || currentEmployees;
    const top10 = list.slice(0, 10);
    
    top10.forEach(emp => {
        const row = `
            <tr>
                <td><div class="d-flex align-items-center"><img src="https://ui-avatars.com/api/?name=${encodeURIComponent(emp.name)}&background=random" class="rounded-circle me-3" width="35"> ${emp.name}</div></td>
                <td>Monthly Salary</td>
                <td class="fw-bold">₹ ${emp.baseSalary.toLocaleString('en-IN')}</td>
                <td><span class="badge rounded-pill bg-success-subtle text-success px-3">Processed</span></td>
            </tr>
        `;
        payrollBody.innerHTML += row;
    });
}

/**
 * Confirm and Delete Employee (Preserving UI improvement)
 */
function confirmDeleteEmployee(id) {
    Swal.fire({
        title: 'Delete Employee?',
        text: `Are you sure you want to completely remove account PAY-${id}? This action cannot be undone.`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Yes, delete it!'
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const response = await fetch(`/api/management/employees/${id}`, { method: 'DELETE' });
                if (response.ok) {
                    showToast('Deleted!', `Employee record removed.`, 'success');
                    loadEmployeesTable();
                } else {
                    showToast('Error', 'Deletion failed on server.', 'error');
                }
            } catch (error) {
                showToast('Error', 'Cannot connect to server.', 'error');
            }
        }
    });
}

/**
 * Generate Mock Attendance for selected date (Rewired to real data)
 */
function generateMockAttendance() {
    const tbody = document.getElementById('attendanceTableBody');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    const dateSelect = document.getElementById('attendanceDateSelect');
    const selectedSeed = dateSelect ? new Date(dateSelect.value).getDate() : 14;
    
    currentEmployees.forEach((emp, index) => {
        const isLate = (selectedSeed + index) % 7 === 0;
        const isAbsent = (selectedSeed + index) % 19 === 0;
        
        let status = isAbsent ? 'Absent' : 'Present';
        let badgeClass = isAbsent ? 'bg-danger' : 'bg-success';
        
        let checkIn = isAbsent ? '--:--' : (isLate ? '09:45 AM' : '09:00 AM');
        let checkOut = isAbsent ? '--:--' : '06:00 PM';
        let totalTime = isAbsent ? '0h 0m' : (isLate ? '8h 15m' : '9h 0m');
        
        const row = `
            <tr>
                <td><div class="d-flex align-items-center"><img src="https://ui-avatars.com/api/?name=${encodeURIComponent(emp.name)}&background=random" class="rounded-circle me-3" width="35"> ${emp.name}</div></td>
                <td>${checkIn}</td>
                <td>${checkOut}</td>
                <td>${totalTime}</td>
                <td><span class="badge ${badgeClass}">${status}</span></td>
            </tr>
        `;
        tbody.innerHTML += row;
    });
}

/**
 * View My Profile (Preserving UI improvement)
 */
function viewMyProfile(e) {
    if (e) e.preventDefault();
    const name = localStorage.getItem('userName') || 'Admin User';
    const email = localStorage.getItem('userEmail') || 'admin@hr.com';
    const role = localStorage.getItem('userRole') || 'ADMIN';
    
    Swal.fire({
        title: 'My Profile',
        html: `
            <div class="text-center mt-2">
                <img src="https://ui-avatars.com/api/?name=${encodeURIComponent(name)}&background=1a4f8a&color=fff" class="rounded-circle mb-3 shadow" width="80">
                <h4 class="fw-bold">${name}</h4>
                <p class="text-secondary mb-1"><i class="fas fa-envelope me-2"></i>${email}</p>
                <span class="badge bg-primary px-3 py-1 mt-2 rounded-pill">Role: ${role}</span>
            </div>
        `,
        confirmButtonText: 'Close',
        confirmButtonColor: '#1a4f8a'
    });
}

function showToast(title, text, icon) {
    Swal.fire({ title, text, icon, toast: true, position: 'top-end', showConfirmButton: false, timer: 3000 });
}

function showSection(sectionId) {
    document.querySelectorAll('#contentSections section').forEach(sec => sec.classList.add('d-none'));
    const target = document.getElementById(sectionId);
    if (target) target.classList.remove('d-none');
    
    // Update active state in sidebar
    document.querySelectorAll('.sidebar-link').forEach(link => link.classList.remove('active'));
    const activeLink = document.querySelector(`.sidebar-link[onclick*="${sectionId}"]`);
    if (activeLink) activeLink.classList.add('active');
}

const staticAttendanceData = {
    'April 2026': [
        { date: '15 April 2026', in: '09:00 AM', out: '06:00 PM', hrs: '9h 00m', status: 'Present', class: 'bg-success' },
        { date: '14 April 2026', in: '08:55 AM', out: '06:05 PM', hrs: '9h 10m', status: 'Present', class: 'bg-success' },
        { date: '13 April 2026', in: '09:05 AM', out: '06:15 PM', hrs: '9h 10m', status: 'Present', class: 'bg-success' },
        { date: '12 April 2026', in: '09:15 AM', out: '06:05 PM', hrs: '8h 50m', status: 'Present', class: 'bg-success' },
        { date: '11 April 2026', in: '--:--', out: '--:--', hrs: '0h 00m', status: 'Absent', class: 'bg-danger' },
        { date: '10 April 2026', in: '09:00 AM', out: '06:00 PM', hrs: '9h 00m', status: 'Present', class: 'bg-success' }
    ],
    'March 2026': [
        { date: '15 March 2026', in: '08:50 AM', out: '05:50 PM', hrs: '9h 00m', status: 'Present', class: 'bg-success' },
        { date: '14 March 2026', in: '09:00 AM', out: '06:00 PM', hrs: '9h 00m', status: 'Present', class: 'bg-success' },
        { date: '13 March 2026', in: '--:--', out: '--:--', hrs: '0h 00m', status: 'Absent', class: 'bg-danger' },
        { date: '12 March 2026', in: '09:05 AM', out: '06:15 PM', hrs: '9h 10m', status: 'Present', class: 'bg-success' }
    ],
    'February 2026': [
        { date: '28 February 2026', in: '09:00 AM', out: '06:00 PM', hrs: '9h 00m', status: 'Present', class: 'bg-success' },
        { date: '27 February 2026', in: '--:--', out: '--:--', hrs: '0h 00m', status: 'Absent', class: 'bg-danger' },
        { date: '26 February 2026', in: '09:10 AM', out: '06:10 PM', hrs: '9h 00m', status: 'Present', class: 'bg-success' },
        { date: '25 February 2026', in: '08:50 AM', out: '05:40 PM', hrs: '8h 50m', status: 'Present', class: 'bg-success' }
    ],
    'January 2026': [
        { date: '15 January 2026', in: '09:00 AM', out: '06:00 PM', hrs: '9h 00m', status: 'Present', class: 'bg-success' },
        { date: '14 January 2026', in: '09:00 AM', out: '06:00 PM', hrs: '9h 00m', status: 'Present', class: 'bg-success' },
        { date: '13 January 2026', in: '09:00 AM', out: '06:00 PM', hrs: '9h 00m', status: 'Present', class: 'bg-success' }
    ],
    'December 2025': [
        { date: '15 December 2025', in: '09:00 AM', out: '06:00 PM', hrs: '9h 00m', status: 'Present', class: 'bg-success' },
        { date: '14 December 2025', in: '09:00 AM', out: '06:00 PM', hrs: '9h 00m', status: 'Present', class: 'bg-success' },
        { date: '13 December 2025', in: '--:--', out: '--:--', hrs: '0h 00m', status: 'Absent', class: 'bg-danger' }
    ]
};

function updateEmployeeAttendance() {
    const select = document.getElementById('attendanceMonthSelectEmp');
    const tbody = document.getElementById('attendanceTableBodyEmp');
    if (!select || !tbody) return;

    const selectedMonth = select.value;
    const data = staticAttendanceData[selectedMonth] || staticAttendanceData['April 2026'];
    
    tbody.innerHTML = '';
    data.forEach(row => {
        tbody.innerHTML += `<tr><td>${row.date}</td><td>${row.in}</td><td>${row.out}</td><td>${row.hrs}</td><td><span class="badge ${row.class}">${row.status}</span></td></tr>`;
    });
}
