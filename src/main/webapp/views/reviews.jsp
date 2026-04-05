<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Company Reviews - Youth Job & Internship Finder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/companies.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<!-- Default Navbar for public page -->
<jsp:include page="/views/components/default-navbar.jsp" />
<section class="jobs-section">
    <div class="jobs-container">
        <h2>Company Reviews</h2>
        <p>Student and applicant feedback on companies will appear here as the reviews feature is implemented.</p>
    </div>
</section>

<!-- footer -->
<jsp:include page="/views/components/footer.jsp" />

</body>
</html>
