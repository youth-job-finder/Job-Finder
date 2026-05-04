<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login Error - JobFinder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css?v=4">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<!-- Reusable Navbar -->
<jsp:include page="/views/components/default-navbar.jsp" />

<!-- Error Section -->
<section class="login-section">
    <div class="login-container">
        <div class="login-header">
            <div class="auth-icon error-icon">
                <i class="fas fa-exclamation-circle"></i>
            </div>
            <h1>Authentication Failed</h1>
            <p>There was an error during the login process</p>
        </div>
        
        <div class="alert alert-error">
            <i class="fas fa-exclamation-triangle"></i>
            <div>
                <strong>Login Error</strong>
                <p>We couldn't authenticate you. Please check your credentials and try again.</p>
                <hr>
                <p class="mb-0">If the problem persists, please contact support.</p>
            </div>
        </div>
        
        <div class="form-actions" style="justify-content: center; margin-top: 2rem;">
            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">
                <i class="fas fa-redo"></i> Try Again
            </a>
            <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary">
                <i class="fas fa-home"></i> Home
            </a>
        </div>
        
        <div class="additional-links" style="text-align: center; margin-top: 1.5rem;">
            <a href="${pageContext.request.contextPath}/password-reset-request">
                <i class="fas fa-key"></i> Forgot Password?
            </a>
        </div>
    </div>
</section>

<jsp:include page="/views/components/footer.jsp" />

</body>
</html>
