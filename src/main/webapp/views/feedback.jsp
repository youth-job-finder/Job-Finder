<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login Feedback - JobFinder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<!-- Default Navbar -->
<jsp:include page="/views/components/default-navbar.jsp" />

<section class="page-header">
    <div class="container">
        <h1 class="page-title">Login Result</h1>
        <p class="page-subtitle">${feedback}</p>
        <div class="hero-actions">
            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">
                <i class="fas fa-arrow-left"></i> Back to Login
            </a>
        </div>
    </div>
</section>

<jsp:include page="/views/components/footer.jsp" />

</body>
</html>
