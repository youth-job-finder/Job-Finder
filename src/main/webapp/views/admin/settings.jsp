<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>System Settings - JobFinder Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-settings.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/views/components/system-navbar.jsp" />

<section class="dashboard">
    <div class="dashboard-container">
        <div class="settings-header">
            <h2><i class="fas fa-cog"></i> System Settings</h2>
        </div>

        <div class="info-box">
            <i class="fas fa-info-circle"></i>
            <strong>Note:</strong> These settings are stored in memory and will reset on server restart. 
            In production, these should be persisted to the database.
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

        <form method="post" action="${currentUri}" class="settings-form">
            <input type="hidden" name="action" value="update">

            <div class="settings-section">
                <h3><i class="fas fa-globe"></i> General Settings</h3>
                
                <div class="form-group">
                    <label for="siteName">Site Name</label>
                    <input type="text" id="siteName" name="siteName" value="${settings.siteName}" required>
                </div>

                <div class="form-group">
                    <label class="checkbox-label">
                        <input type="checkbox" name="maintenanceMode" ${settings.maintenanceMode == 'true' ? 'checked' : ''}>
                        <span>Maintenance Mode (disable public access)</span>
                    </label>
                </div>
            </div>

            <div class="settings-section">
                <h3><i class="fas fa-shield-alt"></i> Security Settings</h3>
                
                <div class="form-group">
                    <label class="checkbox-label">
                        <input type="checkbox" name="enableEmailVerification" ${settings.enableEmailVerification == 'true' ? 'checked' : ''}>
                        <span>Enable Email Verification for new accounts</span>
                    </label>
                </div>

                <div class="form-group">
                    <label class="checkbox-label">
                        <input type="checkbox" name="enableUrlVerification" ${settings.enableUrlVerification == 'true' ? 'checked' : ''}>
                        <span>Enable Automatic URL Verification for companies</span>
                    </label>
                </div>
            </div>

            <div class="settings-section">
                <h3><i class="fas fa-upload"></i> Upload Settings</h3>
                
                <div class="form-group">
                    <label for="maxUploadSize">Max Upload Size (MB)</label>
                    <input type="number" id="maxUploadSize" name="maxUploadSize" value="${settings.maxUploadSize}" min="1" max="100" required>
                </div>
            </div>

            <div class="settings-section">
                <h3><i class="fas fa-briefcase"></i> Job Settings</h3>
                
                <div class="form-group">
                    <label for="defaultJobExpiryDays">Default Job Expiry (Days)</label>
                    <input type="number" id="defaultJobExpiryDays" name="defaultJobExpiryDays" value="${settings.defaultJobExpiryDays}" min="1" max="365" required>
                </div>
            </div>

            <div class="dashboard-actions">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> Save Settings
                </button>
                <button type="reset" class="btn btn-secondary">
                    <i class="fas fa-undo"></i> Reset
                </button>
            </div>
        </form>
    </div>
</section>

<jsp:include page="/views/components/admin-footer.jsp" />

</body>
</html>
