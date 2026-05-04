<%--
  Admin Pending Companies Approval Page
  
  Displays a list of companies with PENDING status awaiting system admin approval.
  System admins can approve or reject company registrations.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%-- LocalDateTime is not supported by fmt:formatDate (expects java.util.Date) --%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pending Company Approvals - Admin - JobFinder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css?v=4">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<jsp:include page="/views/components/system-navbar.jsp" />

<section class="admin-section">
    <div class="admin-container">
        <h2><i class="fas fa-building"></i> Pending Company Approvals</h2>
        <p class="section-description">Review and approve company registrations</p>

        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i> ${successMessage}
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error">
                <i class="fas fa-exclamation-circle"></i> ${errorMessage}
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty pendingCompanies}">
                <div class="empty-state">
                    <i class="fas fa-check-circle" style="font-size: 48px; color: #27ae60;"></i>
                    <h3>No Pending Companies</h3>
                    <p>All company registrations have been processed.</p>
                </div>
            </c:when>
            <c:otherwise>
                <p class="table-scroll-hint"><i class="fas fa-arrows-alt-h"></i> Scroll horizontally to see all columns</p>
                <div class="companies-table-container companies-table-container--scroll">
                    <table class="companies-table pending-approvals-table">
                        <thead>
                            <tr>
                                <th>Company</th>
                                <th>Registration #</th>
                                <th>Company Email</th>
                                <th>Website</th>
                                <th>URL Verified</th>
                                <th>Admin</th>
                                <th>Submitted</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="company" items="${pendingCompanies}">
                                <tr>
                                    <td>
                                        <strong>${company.name}</strong>
                                    </td>
                                    <td>${company.registrationNumber}</td>
                                    <td>
                                        <a href="mailto:${company.email}">${company.email}</a>
                                    </td>
                                    <td class="cell-website">
                                        <c:choose>
                                            <c:when test="${not empty company.url}">
                                                <a href="${company.url}" target="_blank" rel="noopener noreferrer" title="Visit website">
                                                    <i class="fas fa-external-link-alt"></i> ${company.url}
                                                </a>
                                            </c:when>
                                            <c:otherwise><span class="cell-empty">—</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${company.urlVerified}">
                                                <span class="badge badge-success">
                                                    <i class="fas fa-check"></i> Verified
                                                </span>
                                                <br>
                                                <small>
                                                    <c:choose>
                                                        <c:when test="${not empty company.urlVerifiedAt}">
                                                            ${fn:replace(fn:substring(company.urlVerifiedAt, 0, 16), 'T', ' ')}
                                                        </c:when>
                                                        <c:otherwise>—</c:otherwise>
                                                    </c:choose>
                                                </small>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-warning">
                                                    <i class="fas fa-times"></i> Not Verified
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="cell-admin">
                                        <c:if test="${not empty company.companyAdmin}">
                                            <span class="cell-admin-name">${company.companyAdmin.name}</span>
                                            <span class="cell-admin-email">${company.companyAdmin.email}</span>
                                        </c:if>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty company.createdAt}">
                                                ${fn:replace(fn:substring(company.createdAt, 0, 16), 'T', ' ')}
                                            </c:when>
                                            <c:otherwise>—</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="actions">
                                        <form action="${pageContext.request.contextPath}/admin/companies/approve" 
                                              method="post" style="display: inline;">
                                            <input type="hidden" name="companyId" value="${company.id}">
                                            <button type="submit" class="btn btn-success btn-sm">
                                                <i class="fas fa-check"></i> Approve
                                            </button>
                                        </form>
                                        
                                        <button type="button" class="btn btn-danger btn-sm" 
                                                onclick="showRejectModal('${company.id}', '${company.name}')">
                                            <i class="fas fa-times"></i> Reject
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>

        <div class="admin-actions">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Back to Dashboard
            </a>
        </div>
    </div>
</section>

<!-- Reject Modal -->
<div id="rejectModal" class="modal" style="display: none;">
    <div class="modal-content">
        <h3><i class="fas fa-exclamation-triangle"></i> Reject Company Registration</h3>
        <p>Are you sure you want to reject <strong id="rejectCompanyName"></strong>?</p>
        
        <form action="${pageContext.request.contextPath}/admin/companies/reject" method="post">
            <input type="hidden" name="companyId" id="rejectCompanyId">
            
            <div class="form-group">
                <label for="reason">Rejection Reason (optional):</label>
                <textarea id="reason" name="reason" rows="3" 
                          placeholder="Provide a reason for rejection (will be sent to the company admin)"></textarea>
            </div>
            
            <div class="modal-actions">
                <button type="button" class="btn btn-secondary" onclick="hideRejectModal()">Cancel</button>
                <button type="submit" class="btn btn-danger">Reject Registration</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />

<script>
function showRejectModal(companyId, companyName) {
    document.getElementById('rejectCompanyId').value = companyId;
    document.getElementById('rejectCompanyName').textContent = companyName;
    document.getElementById('rejectModal').style.display = 'flex';
}

function hideRejectModal() {
    document.getElementById('rejectModal').style.display = 'none';
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('rejectModal');
    if (event.target === modal) {
        hideRejectModal();
    }
}
</script>

<jsp:include page="/views/components/admin-footer.jsp" />

</body>
</html>
