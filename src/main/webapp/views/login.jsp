<%--
  Created by IntelliJ IDEA.
  User: aubre
  Date: 2026/03/24
  Time: 22:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Login - Youth Job & Internship Finder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<!-- Reusable Navbar -->
<jsp:include page="/views/components/default-navbar.jsp" />

<!-- Login Section -->
<section class="login-section">
    <div class="login-container">
        <div class="login-header">
            <div class="auth-icon">
                <i class="fas fa-user-circle"></i>
            </div>
            <h1>Welcome Back</h1>
            <p>Sign in to your JobFinder account</p>
        </div>
        
        <!-- Success Messages -->
        <c:if test="${param.registered == 'true'}">
            <c:choose>
                <c:when test="${param.verificationRequired == 'true'}">
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i> Registration successful! Please check your email to verify your account before logging in.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle"></i> Registration successful! You can now login.
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>
        
        <c:if test="${param.logout == 'true'}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i> You have been logged out successfully.
            </div>
        </c:if>
        
        <c:if test="${param.passwordChanged == 'true'}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i> Your password has been changed successfully. Please login with your new password.
            </div>
        </c:if>
        
        <!-- Session Expired Message -->
        <c:if test="${param.sessionExpired == 'true'}">
            <div class="alert alert-warning">
                <i class="fas fa-clock"></i> Your session has expired due to inactivity. Please log in again to continue.
            </div>
        </c:if>
        
        <!-- Error Messages -->
        <c:if test="${requestScope.error != null}">
            <div class="alert alert-error">
                <i class="fas fa-exclamation-triangle"></i> ${requestScope.error}
            </div>
        </c:if>
        
        <!-- Resend Verification Section for Unverified Users -->
        <c:if test="${not empty requestScope.unverifiedEmail}">
            <div class="resend-verification-section" style="margin: 1rem 0; padding: 1rem; background: #f8f9fa; border-radius: 8px; border: 1px solid #dee2e6;">
                <h4 style="margin-bottom: 0.5rem;"><i class="fas fa-envelope"></i> Resend Verification Email</h4>
                <p style="margin-bottom: 1rem; font-size: 0.9rem; color: #666;">
                    Didn't receive the verification email? Enter your email below to request a new one.
                </p>
                <form id="resendForm" style="display: flex; gap: 0.5rem;">
                    <input type="email" name="email" value="${requestScope.unverifiedEmail}" placeholder="Enter your email" required 
                           style="flex: 1; padding: 0.5rem; border: 1px solid #ced4da; border-radius: 4px;">
                    <button type="submit" class="btn btn-secondary" style="white-space: nowrap;">
                        <i class="fas fa-paper-plane"></i> Resend
                    </button>
                </form>
                <div id="resendMessage" style="margin-top: 0.5rem;"></div>
            </div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/login" method="post" class="login-form">
            <div class="form-group">
                <label for="email">Email Address</label>
                <input type="email" id="email" name="email" required placeholder="Enter your email">
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required placeholder="Enter your password">
            </div>

            <button type="submit" class="login-btn">
                <i class="fas fa-sign-in-alt"></i> Login
            </button>
        </form>

        <div class="login-footer">
            <p>Don't have an account? <a class="auth-link-secondary" href="${pageContext.request.contextPath}/signup">Sign up here</a></p>
            <p><a href="${pageContext.request.contextPath}/request-password-reset" class="auth-link-secondary">Forgot your password?</a></p>
        </div>
    </div>
</section>

<!-- Footer -->
<jsp:include page="/views/components/footer.jsp" />

<script>
    // Check if there's a verification notice in session storage
    window.addEventListener('load', function() {
        const verificationNotice = sessionStorage.getItem('verificationNotice');
        if (verificationNotice) {
            const noticeDiv = document.createElement('div');
            noticeDiv.className = 'verification-notice';
            noticeDiv.innerHTML = verificationNotice;
            document.querySelector('.login-header').after(noticeDiv);
            sessionStorage.removeItem('verificationNotice');
        }
    });
    
    // Handle resend verification form
    const resendForm = document.getElementById('resendForm');
    const resendMessage = document.getElementById('resendMessage');
    
    if (resendForm) {
        resendForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(resendForm);
            const email = formData.get('email');
            
            resendMessage.innerHTML = '<span style="color: #666;"><i class="fas fa-spinner fa-spin"></i> Sending...</span>';
            
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
                    resendMessage.innerHTML = '<span style="color: #28a745;"><i class="fas fa-check-circle"></i> ' + data.message + '</span>';
                } else {
                    resendMessage.innerHTML = '<span style="color: #dc3545;"><i class="fas fa-exclamation-triangle"></i> ' + data.message + '</span>';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                resendMessage.innerHTML = '<span style="color: #dc3545;"><i class="fas fa-exclamation-triangle"></i> An error occurred. Please try again.</span>';
            });
        });
    }
</script>
</body>
</html>
