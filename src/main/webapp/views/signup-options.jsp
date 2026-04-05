<%--
  Created by IntelliJ IDEA.
  User: aubre
  Date: 2026/03/24
  Time: 23:03
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
</head>
<body>

<!-- Reusable Navbar -->
<jsp:include page="/views/components/default-navbar.jsp" />

<section class="signup-options">
    <div class="signup-container">
        <h2>Create Your Account</h2>
        <p>Choose how you want to register:</p>

        <div class="option-card">
            <h3>Individual User</h3>
            <p>Register as a job seeker. You’ll be able to upload your CV, search jobs, apply, and track your applications.</p>
            <a href="${pageContext.request.contextPath}/signup" class="btn btn-primary">Sign Up as Individual</a>
        </div>

        <div class="option-card">
            <h3>Company</h3>
            <p>Register your company to post jobs and internships, manage applications, and build trust with candidates.</p>
            <a href="${pageContext.request.contextPath}/company-register" class="btn btn-primary">Register as Company</a>
        </div>
    </div>
</section>

<!-- footer -->
<jsp:include page="/views/components/footer.jsp" />

</body>
</html>
