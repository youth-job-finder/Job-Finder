<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management - JobFinder Admin</title>
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
            <h2><i class="fas fa-users"></i> User Management</h2>
            <div class="search-filter">
                <form method="get" action="${currentUri}">
                    <input type="text" name="search" placeholder="Search users..." value="${param.search}">
                    <select name="role">
                        <option value="">All Roles</option>
                        <option value="APPLICANT" ${param.role == 'APPLICANT' ? 'selected' : ''}>Applicant</option>
                        <option value="COMPANY_ADMIN" ${param.role == 'COMPANY_ADMIN' ? 'selected' : ''}>Company Admin</option>
                        <option value="SYSTEM_ADMIN" ${param.role == 'SYSTEM_ADMIN' ? 'selected' : ''}>System Admin</option>
                    </select>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-search"></i> Filter
                    </button>
                </form>
            </div>
        </div>

        <div class="stats-cards">
            <div class="stat-card">
                <h4>Total Users</h4>
                <div class="number">${userCount}</div>
            </div>
            <div class="stat-card">
                <h4>Applicants</h4>
                <div class="number">
                    <c:set var="applicantCount" value="0" />
                    <c:forEach items="${users}" var="u">
                        <c:if test="${u.role == 'APPLICANT'}"><c:set var="applicantCount" value="${applicantCount + 1}" /></c:if>
                    </c:forEach>
                    ${applicantCount}
                </div>
            </div>
            <div class="stat-card">
                <h4>Company Admins</h4>
                <div class="number">
                    <c:set var="companyAdminCount" value="0" />
                    <c:forEach items="${users}" var="u">
                        <c:if test="${u.role == 'COMPANY_ADMIN'}"><c:set var="companyAdminCount" value="${companyAdminCount + 1}" /></c:if>
                    </c:forEach>
                    ${companyAdminCount}
                </div>
            </div>
        </div>

        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success">
                ${sessionScope.successMessage}
                <% session.removeAttribute("successMessage"); %>
            </div>
        </c:if>

        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-error">
                ${sessionScope.errorMessage}
                <% session.removeAttribute("errorMessage"); %>
            </div>
        </c:if>

        <div class="dashboard-card">
            <h3>All Users</h3>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Status</th>
                        <th>Created</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${users}" var="user">
                        <tr>
                            <td>${user.name}</td>
                            <td>${user.email}</td>
                            <td>
                                <span class="role-badge role-${user.role.name().toLowerCase().replace('_', '_')}">
                                    ${user.role}
                                </span>
                            </td>
                            <td>
                                <span class="status-badge ${user.emailVerified ? 'status-verified' : 'status-unverified'}">
                                    ${user.emailVerified ? 'Verified' : 'Unverified'}
                                </span>
                            </td>
                            <td>${user.createdAt}</td>
                            <td>
                                <div class="action-buttons">
                                    <form method="post" style="display: inline;" 
                                          onsubmit="return confirm('Are you sure you want to delete this user?');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="userId" value="${user.id}">
                                        <button type="submit" class="btn-delete" title="Delete">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <c:if test="${empty users}">
                <p style="text-align: center; padding: 2rem; color: #666;">
                    No users found matching your criteria.
                </p>
            </c:if>
            
            <!-- Pagination -->
            <c:if test="${pagination.totalPages > 1}">
                <div class="pagination" style="display: flex; justify-content: center; align-items: center; gap: 0.5rem; margin-top: 2rem;">
                    <c:url var="baseUrl" value="/admin/users">
                        <c:if test="${not empty param.role}"><c:param name="role" value="${param.role}"/></c:if>
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
                    Showing ${pagination.startIndex} - ${pagination.endIndex} of ${pagination.totalItems} users
                </div>
            </c:if>
        </div>
    </div>
</section>

<jsp:include page="/views/components/admin-footer.jsp" />

</body>
</html>
