<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - JobFinder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/company-navbar.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/views/components/system-navbar.jsp" />

<section class="dashboard">
    <div class="dashboard-container">
        <h2><i class="fas fa-tachometer-alt"></i> System Administrator Dashboard</h2>
        <p style="color: #666; margin-bottom: 2rem;">
            Welcome back, Administrator. Manage the entire JobFinder platform from this central dashboard.
        </p>

        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
                <% session.removeAttribute("successMessage"); %>
            </div>
        </c:if>

        <!-- Quick Stats Overview -->
        <div class="quick-stats">
            <div class="quick-stat-card">
                <i class="fas fa-users"></i>
                <h4>Total Users</h4>
                <div class="number">${adminStats['totalUsers']}</div>
            </div>
            <div class="quick-stat-card">
                <i class="fas fa-building"></i>
                <h4>Companies</h4>
                <div class="number">${adminStats['totalCompanies']}</div>
            </div>
            <div class="quick-stat-card">
                <i class="fas fa-briefcase"></i>
                <h4>Job Postings</h4>
                <div class="number">${adminStats['totalJobs']}</div>
            </div>
            <div class="quick-stat-card pending">
                <i class="fas fa-clock"></i>
                <h4>Pending Applications</h4>
                <div class="number">${adminStats['pendingApplications']}</div>
            </div>
            <div class="quick-stat-card success">
                <i class="fas fa-check-circle"></i>
                <h4>This Week's Growth</h4>
                <div class="number">+${adminStats['newUsersThisWeek'] + adminStats['newCompaniesThisWeek'] + adminStats['newJobsThisWeek']}</div>
            </div>
        </div>

        <!-- Management Cards -->
        <div class="management-cards">
            <div class="management-card">
                <h3><i class="fas fa-users-cog"></i> User Management</h3>
                <p>View, manage, and moderate all user accounts. Delete accounts, change roles, and monitor user activity.</p>
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-primary">
                    <i class="fas fa-arrow-right"></i> Manage Users
                </a>
            </div>

            <div class="management-card">
                <h3><i class="fas fa-building"></i> Company Management</h3>
                <p>Approve or reject company registrations, view company details, and manage company status.</p>
                <a href="${pageContext.request.contextPath}/admin/companies" class="btn btn-primary">
                    <i class="fas fa-arrow-right"></i> Manage Companies
                </a>
            </div>

            <div class="management-card">
                <h3><i class="fas fa-briefcase"></i> Job Management</h3>
                <p>Monitor and moderate job postings. Remove inappropriate listings and view job statistics.</p>
                <a href="${pageContext.request.contextPath}/admin/jobs" class="btn btn-primary">
                    <i class="fas fa-arrow-right"></i> Manage Jobs
                </a>
            </div>

            <div class="management-card">
                <h3><i class="fas fa-chart-bar"></i> Reports & Analytics</h3>
                <p>View comprehensive system statistics, user trends, and platform analytics.</p>
                <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-primary">
                    <i class="fas fa-arrow-right"></i> View Reports
                </a>
            </div>

            <div class="management-card">
                <h3><i class="fas fa-cog"></i> System Settings</h3>
                <p>Configure platform settings, security options, and system preferences.</p>
                <a href="${pageContext.request.contextPath}/admin/settings" class="btn btn-primary">
                    <i class="fas fa-arrow-right"></i> Settings
                </a>
            </div>

            <div class="management-card">
                <h3><i class="fas fa-clipboard-check"></i> Pending Approvals</h3>
                <p>Quick access to pending company registrations awaiting your review.</p>
                <a href="${pageContext.request.contextPath}/admin/companies/pending" class="btn btn-warning" style="background-color: #ff9800; color: white;">
                    <i class="fas fa-clock"></i> View Pending
                </a>
            </div>
        </div>

        <!-- Recent Activity -->
        <div class="recent-activity">
            <h3><i class="fas fa-history"></i> Recent System Activity</h3>
            <c:forEach items="${adminActivities}" var="activity">
                <div class="activity-item">
                    <div class="activity-icon ${activity.iconColor}">
                        <i class="fas ${activity.icon}"></i>
                    </div>
                    <div class="activity-content">
                        <div class="activity-title">${activity.title}</div>
                        <div class="activity-meta">${activity.description} - ${activity.date}</div>
                    </div>
                </div>
            </c:forEach>
            <c:if test="${empty adminActivities}">
                <div class="activity-item">
                    <div class="activity-icon blue">
                        <i class="fas fa-info-circle"></i>
                    </div>
                    <div class="activity-content">
                        <div class="activity-title">System Operational</div>
                        <div class="activity-meta">All systems running normally - Today</div>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</section>

<jsp:include page="/views/components/admin-footer.jsp" />

</body>
</html>

