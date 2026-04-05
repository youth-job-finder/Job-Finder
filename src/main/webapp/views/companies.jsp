<%--
  Created by IntelliJ IDEA.
  User: aubre
  Date: 2026/03/24
  Time: 23:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Top Companies - JobFinder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/companies.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<!-- Reusable Navbar -->
<jsp:include page="/views/components/default-navbar.jsp" />

<!-- Companies Header -->
<section class="page-header">
    <div class="container">
        <h1 class="page-title">Top Companies</h1>
        <p class="page-subtitle">Discover verified companies offering amazing opportunities</p>
    </div>
</section>

<!-- Search Section -->
<section class="search-section">
    <div class="container">
        <div class="search-container">
            <div class="search-box">
                <i class="fas fa-search"></i>
                <input type="text" placeholder="Search companies by name or industry..." class="search-input">
            </div>
            <div class="filter-buttons">
                <button class="filter-btn active">
                    <i class="fas fa-building"></i> All Companies
                </button>
                <button class="filter-btn">
                    <i class="fas fa-star"></i> Top Rated
                </button>
                <button class="filter-btn">
                    <i class="fas fa-map-marker-alt"></i> Local
                </button>
            </div>
        </div>
    </div>
</section>

<!-- Companies Grid -->
<section class="companies-section">
    <div class="container">
        <div class="companies-grid">
            <c:choose>
                <c:when test="${not empty companies}">
                    <table class="companies-table">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Registration Number</th>
                            <th>Email</th>
                            <th>Website</th>
                            <th>Status</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="company" items="${companies}">
                            <tr>
                                <td>${company.name}</td>
                                <td>${company.registrationNumber}</td>
                                <td>${company.email}</td>
                                <td><a href="${company.url}" target="_blank">${company.url}</a></td>
                                <td>${company.status}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <p>No companies registered yet.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</section>

<jsp:include page="/views/components/footer.jsp" />

</body>
</html>
