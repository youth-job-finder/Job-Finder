<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Company Analytics - Youth Job & Internship Finder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css?v=4">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/company-navbar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/company-analytics.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/views/components/company-navbar.jsp" />

<section class="analytics">
    <div class="analytics-container">
        <h2><i class="fas fa-chart-bar"></i> Company Analytics</h2>
        <p>View detailed statistics and insights about your company's performance.</p>

        <c:if test="${not empty company}">
            <!-- Overview Stats -->
            <div class="analytics-grid">
                <div class="analytics-card">
                    <div class="card-icon">
                        <i class="fas fa-briefcase"></i>
                    </div>
                    <div class="card-content">
                        <h3>Total Jobs</h3>
                        <p class="stat-number">${analytics['totalJobs']}</p>
                    </div>
                </div>

                <div class="analytics-card">
                    <div class="card-icon applications">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="card-content">
                        <h3>Total Applications</h3>
                        <p class="stat-number">${analytics['totalApplications']}</p>
                    </div>
                </div>

                <div class="analytics-card">
                    <div class="card-icon reviews">
                        <i class="fas fa-star"></i>
                    </div>
                    <div class="card-content">
                        <h3>Total Reviews</h3>
                        <p class="stat-number">${analytics['totalReviews']}</p>
                    </div>
                </div>

                <div class="analytics-card">
                    <div class="card-icon rating">
                        <i class="fas fa-trophy"></i>
                    </div>
                    <div class="card-content">
                        <h3>Average Rating</h3>
                        <p class="stat-number">
                            <c:choose>
                                <c:when test="${analytics['averageRating'] > 0}">
                                    ${String.format("%.1f", analytics['averageRating'])}/5
                                </c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                </div>
            </div>

            <!-- Application Status Breakdown -->
            <div class="analytics-section">
                <h3><i class="fas fa-pie-chart"></i> Application Status Breakdown</h3>
                <div class="status-grid">
                    <div class="status-card pending">
                        <div class="status-icon">
                            <i class="fas fa-clock"></i>
                        </div>
                        <div class="status-content">
                            <h4>Pending</h4>
                            <p class="status-number">${analytics['pendingApplications']}</p>
                        </div>
                    </div>

                    <div class="status-card approved">
                        <div class="status-icon">
                            <i class="fas fa-check-circle"></i>
                        </div>
                        <div class="status-content">
                            <h4>Approved</h4>
                            <p class="status-number">${analytics['approvedApplications']}</p>
                        </div>
                    </div>

                    <div class="status-card rejected">
                        <div class="status-icon">
                            <i class="fas fa-times-circle"></i>
                        </div>
                        <div class="status-content">
                            <h4>Rejected</h4>
                            <p class="status-number">${analytics['rejectedApplications']}</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Quick Actions -->
            <div class="analytics-actions">
                <a href="${pageContext.request.contextPath}/company/dashboard" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Back to Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/company/listings" class="btn btn-primary">
                    <i class="fas fa-list"></i> View Job Listings
                </a>
                <a href="${pageContext.request.contextPath}/company/applicants" class="btn btn-info">
                    <i class="fas fa-users"></i> View Applicants
                </a>
            </div>
        </c:if>

        <c:if test="${empty company}">
            <div class="no-data">
                <i class="fas fa-exclamation-circle"></i>
                <p>No company data available. Please complete your company profile first.</p>
                <a href="${pageContext.request.contextPath}/company/dashboard" class="btn btn-primary">
                    Go to Dashboard
                </a>
            </div>
        </c:if>
    </div>
</section>

<jsp:include page="/views/components/company-footer.jsp" />

</body>
</html>
