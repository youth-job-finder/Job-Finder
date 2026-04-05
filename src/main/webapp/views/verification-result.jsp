<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Email Verification - JobFinder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<!-- Reusable Navbar -->
<jsp:include page="/views/components/default-navbar.jsp" />

<div class="verification-container">
    <c:choose>
        <c:when test="${requestScope.verificationSuccess == true}">
            <div class="verification-icon success-icon">
                <i class="fas fa-check-circle"></i>
            </div>
            <h1 class="verification-title">Email Verified Successfully!</h1>
            <p class="verification-message">
                Your email has been successfully verified. You can now login to your account and start using JobFinder.
            </p>
            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">
                    <i class="fas fa-sign-in-alt"></i> Login to Your Account
                </a>
            </div>
        </c:when>
        
        <c:when test="${requestScope.verificationSuccess == false}">
            <div class="verification-icon error-icon">
                <i class="fas fa-exclamation-circle"></i>
            </div>
            <h1 class="verification-title">Verification Failed</h1>
            <p class="verification-message">
                <c:choose>
                    <c:when test="${not empty requestScope.errorMessage}">
                        ${requestScope.errorMessage}
                    </c:when>
                    <c:otherwise>
                        The verification link is invalid or has expired. Please request a new verification email.
                    </c:otherwise>
                </c:choose>
            </p>
            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Back to Login
                </a>
            </div>
            
            <div class="resend-section">
                <h3>Need a new verification email?</h3>
                <p>Enter your email address below and we'll send you a new verification link.</p>
                <form class="resend-form" id="resendForm">
                    <input type="email" name="email" placeholder="Enter your email" required>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-paper-plane"></i> Resend
                    </button>
                </form>
                <div class="resend-message" id="resendMessage"></div>
            </div>
        </c:when>
        
        <c:otherwise>
            <div class="verification-icon error-icon">
                <i class="fas fa-exclamation-triangle"></i>
            </div>
            <h1 class="verification-title">Invalid Request</h1>
            <p class="verification-message">
                Invalid verification request. Please use the verification link sent to your email.
            </p>
            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Back to Login
                </a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Footer -->
<jsp:include page="/views/components/footer.jsp" />

<script>
document.addEventListener('DOMContentLoaded', function() {
    const resendForm = document.getElementById('resendForm');
    const resendMessage = document.getElementById('resendMessage');
    
    if (resendForm) {
        resendForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(resendForm);
            const email = formData.get('email');
            
            // Show loading state
            resendMessage.innerHTML = '<div class="loading"><span class="spinner"></span>Sending...</div>';
            
            fetch('${pageContext.request.contextPath}/resend-verification', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'email=' + encodeURIComponent(email)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    resendMessage.innerHTML = '<div class="alert alert-success"><i class="fas fa-check-circle"></i> ' + data.message + '</div>';
                    resendForm.reset();
                } else {
                    resendMessage.innerHTML = '<div class="alert alert-error"><i class="fas fa-exclamation-triangle"></i> ' + data.message + '</div>';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                resendMessage.innerHTML = '<div class="alert alert-error"><i class="fas fa-exclamation-triangle"></i> An error occurred. Please try again.</div>';
            });
        });
    }
});
</script>

<jsp:include page="/views/components/footer.jsp" />

</body>
</html>
