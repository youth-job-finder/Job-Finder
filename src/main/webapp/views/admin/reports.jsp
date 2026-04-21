<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reports & Analytics - JobFinder Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-reports.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/views/components/system-navbar.jsp" />

<section class="dashboard">
    <div class="dashboard-container">
        <div class="reports-header">
            <h2><i class="fas fa-chart-bar"></i> Reports & Analytics</h2>
            <a href="${currentUri}" class="btn btn-secondary">
                <i class="fas fa-sync"></i> Refresh
            </a>
        </div>

        <p class="generated-at">
            <i class="fas fa-clock"></i> Generated: ${generatedAt}
        </p>

        <div class="summary-cards">
            <div class="summary-card">
                <i class="fas fa-users"></i>
                <h4>Total Users</h4>
                <div class="number">${reports.userStats.totalUsers}</div>
            </div>
            <div class="summary-card">
                <i class="fas fa-building"></i>
                <h4>Companies</h4>
                <div class="number">${reports.companyStats.totalCompanies}</div>
            </div>
            <div class="summary-card">
                <i class="fas fa-briefcase"></i>
                <h4>Job Postings</h4>
                <div class="number">${reports.jobStats.totalJobs}</div>
            </div>
            <div class="summary-card">
                <i class="fas fa-file-alt"></i>
                <h4>Applications</h4>
                <div class="number">${reports.applicationStats.totalApplications}</div>
            </div>
        </div>

        <div class="reports-grid">
            <div class="report-card">
                <h3><i class="fas fa-users"></i> User Statistics</h3>
                <div class="stat-row">
                    <span class="stat-label">Total Users</span>
                    <span class="stat-value">${reports.userStats.totalUsers}</span>
                </div>
                <div class="stat-row">
                    <span class="stat-label">Applicants</span>
                    <span class="stat-value">${reports.userStats.applicants}</span>
                </div>
                <div class="stat-row">
                    <span class="stat-label">Company Admins</span>
                    <span class="stat-value">${reports.userStats.companyAdmins}</span>
                </div>
                <div class="stat-row">
                    <span class="stat-label">System Admins</span>
                    <span class="stat-value">${reports.userStats.systemAdmins}</span>
                </div>
                <div class="stat-row">
                    <span class="stat-label">Verified Users</span>
                    <span class="stat-value">${reports.userStats.verifiedUsers}</span>
                </div>
                <div class="stat-row">
                    <span class="stat-label">New This Week</span>
                    <span class="stat-value">${reports.userStats.newUsersThisWeek}</span>
                </div>
                <div class="stat-row">
                    <span class="stat-label">New This Month</span>
                    <span class="stat-value">${reports.userStats.newUsersThisMonth}</span>
                </div>
            </div>

            <div class="report-card">
                <h3><i class="fas fa-building"></i> Company Statistics</h3>
                <div class="stat-row">
                    <span class="stat-label">Total Companies</span>
                    <span class="stat-value">${reports.companyStats.totalCompanies}</span>
                </div>
                <div class="stat-row">
                    <span class="stat-label">Pending Approval</span>
                    <span class="stat-value" style="color: #ff9800;">${reports.companyStats.pendingCompanies}</span>
                </div>
                <div class="stat-row">
                    <span class="stat-label">Approved</span>
                    <span class="stat-value" style="color: #4caf50;">${reports.companyStats.approvedCompanies}</span>
                </div>
                <div class="stat-row">
                    <span class="stat-label">Rejected</span>
                    <span class="stat-value" style="color: #f44336;">${reports.companyStats.rejectedCompanies}</span>
                </div>
                <div class="stat-row">
                    <span class="stat-label">URL Verified</span>
                    <span class="stat-value">${reports.companyStats.urlVerifiedCompanies}</span>
                </div>
            </div>

            <div class="report-card">
                <h3><i class="fas fa-file-alt"></i> Application Statistics</h3>
                <div class="stat-row">
                    <span class="stat-label">Total Applications</span>
                    <span class="stat-value">${reports.applicationStats.totalApplications}</span>
                </div>
                <div class="stat-row">
                    <span class="stat-label">Pending Review</span>
                    <span class="stat-value" style="color: #ff9800;">${reports.applicationStats.pendingApplications}</span>
                </div>
                <div class="stat-row">
                    <span class="stat-label">Approved</span>
                    <span class="stat-value" style="color: #4caf50;">${reports.applicationStats.approvedApplications}</span>
                </div>
                <div class="stat-row">
                    <span class="stat-label">Rejected</span>
                    <span class="stat-value" style="color: #f44336;">${reports.applicationStats.rejectedApplications}</span>
                </div>
            </div>

            <div class="report-card">
                <h3><i class="fas fa-chart-line"></i> Activity Trends (30 Days)</h3>
                <div class="stat-row">
                    <span class="stat-label">New Users</span>
                    <span class="stat-value">${reports.activityTrends.recentUsers}</span>
                </div>
                <div class="stat-row">
                    <span class="stat-label">New Jobs Posted</span>
                    <span class="stat-value">${reports.activityTrends.recentJobs}</span>
                </div>
                <div class="stat-row">
                    <span class="stat-label">New Applications</span>
                    <span class="stat-value">${reports.activityTrends.recentApplications}</span>
                </div>
            </div>

            <div class="report-card" style="grid-column: 1 / -1;">
                <h3><i class="fas fa-map-marker-alt"></i> Jobs by Location (Top 5)</h3>
                <c:forEach items="${reports.jobStats.jobsByLocation}" var="entry">
                    <div class="stat-row">
                        <span class="stat-label">${entry.key}</span>
                        <span class="stat-value">${entry.value} jobs</span>
                    </div>
                </c:forEach>
                <c:if test="${empty reports.jobStats.jobsByLocation}">
                    <div class="chart-placeholder">
                        <i class="fas fa-chart-bar" style="font-size: 3rem;"></i>
                        <p>No location data available</p>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/views/components/admin-footer.jsp" />

</body>
</html>
