<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Set New Password - JobFinder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css?v=4">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<!-- Reusable Navbar -->
<jsp:include page="/views/components/default-navbar.jsp" />

<div class="reset-container">
    <div class="reset-header">
        <div class="reset-icon">
            <i class="fas fa-lock"></i>
        </div>
        <h1 class="reset-title">Set New Password</h1>
        <p class="reset-subtitle">
            Enter your new password below.
        </p>
    </div>
    
    <!-- Success/Error Messages -->
    <c:if test="${not empty requestScope.message}">
        <div class="alert alert-success">
            <i class="fas fa-check-circle"></i> ${requestScope.message}
        </div>
    </c:if>
    
    <c:if test="${not empty requestScope.error}">
        <div class="alert alert-error">
            <i class="fas fa-exclamation-triangle"></i> ${requestScope.error}
        </div>
    </c:if>
    
    <form id="resetForm" class="reset-form">
        <div class="form-group">
            <label for="password">New Password</label>
            <input type="password" id="password" name="password" required placeholder="Enter new password">
            <div class="password-strength">
                <div class="password-strength-bar" id="strengthBar"></div>
            </div>
            <div class="password-requirements">
                <div class="requirement" id="length">
                    <i class="fas fa-times"></i> At least 8 characters
                </div>
                <div class="requirement" id="uppercase">
                    <i class="fas fa-times"></i> One uppercase letter
                </div>
                <div class="requirement" id="lowercase">
                    <i class="fas fa-times"></i> One lowercase letter
                </div>
                <div class="requirement" id="number">
                    <i class="fas fa-times"></i> One number
                </div>
                <div class="requirement" id="special">
                    <i class="fas fa-times"></i> One special character
                </div>
            </div>
        </div>
        
        <div class="form-group">
            <label for="confirmPassword">Confirm Password</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required placeholder="Confirm new password">
        </div>
        
        <button type="submit" class="btn btn-primary" id="submitBtn" disabled>
            <i class="fas fa-save"></i> Set New Password
        </button>
    </form>
    
    <div class="loading" id="loading">
        <span class="spinner"></span>
        Updating password...
    </div>
    
    <div class="back-link">
        <a href="${pageContext.request.contextPath}/login">
            <i class="fas fa-arrow-left"></i> Back to Login
        </a>
    </div>
</div>

<!-- Footer -->
<jsp:include page="/views/components/footer.jsp" />

<script>
document.addEventListener('DOMContentLoaded', function() {
    const resetForm = document.getElementById('resetForm');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const submitBtn = document.getElementById('submitBtn');
    const loading = document.getElementById('loading');
    const strengthBar = document.getElementById('strengthBar');
    
    // Password requirements
    const requirements = {
        length: { element: document.getElementById('length'), regex: /.{8,}/ },
        uppercase: { element: document.getElementById('uppercase'), regex: /[A-Z]/ },
        lowercase: { element: document.getElementById('lowercase'), regex: /[a-z]/ },
        number: { element: document.getElementById('number'), regex: /[0-9]/ },
        special: { element: document.getElementById('special'), regex: /[!@#$%^&*(),.?":{}|<>]/ }
    };
    
    // Check password strength
    function checkPasswordStrength(password) {
        let passedRequirements = 0;
        
        for (const [key, req] of Object.entries(requirements)) {
            if (req.regex.test(password)) {
                req.element.classList.add('met');
                req.element.querySelector('i').className = 'fas fa-check';
                passedRequirements++;
            } else {
                req.element.classList.remove('met');
                req.element.querySelector('i').className = 'fas fa-times';
            }
        }
        
        // Update strength bar
        strengthBar.className = 'password-strength-bar';
        if (passedRequirements <= 2) {
            strengthBar.classList.add('strength-weak');
        } else if (passedRequirements <= 4) {
            strengthBar.classList.add('strength-medium');
        } else {
            strengthBar.classList.add('strength-strong');
        }
        
        return passedRequirements === 5;
    }
    
    // Check if passwords match
    function checkPasswordMatch() {
        return passwordInput.value === confirmPasswordInput.value && passwordInput.value.length > 0;
    }
    
    // Enable/disable submit button
    function updateSubmitButton() {
        const isPasswordValid = checkPasswordStrength(passwordInput.value);
        const doPasswordsMatch = checkPasswordMatch();
        submitBtn.disabled = !(isPasswordValid && doPasswordsMatch);
    }
    
    // Event listeners
    passwordInput.addEventListener('input', function() {
        checkPasswordStrength(this.value);
        updateSubmitButton();
    });
    
    confirmPasswordInput.addEventListener('input', updateSubmitButton);
    
    // Form submission
    if (resetForm) {
        resetForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(resetForm);
            const password = formData.get('password');
            const confirmPassword = formData.get('confirmPassword');
            
            // Show loading state
            submitBtn.disabled = true;
            loading.style.display = 'block';
            
            // Remove existing alerts
            const existingAlerts = document.querySelectorAll('.alert');
            existingAlerts.forEach(alert => alert.remove());
            
            // Get token from URL parameter
            const urlParams = new URLSearchParams(window.location.search);
            const token = urlParams.get('token');
            
            if (!token) {
                const alertDiv = document.createElement('div');
                alertDiv.className = 'alert alert-error';
                alertDiv.innerHTML = '<i class="fas fa-exclamation-triangle"></i> Invalid reset token. Please request a new password reset.';
                
                resetForm.parentNode.insertBefore(alertDiv, resetForm);
                loading.style.display = 'none';
                submitBtn.disabled = false;
                return;
            }
            
            fetch('${pageContext.request.contextPath}/reset-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'token=' + encodeURIComponent(token) + '&newPassword=' + encodeURIComponent(password) + '&confirmPassword=' + encodeURIComponent(confirmPassword)
            })
            .then(response => response.json())
            .then(data => {
                loading.style.display = 'none';
                
                // Create and show alert
                const alertDiv = document.createElement('div');
                alertDiv.className = 'alert ' + (data.success ? 'alert-success' : 'alert-error');
                alertDiv.innerHTML = '<i class="fas fa-' + (data.success ? 'check-circle' : 'exclamation-triangle') + '"></i> ' + data.message;
                
                resetForm.parentNode.insertBefore(alertDiv, resetForm);
                
                if (data.success) {
                    resetForm.remove();
                    // Redirect to login after 3 seconds
                    setTimeout(() => {
                        window.location.href = '${pageContext.request.contextPath}/login?passwordChanged=true';
                    }, 3000);
                } else {
                    submitBtn.disabled = false;
                }
            })
            .catch(error => {
                console.error('Error:', error);
                loading.style.display = 'none';
                submitBtn.disabled = false;
                
                // Create and show error alert
                const alertDiv = document.createElement('div');
                alertDiv.className = 'alert alert-error';
                alertDiv.innerHTML = '<i class="fas fa-exclamation-triangle"></i> An error occurred. Please try again.';
                
                resetForm.parentNode.insertBefore(alertDiv, resetForm);
            });
        });
    }
});
</script>
</body>
</html>
