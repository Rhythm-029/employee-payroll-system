// Initialize form listener
document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const selectedRole = document.querySelector('input[name="loginRole"]:checked').value;
            login(selectedRole);
        });
    }

    const togglePass = document.getElementById('togglePassword');
    if (togglePass) {
        togglePass.addEventListener('click', () => {
            const passInput = document.getElementById('password');
            const type = passInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passInput.setAttribute('type', type);
            togglePass.querySelector('i').classList.toggle('fa-eye');
            togglePass.querySelector('i').classList.toggle('fa-eye-slash');
        });
    }
});

async function login(role) {
    const emailElem = document.getElementById('email');
    const passwordElem = document.getElementById('password');
    
    if (!emailElem.value || !passwordElem.value) {
        showError('Please enter both email and password.');
        return;
    }

    const loginData = {
        email: emailElem.value,
        password: passwordElem.value,
        role: role
    };

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(loginData)
        });

        const result = await response.json();

        if (result.status === 'success') {
            // Store session details
            localStorage.setItem('userId', result.userId);
            localStorage.setItem('userName', result.name);
            localStorage.setItem('userRole', result.role);
            localStorage.setItem('userEmail', result.email);
            
            // Redirect based on role
            if (result.role === 'ADMIN') {
                window.location.href = 'admin_dashboard.html';
            } else {
                window.location.href = 'employee_dashboard.html';
            }
        } else {
            showError(result.message || 'Login failed. Please check credentials.');
        }
    } catch (error) {
        console.error('Login error:', error);
        showError('Cannot connect to server. Ensure backend is running.');
    }
}

/**
 * Log out user with SweetAlert confirmation (Preserving UI improvement)
 */
function logout(e) {
    if (e) e.preventDefault();
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            title: 'Confirm Logout',
            text: 'Are you sure you want to safely log out of your session?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Yes, logout'
        }).then((result) => {
            if (result.isConfirmed) {
                executeLogout();
            }
        });
    } else {
        if (confirm("Are you sure you want to logout?")) {
            executeLogout();
        }
    }
}

function executeLogout() {
    localStorage.clear();
    window.location.href = 'index.html';
}

/**
 * Guards routes based on role.
 */
function checkAuth(requiredRole) {
    const role = localStorage.getItem('userRole');
    const userId = localStorage.getItem('userId');

    if (!userId || !role) {
        window.location.href = 'index.html';
        return;
    }

    if (requiredRole && role !== requiredRole) {
        // Redirect to their own portal if they try to access the wrong one
        window.location.href = role === 'ADMIN' ? 'admin_dashboard.html' : 'employee_dashboard.html';
    }
}

function showError(message) {
    const alertBox = document.getElementById('loginAlert');
    if (alertBox) {
        alertBox.textContent = message;
        alertBox.classList.remove('d-none');
    } else {
        alert(message);
    }
}

// System initialization
window.onload = function() {
    console.log('Auth system initialized (Database Mode)');
};
