<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Job Listings - Youth Job & Internship Finder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/company-navbar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-management.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/company-listings.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/views/components/company-navbar.jsp" />

<section class="listings">
    <div class="listings-container">
        <h2><i class="fas fa-list"></i> Job Listings</h2>
        <p>Manage your company's job postings and track their performance.</p>

        <!-- Success Message -->
        <c:if test="${not empty param.success}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i>
                <span>${param.success}</span>
            </div>
        </c:if>

        <!-- Error Message -->
        <c:if test="${not empty param.error}">
            <div class="alert alert-error">
                <i class="fas fa-exclamation-circle"></i>
                <span>${param.error}</span>
            </div>
        </c:if>

        <c:if test="${not empty company}">
            <!-- Actions Bar -->
            <div class="actions-bar">
                <a href="${pageContext.request.contextPath}/company/list-job" class="btn btn-primary">
                    <i class="fas fa-plus"></i> Post New Job
                </a>
            </div>

            <!-- Jobs Table -->
            <div class="dashboard-card full-width">
                <c:choose>
                    <c:when test="${empty jobs}">
                        <div class="no-listings">
                            <i class="fas fa-folder-open"></i>
                            <h3>No Job Listings Yet</h3>
                            <p>You haven't posted any jobs yet. Create your first job posting to start receiving applications!</p>
                            <a href="${pageContext.request.contextPath}/company/list-job" class="btn btn-primary">
                                <i class="fas fa-plus"></i> Create First Job
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <h3>All Jobs</h3>
                        <div class="table-responsive">
                            <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Job Title</th>
                                    <th>Location</th>
                                    <th>Posted Date</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${jobs}" var="job">
                                    <tr>
                                        <td>
                                            <div class="job-title">${job.jobTitle}</div>
                                            <div class="job-description-preview">
                                                ${job.jobDescription.length() > 50 ? job.jobDescription.substring(0, 50).concat('...') : job.jobDescription}
                                            </div>
                                        </td>
                                        <td>${job.physicalAddress}</td>
                                        <td>${job.createdAt}</td>
                                        <td>
                                            <div class="action-buttons">
                                                <a href="${pageContext.request.contextPath}/job/${job.id}" class="btn-view">
                                                    <i class="fas fa-eye"></i> View
                                                </a>
                                                <a href="${pageContext.request.contextPath}/company/applicants?jobId=${job.id}" class="btn-edit">
                                                    <i class="fas fa-users"></i> Applicants
                                                </a>
                                                <a href="${pageContext.request.contextPath}/company/edit-job?id=${job.id}" class="btn-edit">
                                                    <i class="fas fa-edit"></i> Edit
                                                </a>
                                                <form action="${pageContext.request.contextPath}/company/listings" method="post" class="inline-form" 
                                                      onsubmit="return confirm('Delete this job posting?');">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="jobId" value="${job.id}">
                                                    <button type="submit" class="btn-delete">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
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

        <!-- Back Button -->
        <div class="listings-actions">
            <a href="${pageContext.request.contextPath}/company/dashboard" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Back to Dashboard
            </a>
        </div>
    </div>
</section>

<jsp:include page="/views/components/company-footer.jsp" />

</body>
</html>
