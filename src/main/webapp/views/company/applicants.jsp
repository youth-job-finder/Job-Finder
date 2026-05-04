<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Job Applicants - Youth Job & Internship Finder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css?v=4">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/company-navbar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/company-applicants.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/views/components/company-navbar.jsp" />

<section class="applicants">
    <div class="applicants-container">
        <h2><i class="fas fa-users"></i> Job Applicants</h2>
        <p>Review and manage applications from candidates interested in your job postings.</p>

        <!-- Success Message -->
        <c:if test="${not empty param.success}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i>
                <span>${param.success}</span>
            </div>
        </c:if>

        <c:if test="${not empty company}">
            <!-- Stats Summary -->
            <div class="applicants-summary">
                <div class="summary-card">
                    <div class="summary-icon">
                        <i class="fas fa-user-clock"></i>
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
                <div class="summary-card">
                    <div class="summary-icon approved">
                        <i class="fas fa-user-check"></i>
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
                <div class="summary-card">
                    <div class="summary-icon rejected">
                        <i class="fas fa-user-times"></i>
                    </div>
                    <div class="summary-content">
                        <h4>Rejected</h4>
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

            <!-- Applications Table -->
            <div class="applicants-card">
                <c:choose>
                    <c:when test="${empty applications}">
                        <div class="no-applicants">
                            <i class="fas fa-inbox"></i>
                            <h3>No Applications Yet</h3>
                            <p>You haven't received any applications yet. Applications will appear here once candidates apply to your job postings.</p>
                            <a href="${pageContext.request.contextPath}/company/listings" class="btn btn-primary">
                                <i class="fas fa-list"></i> View Job Listings
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="applicants-table">
                                <thead>
                                    <tr>
                                        <th>Applicant</th>
                                        <th>Job Position</th>
                                        <th>Applied Date</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${applications}" var="application">
                                        <tr>
                                            <td class="applicant-info">
                                                <div class="applicant-avatar">
                                                    <i class="fas fa-user-circle"></i>
                                                </div>
                                                <div class="applicant-details">
                                                    <span class="applicant-name">
                                                        ${application.applicant.name}
                                                    </span>
                                                    <span class="applicant-email">${application.applicant.email}</span>
                                                    <span class="applicant-id">ID: ${application.applicant.id}</span>
                                                </div>
                                            </td>
                                            <td class="job-position">
                                                <i class="fas fa-briefcase"></i>
                                                ${application.job.jobTitle}
                                            </td>
                                            <td class="applied-date">${application.created_at}</td>
                                            <td class="application-status">
                                                <span class="status-badge ${application.applicationStatus.name().toLowerCase()}">
                                                    ${application.applicationStatus.name()}
                                                </span>
                                            </td>
                                            <td class="applicant-actions">
                                                <!-- Status Update Forms -->
                                                <form action="${pageContext.request.contextPath}/company/applicants" method="post" class="action-form">
                                                    <input type="hidden" name="action" value="updateStatus">
                                                    <input type="hidden" name="applicationId" value="${application.id}">
                                                    <select name="status" class="status-select" onchange="this.form.submit()">
                                                        <option value="PENDING" ${application.applicationStatus == 'PENDING' ? 'selected' : ''}>Pending</option>
                                                        <option value="APPROVED" ${application.applicationStatus == 'APPROVED' ? 'selected' : ''}>Approve</option>
                                                        <option value="REJECTED" ${application.applicationStatus == 'REJECTED' ? 'selected' : ''}>Reject</option>
                                                    </select>
                                                </form>
                                                
                                                <!-- CV Download -->
                                                <form action="${pageContext.request.contextPath}/company/applicants" method="post" class="action-form">
                                                    <input type="hidden" name="action" value="downloadCV">
                                                    <input type="hidden" name="applicationId" value="${application.id}">
                                                    <button type="submit" class="btn btn-sm btn-info" title="Download CV">
                                                        <i class="fas fa-download"></i> CV
                                                    </button>
                                                </form>
                                                
                                                <!-- CV View in New Tab -->
                                                <a href="${pageContext.request.contextPath}/view-cv?applicationId=${application.id}" 
                                                   target="_blank" class="btn btn-sm btn-info" title="View CV">
                                                    <i class="fas fa-file-alt"></i> View CV
                                                </a>
                                                
                                                <!-- View Details -->
                                                <button class="btn btn-sm btn-secondary" title="View Applicant Details" onclick="viewApplicantDetails('${application.applicant.id}', '${application.applicant.name}', '${application.applicant.email}', '${application.applicant.phone}')">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        
                        <!-- Pagination -->
                        <c:if test="${pagination.totalPages > 1}">
                            <div class="pagination" style="display: flex; justify-content: center; align-items: center; gap: 0.5rem; margin-top: 2rem;">
                                <c:url var="baseUrl" value="/company/applicants">
                                    <c:if test="${not empty selectedJobId}"><c:param name="jobId" value="${selectedJobId}"/></c:if>
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
        <div class="applicants-actions">
            <a href="${pageContext.request.contextPath}/company/dashboard" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Back to Dashboard
            </a>
        </div>
    </div>
</section>

<jsp:include page="/views/components/company-footer.jsp" />

<!-- Applicant Details Modal -->
<div id="applicantModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3><i class="fas fa-user"></i> Applicant Details</h3>
            <span class="close" onclick="closeModal()">&times;</span>
        </div>
        <div class="modal-body">
            <div class="detail-row">
                <span class="detail-label">Name:</span>
                <span class="detail-value" id="modalName"></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Email:</span>
                <span class="detail-value" id="modalEmail"></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Phone:</span>
                <span class="detail-value" id="modalPhone"></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">User ID:</span>
                <span class="detail-value" id="modalId"></span>
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn btn-secondary" onclick="closeModal()">Close</button>
        </div>
    </div>
</div>

<script>
function viewApplicantDetails(id, name, email, phone) {
    document.getElementById('modalId').textContent = id;
    document.getElementById('modalName').textContent = name;
    document.getElementById('modalEmail').textContent = email || 'Not provided';
    document.getElementById('modalPhone').textContent = phone || 'Not provided';
    document.getElementById('applicantModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('applicantModal').style.display = 'none';
}

// Close modal when clicking outside
window.onclick = function(event) {
    var modal = document.getElementById('applicantModal');
    if (event.target == modal) {
        modal.style.display = 'none';
    }
}
</script>

</body>
</html>
