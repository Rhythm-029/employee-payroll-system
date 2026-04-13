document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const loginError = document.getElementById('loginError');
    const togglePassword = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');

    // Toggle Password Visibility
    if (togglePassword) {
        togglePassword.addEventListener('click', () => {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            togglePassword.querySelector('i').classList.toggle('fa-eye');
            togglePassword.querySelector('i').classList.toggle('fa-eye-slash');
        });
    }

    // Handle Login submission
    if (loginForm) {
        loginForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value.trim();
            const password = passwordInput.value.trim();
            const selectedRole = document.querySelector('input[name="loginRole"]:checked').value;

            // Mock Credentials (Validation Logic)
            // In a real app, this would be an API call POST /api/auth/login
            const ADMIN_EMAIL = 'admin@hr.com';
            const ADMIN_PASS = 'admin123';
            const EMP_EMAIL = 'emp@hr.com';
            const EMP_PASS = 'emp123';

            // Validation step: Check if role matches email
            if (selectedRole === 'ADMIN') {
                if (email === ADMIN_EMAIL && password === ADMIN_PASS) {
                    localStorage.setItem('userRole', 'ADMIN');
                    localStorage.setItem('userName', 'Admin User');
                    window.location.href = 'admin_dashboard.html';
                } else {
                    showError('Invalid HR credentials.');
                }
            } else {
                if (email === EMP_EMAIL && password === EMP_PASS) {
                    localStorage.setItem('userRole', 'EMPLOYEE');
                    localStorage.setItem('userName', 'John Doe');
                    window.location.href = 'employee_dashboard.html';
                } else {
                    showError('Invalid Employee credentials.');
                }
            }
        });
    }

    function showError(msg) {
        loginError.classList.remove('d-none');
        loginError.textContent = msg;
        passwordInput.value = '';
    }
});

// Function to check if user is logged in (to be used in dashboards)
function checkAuth(role) {
    const storedRole = localStorage.getItem('userRole');
    if (!storedRole) {
        window.location.href = 'index.html';
    } else if (role && storedRole !== role) {
        // Simple role guard
        window.location.href = role === 'ADMIN' ? 'admin_dashboard.html' : 'employee_dashboard.html';
    }
}

// Function to logout
function logout() {
    localStorage.removeItem('userRole');
    localStorage.removeItem('userName');
    window.location.href = 'index.html';
}
