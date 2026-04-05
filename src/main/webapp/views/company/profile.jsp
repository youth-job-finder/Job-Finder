<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Company Profile - Youth Job & Internship Finder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/company-navbar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/company-profile.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/views/components/company-navbar.jsp" />

<section class="profile-section">
    <div class="profile-container">
        <div class="profile-header">
            <h2><i class="fas fa-building"></i> Company Profile</h2>
            <p>View and manage your company information</p>
        </div>

        <!-- Success Message -->
        <c:if test="${not empty success}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i>
                <span>${success}</span>
            </div>
        </c:if>

        <!-- Error Message -->
        <c:if test="${not empty error}">
            <div class="alert alert-error">
                <i class="fas fa-exclamation-circle"></i>
                <span>${error}</span>
            </div>
        </c:if>

        <c:if test="${not empty company}">
            <div class="profile-card">
                <div class="profile-info-header">
                    <div class="company-icon">
                        <i class="fas fa-building"></i>
                    </div>
                    <div class="company-info">
                        <h3>${company.name}</h3>
                        <span class="status-badge ${company.status.toString().toLowerCase()}">
                            ${company.status}
                        </span>
                    </div>
                </div>

                <form action="${pageContext.request.contextPath}/company/profile" method="post" class="profile-form">
                    <div class="form-section">
                        <h4><i class="fas fa-info-circle"></i> Company Information</h4>
                        
                        <div class="form-group">
                            <label for="companyName">
                                <i class="fas fa-briefcase"></i> Company Name
                            </label>
                            <input type="text" id="companyName" name="companyName" 
                                   value="${company.name}" required 
                                   placeholder="Enter company name">
                        </div>

                        <div class="form-group">
                            <label for="registrationNumber">
                                <i class="fas fa-id-card"></i> Registration Number
                            </label>
                            <input type="text" id="registrationNumber" name="registrationNumber" 
                                   value="${company.registrationNumber}" required 
                                   placeholder="e.g., CAC/IT/123456">
                            <small class="form-hint">Your official business registration number</small>
                        </div>

                        <div class="form-group">
                            <label for="companyEmail">
                                <i class="fas fa-envelope"></i> Company Email
                            </label>
                            <input type="email" id="companyEmail" name="companyEmail" 
                                   value="${company.email}" required 
                                   placeholder="contact@company.com">
                        </div>

                        <div class="form-group">
                            <label for="companyUrl">
                                <i class="fas fa-globe"></i> Company Website
                            </label>
                            <input type="url" id="companyUrl" name="companyUrl" 
                                   value="${company.url}" required 
                                   placeholder="https://www.company.com">
                            <small class="form-hint">
                                <c:choose>
                                    <c:when test="${company.urlVerified}">
                                        <i class="fas fa-check-circle" style="color: #27ae60;"></i> URL verified
                                    </c:when>
                                    <c:otherwise>
                                        <i class="fas fa-clock" style="color: #f39c12;"></i> URL verification pending
                                    </c:otherwise>
                                </c:choose>
                            </small>
                        </div>

                        <div class="form-group">
                            <label for="description">
                                <i class="fas fa-info-circle"></i> Company Description
                            </label>
                            <textarea id="description" name="description" rows="4" 
                                      placeholder="Briefly describe your company, its mission, and what you do...">${company.description}</textarea>
                            <small class="form-hint">Max 1000 characters. This will be displayed on your company profile.</small>
                        </div>

                        <div class="form-group">
                            <label for="industry">
                                <i class="fas fa-industry"></i> Industry
                            </label>
                            <input type="text" id="industry" name="industry" 
                                   value="${company.industry}" 
                                   placeholder="e.g., Technology, Healthcare, Finance">
                        </div>

                        <div class="form-group">
                            <label for="location">
                                <i class="fas fa-map-marker-alt"></i> Location
                            </label>
                            <input type="text" id="location" name="location" 
                                   value="${company.location}" 
                                   placeholder="e.g., Johannesburg, South Africa">
                        </div>
                    </div>

                    <div class="form-section">
                        <h4><i class="fas fa-shield-alt"></i> Registration Status</h4>
                        <div class="status-info">
                            <div class="status-row">
                                <span class="status-label">Status:</span>
                                <span class="status-value ${company.status.toString().toLowerCase()}">
                                    ${company.status}
                                </span>
                            </div>
                            <div class="status-row">
                                <span class="status-label">Created:</span>
                                <span class="status-value">
                                    <fmt:parseDate value="${company.createdAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" />
                                    <fmt:formatDate value="${parsedDate}" pattern="MMMM d, yyyy" />
                                </span>
                            </div>
                            <div class="status-row">
                                <span class="status-label">Last Updated:</span>
                                <span class="status-value">
                                    <c:choose>
                                        <c:when test="${not empty company.updatedAt}">
                                            <fmt:parseDate value="${company.updatedAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedUpdated" />
                                            <fmt:formatDate value="${parsedUpdated}" pattern="MMMM d, yyyy 'at' h:mm a" />
                                        </c:when>
                                        <c:otherwise>
                                            Not updated yet
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                            <div class="status-row">
                                <span class="status-label">URL Verified:</span>
                                <span class="status-value">
                                    <c:choose>
                                        <c:when test="${company.urlVerified}">
                                            <i class="fas fa-check-circle" style="color: #27ae60;"></i> Yes
                                        </c:when>
                                        <c:otherwise>
                                            <i class="fas fa-times-circle" style="color: #e74c3c;"></i> No
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Update Profile
                        </button>
                        <a href="${pageContext.request.contextPath}/company/dashboard" class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Back to Dashboard
                        </a>
                    </div>
                </form>

                <hr class="form-divider">

                <h2><i class="fas fa-lock"></i> Change Password</h2>

                <form action="${pageContext.request.contextPath}/company/profile?action=changePassword" method="post" class="profile-form">
                    <div class="form-group">
                        <label for="currentPassword">Current Password</label>
                        <input type="password" id="currentPassword" name="currentPassword" required>
                    </div>

                    <div class="form-group">
                        <label for="newPassword">New Password</label>
                        <input type="password" id="newPassword" name="newPassword" required minlength="8">
                        <small>Password must be at least 8 characters</small>
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword">Confirm New Password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" required>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">Change Password</button>
                    </div>
                </form>
            </div>
        </c:if>

        <c:if test="${empty company}">
            <div class="no-data">
                <i class="fas fa-exclamation-circle"></i>
                <p>No company data available. Please complete your company registration first.</p>
                <a href="${pageContext.request.contextPath}/company-register" class="btn btn-primary">
                    <i class="fas fa-plus"></i> Register Company
                </a>
            </div>
        </c:if>
    </div>
</section>

<jsp:include page="/views/components/company-footer.jsp" />

</body>
</html>
