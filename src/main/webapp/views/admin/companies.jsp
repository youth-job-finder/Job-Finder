<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Company Management - JobFinder Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
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
            <h2><i class="fas fa-building"></i> Company Management</h2>
            <div class="search-filter">
                <form method="get" action="${currentUri}">
                    <input type="text" name="search" placeholder="Search companies..." value="${param.search}">
                    <select name="status">
                        <option value="">All Status</option>
                        <option value="PENDING" ${param.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                        <option value="APPROVED" ${param.status == 'APPROVED' ? 'selected' : ''}>Approved</option>
                        <option value="REJECTED" ${param.status == 'REJECTED' ? 'selected' : ''}>Rejected</option>
                    </select>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-search"></i> Filter
                    </button>
                </form>
            </div>
        </div>

        <div class="stats-cards">
            <div class="stat-card total">
                <h4>Total Companies</h4>
                <div class="number">${companyCount}</div>
            </div>
            <div class="stat-card pending">
                <h4>Pending</h4>
                <div class="number">${pendingCount}</div>
            </div>
            <div class="stat-card approved">
                <h4>Approved</h4>
                <div class="number">${approvedCount}</div>
            </div>
            <div class="stat-card rejected">
                <h4>Rejected</h4>
                <div class="number">${rejectedCount}</div>
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

        <div class="dashboard-card">
            <h3>All Companies</h3>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Company Name</th>
                        <th>Registration #</th>
                        <th>Email</th>
                        <th>Status</th>
                        <th>URL Verified</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${companies}" var="company">
                        <tr>
                            <td>${company.name}</td>
                            <td>${company.registrationNumber}</td>
                            <td>${company.email}</td>
                            <td>
                                <span class="status-badge status-${company.status.name().toLowerCase()}">
                                    ${company.status}
                                </span>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${company.urlVerified}">
                                        <i class="fas fa-check-circle" style="color: #4caf50;"></i> Yes
                                    </c:when>
                                    <c:otherwise>
                                        <i class="fas fa-times-circle" style="color: #f44336;"></i> No
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div class="action-buttons">
                                    <c:if test="${company.status == 'PENDING'}">
                                        <form method="post" style="display: inline;">
                                            <input type="hidden" name="action" value="approve">
                                            <input type="hidden" name="companyId" value="${company.id}">
                                            <button type="submit" class="btn-approve">
                                                <i class="fas fa-check"></i> Approve
                                            </button>
                                        </form>
                                        <form method="post" style="display: inline;">
                                            <input type="hidden" name="action" value="reject">
                                            <input type="hidden" name="companyId" value="${company.id}">
                                            <input type="hidden" name="reason" value="Rejected by admin">
                                            <button type="submit" class="btn-reject">
                                                <i class="fas fa-times"></i> Reject
                                            </button>
                                        </form>
                                    </c:if>
                                    <form method="post" style="display: inline;" 
                                          onsubmit="return confirm('Delete this company?');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="companyId" value="${company.id}">
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
            <c:if test="${empty companies}">
                <p style="text-align: center; padding: 2rem; color: #666;">
                    <i class="fas fa-info-circle"></i> No companies found matching your criteria.
                </p>
            </c:if>
        </div>

        <div class="dashboard-actions" style="margin-top: 2rem;">
            <a href="${pageContext.request.contextPath}/admin/companies/pending" class="btn btn-primary">
                <i class="fas fa-clock"></i> View Pending Approvals
            </a>
        </div>
    </div>
</section>

<jsp:include page="/views/components/admin-footer.jsp" />

</body>
</html>
