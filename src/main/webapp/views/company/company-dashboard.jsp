<%--
  Created by IntelliJ IDEA.
  User: aubre
  Date: 2026/03/24
  Time: 23:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Company Dashboard - Youth Job & Internship Finder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/company-navbar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-management.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/company-dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/views/components/company-navbar.jsp" />

<section class="dashboard">
    <div class="dashboard-container">
        <h2><i class="fas fa-tachometer-alt"></i> Company Dashboard</h2>
        <p class="dashboard-subtitle">
            Welcome back, ${sessionScope.companyName}. Manage your job listings, applicants, and company profile from this central dashboard.
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
                <i class="fas fa-briefcase"></i>
                <h4>Total Jobs</h4>
                <div class="number">${companyStats['totalJobs']}</div>
            </div>
            <div class="quick-stat-card">
                <i class="fas fa-users"></i>
                <h4>Total Applications</h4>
                <div class="number">${companyStats['totalApplications']}</div>
            </div>
            <div class="quick-stat-card pending">
                <i class="fas fa-clock"></i>
                <h4>Pending Applications</h4>
                <div class="number">${companyStats['pendingApplications']}</div>
            </div>
            <div class="quick-stat-card success">
                <i class="fas fa-check-circle"></i>
                <h4>Approved</h4>
                <div class="number">${companyStats['approvedApplications']}</div>
            </div>
            <div class="quick-stat-card">
                <i class="fas fa-calendar-plus"></i>
                <h4>New Jobs (30 days)</h4>
                <div class="number">${companyStats['recentJobs']}</div>
            </div>
        </div>

        <!-- Management Cards -->
        <div class="management-cards">
            <div class="management-card">
                <h3><i class="fas fa-briefcase"></i> Job Listings</h3>
                <p>Manage all your job postings, edit details, and track status. View applications for each position.</p>
                <a href="${pageContext.request.contextPath}/company/listings" class="btn btn-primary">
                    <i class="fas fa-arrow-right"></i> View Listings
                </a>
            </div>

            <div class="management-card">
                <h3><i class="fas fa-users"></i> Applicants</h3>
                <p>Review candidate applications, update their status, and manage your hiring pipeline.</p>
                <a href="${pageContext.request.contextPath}/company/applicants" class="btn btn-primary">
                    <i class="fas fa-arrow-right"></i> View Applicants
                </a>
            </div>

            <div class="management-card">
                <h3><i class="fas fa-chart-bar"></i> Analytics</h3>
                <p>View insights on job views, application rates, and your overall hiring performance.</p>
                <a href="${pageContext.request.contextPath}/company/analytics" class="btn btn-primary">
                    <i class="fas fa-arrow-right"></i> View Analytics
                </a>
            </div>

            <div class="management-card">
                <h3><i class="fas fa-star"></i> Reviews</h3>
                <p>Manage company reviews, respond to feedback, and build your employer brand.</p>
                <a href="${pageContext.request.contextPath}/company/reviews" class="btn btn-primary">
                    <i class="fas fa-arrow-right"></i> View Reviews
                </a>
            </div>

            <div class="management-card">
                <h3><i class="fas fa-building"></i> Company Profile</h3>
                <p>Update your company information, description, industry, and public profile details.</p>
                <a href="${pageContext.request.contextPath}/company/profile" class="btn btn-primary">
                    <i class="fas fa-arrow-right"></i> Edit Profile
                </a>
            </div>

            <div class="management-card">
                <h3><i class="fas fa-plus-circle"></i> Post New Job</h3>
                <p>Create a new job listing to attract qualified candidates to your open positions.</p>
                <a href="${pageContext.request.contextPath}/company/list-job" class="btn btn-primary">
                    <i class="fas fa-arrow-right"></i> Create Job
                </a>
            </div>
        </div>

        <!-- Recent Applications -->
        <div class="dashboard-card">
            <h3><i class="fas fa-users"></i> Recent Applications</h3>
            <div class="applications-list">
                <c:choose>
                    <c:when test="${empty recentApplications}">
                        <div class="no-applications">
                            <p>No recent applications to display.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${recentApplications}" var="application">
                            <div class="application-item">
                                <div class="applicant-info">
                                    <strong>${application['applicantName']}</strong>
                                    <p>Applied for: ${application['jobTitle']}</p>
                                </div>
                                <div class="application-status">
                                    <span class="status-badge ${application['statusClass']}">${application['status']}</span>
                                    <small>${application['appliedDate']}</small>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Recent Jobs -->
        <div class="dashboard-card full-width">
            <h3><i class="fas fa-briefcase"></i> Recent Job Listings</h3>
            <div class="table-responsive">
                <table class="data-table">
                <thead>
                    <tr>
                        <th>Job Title</th>
                        <th>Location</th>
                        <th>Posted Date</th>
                        <th>Applications</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${recentJobs}" var="job">
                        <tr>
                            <td>
                                <div class="job-title">${job['title']}</div>
                                <div class="job-description-preview">
                                    ${job['description'].length() > 50 ? job['description'].substring(0, 50).concat('...') : job['description']}
                                </div>
                            </td>
                            <td>${job['location']}</td>
                            <td>${job['postedDate']}</td>
                            <td><span class="badge">${job['applicationCount']}</span></td>
                            <td>
                                <div class="action-buttons">
                                    <a href="${pageContext.request.contextPath}/job/${job['id']}" class="btn-view">
                                        <i class="fas fa-eye"></i> View
                                    </a>
                                    <a href="${pageContext.request.contextPath}/company/applicants?jobId=${job['id']}" class="btn-edit">
                                        <i class="fas fa-users"></i> Applicants
                                    </a>
                                    <a href="${pageContext.request.contextPath}/company/edit-job?id=${job['id']}" class="btn-edit">
                                        <i class="fas fa-edit"></i> Edit
                                    </a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
                </table>
            </div>
            <c:if test="${empty recentJobs}">
                <p class="no-data-message">
                    <i class="fas fa-info-circle"></i> No jobs posted yet.
                    <a href="${pageContext.request.contextPath}/company/list-job" class="btn btn-primary btn-sm ml-1">
                        <i class="fas fa-plus"></i> Post First Job
                    </a>
                </p>
            </c:if>
        </div>
    </div>
</section>

<jsp:include page="/views/components/company-footer.jsp" />

</body>
</html>
