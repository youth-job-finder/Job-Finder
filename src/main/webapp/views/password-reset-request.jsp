<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password - JobFinder</title>
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
            <i class="fas fa-key"></i>
        </div>
        <h1 class="reset-title">Reset Your Password</h1>
        <p class="reset-subtitle">
            Enter your email address and we'll send you a link to reset your password.
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
            <label for="email">Email Address</label>
            <input type="email" id="email" name="email" required placeholder="Enter your email address">
        </div>
        
        <button type="submit" class="btn btn-primary" id="submitBtn">
            <i class="fas fa-paper-plane"></i> Send Reset Link
        </button>
    </form>
    
    <div class="loading" id="loading">
        <span class="spinner"></span>
        Sending reset link...
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
    const submitBtn = document.getElementById('submitBtn');
    const loading = document.getElementById('loading');
    
    if (resetForm) {
        resetForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(resetForm);
            const email = formData.get('email');
            
            // Show loading state
            submitBtn.disabled = true;
            loading.style.display = 'block';
            
            // Remove existing alerts
            const existingAlerts = document.querySelectorAll('.alert');
            existingAlerts.forEach(alert => alert.remove());
            
            fetch('${pageContext.request.contextPath}/request-password-reset', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'email=' + encodeURIComponent(email)
            })
            .then(response => response.json())
            .then(data => {
                loading.style.display = 'none';
                submitBtn.disabled = false;
                
                // Create and show alert
                const alertDiv = document.createElement('div');
                alertDiv.className = 'alert ' + (data.success ? 'alert-success' : 'alert-error');
                alertDiv.innerHTML = '<i class="fas fa-' + (data.success ? 'check-circle' : 'exclamation-triangle') + '"></i> ' + data.message;
                
                resetForm.parentNode.insertBefore(alertDiv, resetForm);
                
                if (data.success) {
                    resetForm.reset();
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
