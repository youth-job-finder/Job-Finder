<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Job Management - JobFinder Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css?v=4">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-management.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/views/components/system-navbar.jsp" />

<section class="dashboard">
    <div class="dashboard-container">
        <div class="management-header">
            <h2><i class="fas fa-briefcase"></i> Job Management</h2>
            <div class="search-filter">
                <form method="get" action="${currentUri}">
                    <input type="text" name="search" placeholder="Search job titles or descriptions..." value="${param.search}">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-search"></i> Search
                    </button>
                </form>
            </div>
        </div>

        <div class="stats-cards">
            <div class="stat-card">
                <h4>Total Jobs</h4>
                <div class="number">${jobCount}</div>
            </div>
        </div>

        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
                <% session.removeAttribute("successMessage"); %>
            </div>
        </c:if>

        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-error">
                <i class="fas fa-exclamation-circle"></i> ${sessionScope.errorMessage}
                <% session.removeAttribute("errorMessage"); %>
            </div>
        </c:if>

        <div class="dashboard-card full-width">
            <h3>All Jobs</h3>
            <div class="table-responsive">
                <table class="data-table">
                <thead>
                    <tr>
                        <th>Job Title</th>
                        <th>Company</th>
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
                            <td class="company-name">${job.company.name}</td>
                            <td>${job.physicalAddress}</td>
                            <td>${job.created_at}</td>
                            <td>
                                <div class="action-buttons">
                                    <a href="${pageContext.request.contextPath}/job/${job.id}" class="btn-view">
                                        <i class="fas fa-eye"></i> View
                                    </a>
                                    <form method="post" class="inline-form" 
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
            <c:if test="${empty jobs}">
                <p class="no-data-message">
                    <i class="fas fa-info-circle"></i> No jobs found matching your criteria.
                </p>
            </c:if>
            
            <!-- Pagination -->
            <c:if test="${pagination.totalPages > 1}">
                <div class="pagination" style="display: flex; justify-content: center; align-items: center; gap: 0.5rem; margin-top: 2rem;">
                    <c:url var="baseUrl" value="/admin/jobs">
                        <c:if test="${not empty param.search}"><c:param name="search" value="${param.search}"/></c:if>
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
                    Showing ${pagination.startIndex} - ${pagination.endIndex} of ${pagination.totalItems} jobs
                </div>
            </c:if>
        </div>
    </div>
</section>

<jsp:include page="/views/components/admin-footer.jsp" />

</body>
</html>
