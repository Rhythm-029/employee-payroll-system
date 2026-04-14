document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const loginError = document.getElementById('loginError');
    const togglePassword = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');
    const rememberMeCheckbox = document.getElementById('rememberMe');
    const googleSignInButton = document.getElementById('googleSignInButton');
    const googleClientId = googleSignInButton?.dataset?.googleClientId || 'YOUR_GOOGLE_CLIENT_ID';

    const rememberedEmail = localStorage.getItem('rememberedEmail');
    const rememberedRole = localStorage.getItem('rememberedRole');
    const rememberFlag = localStorage.getItem('rememberMe') === 'true';
    const storedRole = localStorage.getItem('userRole');
    const storedName = localStorage.getItem('userName');

    if (rememberFlag && storedRole && storedName) {
        if (storedRole === 'ADMIN') {
            window.location.href = 'admin_dashboard.html';
        } else {
            window.location.href = 'employee_dashboard.html';
        }
        return;
    }

    if (rememberedEmail) {
        document.getElementById('email').value = rememberedEmail;
        if (rememberMeCheckbox) rememberMeCheckbox.checked = true;
    }

    if (rememberedRole) {
        const roleInput = document.querySelector(`input[name="loginRole"][value="${rememberedRole}"]`);
        if (roleInput) {
            roleInput.checked = true;
        }
    }

    if (googleSignInButton && googleClientId && googleClientId !== 'YOUR_GOOGLE_CLIENT_ID') {
        initializeGoogleSignIn();
    } else if (googleSignInButton) {
        googleSignInButton.style.display = 'none';
    }

    // Toggle Password Visibility
    if (togglePassword) {
        togglePassword.addEventListener('click', () => {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            togglePassword.querySelector('i').classList.toggle('fa-eye');
            togglePassword.querySelector('i').classList.toggle('fa-eye-slash');
        });
    }

    const forgotPasswordLink = Array.from(document.querySelectorAll('a')).find(link => link.textContent.trim() === 'Forgot Password?');
    if (forgotPasswordLink) {
        forgotPasswordLink.addEventListener('click', async (e) => {
            e.preventDefault();
            await handleForgotPassword();
        });
    }

    // Handle Login submission
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value.trim();
            const password = passwordInput.value.trim();
            const selectedRole = document.querySelector('input[name="loginRole"]:checked').value;
            const rememberMe = rememberMeCheckbox && rememberMeCheckbox.checked;

            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, password, role: selectedRole })
                });

                const data = await response.json();

                if (data.status === 'success') {
                    localStorage.setItem('userRole', data.role);
                    localStorage.setItem('userName', data.name);
                    localStorage.setItem('userEmail', data.email || email);

                    if (rememberMe) {
                        localStorage.setItem('rememberMe', 'true');
                        localStorage.setItem('rememberedEmail', email);
                        localStorage.setItem('rememberedRole', selectedRole);
                    } else {
                        localStorage.removeItem('rememberMe');
                        localStorage.removeItem('rememberedEmail');
                        localStorage.removeItem('rememberedRole');
                    }

                    if (data.role === 'ADMIN') {
                        window.location.href = 'admin_dashboard.html';
                    } else {
                        window.location.href = 'employee_dashboard.html';
                    }
                } else {
                    showError(data.message || 'Invalid credentials.');
                }
            } catch (err) {
                console.warn('Backend not detected, using mock validation.', err);
                handleMockLogin(email, password, selectedRole, rememberMe);
            }
        });
    }

    function initializeGoogleSignIn() {
        if (!window.google || !window.google.accounts || !window.google.accounts.id) {
            setTimeout(initializeGoogleSignIn, 250);
            return;
        }

        google.accounts.id.initialize({
            client_id: googleClientId,
            callback: handleGoogleResponse,
            cancel_on_tap_outside: true
        });

        google.accounts.id.renderButton(googleSignInButton, {
            theme: 'outline',
            size: 'large',
            width: '100%',
            text: 'signin_with'
        });
    }

    async function handleGoogleResponse(response) {
        if (!response || !response.credential) {
            showError('Google authentication failed.');
            return;
        }

        const selectedRole = document.querySelector('input[name="loginRole"]:checked').value;
        const rememberMe = rememberMeCheckbox && rememberMeCheckbox.checked;

        try {
            const apiResponse = await fetch('/api/auth/google', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ idToken: response.credential, role: selectedRole })
            });
            const data = await apiResponse.json();

            if (data.status === 'success') {
                localStorage.setItem('userRole', data.role);
                localStorage.setItem('userName', data.name);
                localStorage.setItem('userEmail', data.email);

                if (rememberMe) {
                    localStorage.setItem('rememberMe', 'true');
                    localStorage.setItem('rememberedEmail', data.email);
                    localStorage.setItem('rememberedRole', data.role);
                }

                if (data.role === 'ADMIN') {
                    window.location.href = 'admin_dashboard.html';
                } else {
                    window.location.href = 'employee_dashboard.html';
                }
            } else {
                showError(data.message || 'Google login failed.');
            }
        } catch (err) {
            showError('Google login failed. Please try again.');
        }
    }

    async function handleForgotPassword() {
        const selectedRole = document.querySelector('input[name="loginRole"]:checked').value;
        const email = prompt('Enter your registered email address to receive password reset instructions:');
        if (!email) return;

        try {
            const response = await fetch('/api/auth/forgot-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: email.trim(), role: selectedRole })
            });
            const data = await response.json();
            if (data.status === 'success') {
                alert(data.message || 'A password reset email has been sent. Please check your inbox.');
            } else {
                alert(data.message || 'Unable to send password reset instructions.');
            }
        } catch (err) {
            alert('Unable to send password reset email. Please try again later.');
        }
    }

    function handleMockLogin(email, password, selectedRole, rememberMe) {
        const ADMIN_EMAIL = 'admin@hr.com';
        const ADMIN_PASS = 'admin123';
        const EMP_EMAIL = 'emp@hr.com';
        const EMP_PASS = 'emp123';

        if (selectedRole === 'ADMIN') {
            if (email === ADMIN_EMAIL && password === ADMIN_PASS) {
                localStorage.setItem('userRole', 'ADMIN');
                localStorage.setItem('userName', 'Admin User');
                localStorage.setItem('userEmail', email);
                if (rememberMe) {
                    localStorage.setItem('rememberMe', 'true');
                    localStorage.setItem('rememberedEmail', email);
                    localStorage.setItem('rememberedRole', selectedRole);
                }
                window.location.href = 'admin_dashboard.html';
            } else {
                showError('Invalid HR credentials.');
            }
        } else {
            if (email === EMP_EMAIL && password === EMP_PASS) {
                localStorage.setItem('userRole', 'EMPLOYEE');
                localStorage.setItem('userName', 'Rhythm Singhal');
                localStorage.setItem('userEmail', email);
                if (rememberMe) {
                    localStorage.setItem('rememberMe', 'true');
                    localStorage.setItem('rememberedEmail', email);
                    localStorage.setItem('rememberedRole', selectedRole);
                }
                window.location.href = 'employee_dashboard.html';
            } else {
                showError('Invalid Employee credentials.');
            }
        }
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
    localStorage.removeItem('userEmail');
    localStorage.removeItem('rememberMe');
    localStorage.removeItem('rememberedEmail');
    localStorage.removeItem('rememberedRole');
    window.location.href = 'index.html';
}
