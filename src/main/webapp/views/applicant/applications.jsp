<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Applications - Youth Job & Internship Finder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css?v=4">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/applicant-applications.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/views/components/user-navbar.jsp" />

<section class="page-section">
    <div class="container">
        <h1><i class="fas fa-list-alt"></i> My Applications</h1>
        <p>Track the status of your job applications in real-time.</p>

        <!-- Success Message -->
        <c:if test="${not empty param.success}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i>
                <span>${param.success}</span>
            </div>
        </c:if>

        <!-- Stats Summary -->
        <c:if test="${not empty applications}">
            <div class="applications-summary">
                <div class="summary-card total">
                    <div class="summary-icon">
                        <i class="fas fa-file-alt"></i>
                    </div>
                    <div class="summary-content">
                        <h4>Total Applications</h4>
                        <p class="summary-number">${applications.size()}</p>
                    </div>
                </div>
                <div class="summary-card pending">
                    <div class="summary-icon">
                        <i class="fas fa-clock"></i>
                    </div>
                    <div class="summary-content">
                        <h4>Pending Review</h4>
                        <p class="summary-number">
                            <c:set var="pendingCount" value="0" />
                            <c:forEach items="${applications}" var="app">
                                <c:if test="${app.applicationStatus == 'PENDING'}">
                                    <c:set var="pendingCount" value="${pendingCount + 1}" />
                                </c:if>
                            </c:forEach>
                            ${pendingCount}
                        </p>
                    </div>
                </div>
                <div class="summary-card approved">
                    <div class="summary-icon">
                        <i class="fas fa-check-circle"></i>
                    </div>
                    <div class="summary-content">
                        <h4>Approved</h4>
                        <p class="summary-number">
                            <c:set var="approvedCount" value="0" />
                            <c:forEach items="${applications}" var="app">
                                <c:if test="${app.applicationStatus == 'APPROVED'}">
                                    <c:set var="approvedCount" value="${approvedCount + 1}" />
                                </c:if>
                            </c:forEach>
                            ${approvedCount}
                        </p>
                    </div>
                </div>
                <div class="summary-card rejected">
                    <div class="summary-icon">
                        <i class="fas fa-times-circle"></i>
                    </div>
                    <div class="summary-content">
                        <h4>Not Selected</h4>
                        <p class="summary-number">
                            <c:set var="rejectedCount" value="0" />
                            <c:forEach items="${applications}" var="app">
                                <c:if test="${app.applicationStatus == 'REJECTED'}">
                                    <c:set var="rejectedCount" value="${rejectedCount + 1}" />
                                </c:if>
                            </c:forEach>
                            ${rejectedCount}
                        </p>
                    </div>
                </div>
            </div>
        </c:if>

        <c:if test="${empty applications}">
            <div class="empty-state">
                <i class="fas fa-inbox fa-3x"></i>
                <h3>No Applications Yet</h3>
                <p>You haven't applied to any jobs yet. Start browsing and apply to jobs that match your skills!</p>
                <a href="${pageContext.request.contextPath}/jobs" class="btn btn-primary">Browse Jobs</a>
            </div>
        </c:if>

        <c:if test="${not empty applications}">
            <div class="applications-list">
                <c:forEach items="${applications}" var="app">
                    <div class="application-card">
                        <div class="application-header">
                            <div class="job-info">
                                <h3>${app.job.jobTitle}</h3>
                                <span class="company-name">
                                    <i class="fas fa-building"></i> ${app.job.company.name}
                                </span>
                            </div>
                            <span class="status-badge ${app.applicationStatus.name().toLowerCase()}">
                                <i class="fas 
                                    ${app.applicationStatus == 'PENDING' ? 'fa-clock' : 
                                      app.applicationStatus == 'APPROVED' ? 'fa-check' : 'fa-times'}"></i>
                                ${app.applicationStatus.name()}
                            </span>
                        </div>
                        <div class="application-details">
                            <div class="detail-item">
                                <i class="fas fa-calendar"></i>
                                <span>Applied: ${app.created_at}</span>
                            </div>
                            <div class="detail-item">
                                <i class="fas fa-map-marker-alt"></i>
                                <span>${app.job.physicalAddress != null ? app.job.physicalAddress : 'Location not specified'}</span>
                            </div>
                            <div class="detail-item application-id">
                                <i class="fas fa-hashtag"></i>
                                <span>Application ID: ${app.id}</span>
                            </div>
                        </div>
                        <div class="application-actions">
                            <a href="${pageContext.request.contextPath}/job/${app.job.id}" class="btn btn-sm btn-secondary">
                                <i class="fas fa-eye"></i> View Job
                            </a>
                            <c:if test="${app.applicationStatus == 'APPROVED'}">
                                <span class="success-message">
                                    <i class="fas fa-trophy"></i> Congratulations! You have been shortlisted.
                                </span>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </div>
            
            <!-- Pagination -->
            <c:if test="${pagination.totalPages > 1}">
                <div class="pagination" style="display: flex; justify-content: center; align-items: center; gap: 0.5rem; margin-top: 2rem;">
                    <c:url var="baseUrl" value="/applicant/applications">
                        <c:if test="${not empty param.filter}"><c:param name="filter" value="${param.filter}"/></c:if>
                    </c:url>
                    <c:set var="pageSeparator" value="${fn:contains(baseUrl, '?') ? '&' : '?'}"></c:set>
                    
                    <!-- Previous Page -->
                    <c:choose>
                        <c:when test="${pagination.hasPreviousPage}">
                            <a href="${baseUrl}${pageSeparator}page=${pagination.currentPage - 1}" class="pagination-btn" style="padding: 0.5rem 1rem; border-radius: 8px; background: rgba(255,255,255,0.05); color: #d4af37; text-decoration: none; border: 1px solid rgba(212,175,55,0.3);">
                                <i class="fas fa-chevron-left"></i>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <span class="pagination-btn disabled" style="padding: 0.5rem 1rem; border-radius: 8px; background: rgba(255,255,255,0.03); color: #666; border: 1px solid rgba(212,175,55,0.2); cursor: not-allowed;">
                                <i class="fas fa-chevron-left"></i>
                            </span>
                        </c:otherwise>
                    </c:choose>
                    
                    <!-- Page Numbers -->
                    <c:forEach begin="1" end="${pagination.totalPages}" var="pageNum">
                        <c:choose>
                            <c:when test="${pageNum == pagination.currentPage}">
                                <span class="pagination-btn active" style="padding: 0.5rem 1rem; border-radius: 8px; background: #d4af37; color: #0f172a; font-weight: 700; min-width: 40px; text-align: center;">${pageNum}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="${baseUrl}${pageSeparator}page=${pageNum}" class="pagination-btn" style="padding: 0.5rem 1rem; border-radius: 8px; background: rgba(255,255,255,0.05); color: #d4af37; text-decoration: none; border: 1px solid rgba(212,175,55,0.3); min-width: 40px; text-align: center;">${pageNum}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    
                    <!-- Next Page -->
                    <c:choose>
                        <c:when test="${pagination.hasNextPage}">
                            <a href="${baseUrl}${pageSeparator}page=${pagination.currentPage + 1}" class="pagination-btn" style="padding: 0.5rem 1rem; border-radius: 8px; background: rgba(255,255,255,0.05); color: #d4af37; text-decoration: none; border: 1px solid rgba(212,175,55,0.3);">
                                <i class="fas fa-chevron-right"></i>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <span class="pagination-btn disabled" style="padding: 0.5rem 1rem; border-radius: 8px; background: rgba(255,255,255,0.03); color: #666; border: 1px solid rgba(212,175,55,0.2); cursor: not-allowed;">
                                <i class="fas fa-chevron-right"></i>
                            </span>
                        </c:otherwise>
                    </c:choose>
                </div>
                
                <!-- Showing X of Y results -->
                <div style="text-align: center; margin-top: 1rem; color: #94a3b8; font-size: 0.9rem;">
                    Showing ${pagination.startIndex} - ${pagination.endIndex} of ${pagination.totalItems} applications
                </div>
            </c:if>
        </c:if>
    </div>
</section>

<jsp:include page="/views/components/applicant-footer.jsp" />

</body>
</html>
