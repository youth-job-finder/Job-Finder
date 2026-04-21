<%--
  Created by IntelliJ IDEA.
  User: aubre
  Date: 2026/03/24
  Time: 22:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign Up - Youth Job & Internship Finder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        .password-requirements { margin-top: 8px; padding: 10px; background: #f8f9fa; border-radius: 4px; }
        .requirement { display: block; color: #6c757d; font-size: 0.85rem; margin: 4px 0; }
        .requirement.valid { color: #28a745; }
        .requirement.invalid { color: #dc3545; }
        .requirement i { width: 16px; margin-right: 4px; }
        .password-match { margin-top: 4px; font-size: 0.85rem; }
        .password-match.match { color: #28a745; }
        .password-match.mismatch { color: #dc3545; }
    </style>
</head>
<body>

<!-- Reusable Navbar -->
<jsp:include page="/views/components/default-navbar.jsp" />

<!-- Signup Section -->
<section class="signup-section">
    <div class="signup-container">
        <h2>Create Your Account</h2>
        
        <!-- Error Messages -->
        <c:if test="${not empty error}">
            <div class="alert alert-error">
                <i class="fas fa-exclamation-triangle"></i> ${error}
            </div>
        </c:if>
        
        <!-- Success Messages -->
        <c:if test="${not empty success}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i> ${success}
            </div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/signup" method="post" class="signup-form">
            <div class="form-group">
                <label for="fullname">Full Name</label>
                <input type="text" id="fullname" name="fullname" required>
            </div>

            <div class="form-group">
                <label for="email">Email Address</label>
                <input type="email" id="email" name="email" required>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required minlength="8" pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$">

                <label for="confirmPassword">Confirm Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required minlength="8">

                <div class="password-match" id="passwordMatch"></div>

                <div class="password-requirements">
                    <small class="requirement" id="length"><i class="fas fa-times"></i> At least 8 characters</small>
                    <small class="requirement" id="uppercase"><i class="fas fa-times"></i> One uppercase letter</small>
                    <small class="requirement" id="lowercase"><i class="fas fa-times"></i> One lowercase letter</small>
                    <small class="requirement" id="number"><i class="fas fa-times"></i> One number</small>
                    <small class="requirement" id="special"><i class="fas fa-times"></i> One special character (@$!%*?&)</small>
                </div>
            </div>

            <button type="submit" class="btn btn-primary btn-block">Sign Up</button>
        </form>

        <p class="login-prompt">
            Already have an account?
            <a href="${pageContext.request.contextPath}/login" class="auth-link-secondary">Login</a>
        </p>
    </div>
</section>

<!-- footer -->
<jsp:include page="/views/components/footer.jsp" />
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const password = document.getElementById('password');
        const confirmPassword = document.getElementById('confirmPassword');
        const reqs = {
            length: document.getElementById('length'),
            uppercase: document.getElementById('uppercase'),
            lowercase: document.getElementById('lowercase'),
            number: document.getElementById('number'),
            special: document.getElementById('special')
        };
        const passwordMatch = document.getElementById('passwordMatch');

        function update(el, valid) {
            el.className = 'requirement ' + (valid ? 'valid' : 'invalid');
            el.querySelector('i').className = 'fas fa-' + (valid ? 'check' : 'times');
        }

        function validate() {
            const v = password.value;
            update(reqs.length, v.length >= 8);
            update(reqs.uppercase, /[A-Z]/.test(v));
            update(reqs.lowercase, /[a-z]/.test(v));
            update(reqs.number, /\d/.test(v));
            update(reqs.special, /[@$!%*?&]/.test(v));
            checkMatch();
        }

        function checkMatch() {
            if (!confirmPassword.value) { passwordMatch.textContent = ''; return; }
            const match = password.value === confirmPassword.value;
            passwordMatch.innerHTML = '<i class="fas fa-' + (match ? 'check' : 'times') + '"></i> Passwords ' + (match ? 'match' : 'do not match');
            passwordMatch.className = 'password-match ' + (match ? 'match' : 'mismatch');
        }

        password.addEventListener('input', validate);
        confirmPassword.addEventListener('input', checkMatch);
    });
</script>
</body>
</html>