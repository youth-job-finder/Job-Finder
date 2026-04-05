<%--
  Admin Dashboard Footer - System Administration Focus
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-footer.css">

<footer class="admin-footer">
    <div class="container">
        <div class="footer-content">
            <div class="footer-section">
                <h3><i class="fas fa-shield-alt"></i> System Admin</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/admins/${adminId}/dashboard"><i class="fas fa-tachometer-alt"></i> Admin Dashboard</a></li>
                    <li><a href="${pageContext.request.contextPath}/admins/${adminId}/users"><i class="fas fa-users-cog"></i> User Management</a></li>
                    <li><a href="${pageContext.request.contextPath}/admins/${adminId}/companies"><i class="fas fa-building"></i> Company Administration</a></li>
                    <li><a href="${pageContext.request.contextPath}/admins/${adminId}/jobs"><i class="fas fa-briefcase"></i> Job Oversight</a></li>
                    <li><a href="${pageContext.request.contextPath}/admins/${adminId}/reports"><i class="fas fa-chart-line"></i> System Reports</a></li>
                </ul>
            </div>
            
            <div class="footer-section">
                <h3><i class="fas fa-tools"></i> System Tools</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/admins/${adminId}/database"><i class="fas fa-database"></i> Database Management</a></li>
                    <li><a href="${pageContext.request.contextPath}/admins/${adminId}/logs"><i class="fas fa-file-alt"></i> System Logs</a></li>
                    <li><a href="${pageContext.request.contextPath}/admins/${adminId}/backup"><i class="fas fa-save"></i> Backup & Restore</a></li>
                    <li><a href="${pageContext.request.contextPath}/admins/${adminId}/security"><i class="fas fa-lock"></i> Security Center</a></li>
                    <li><a href="${pageContext.request.contextPath}/admins/${adminId}/performance"><i class="fas fa-tachometer-alt"></i> Performance Monitor</a></li>
                </ul>
            </div>
            
            <div class="footer-section">
                <h3><i class="fas fa-cogs"></i> System Configuration</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/admins/${adminId}/settings"><i class="fas fa-cog"></i> System Settings</a></li>
                    <li><a href="${pageContext.request.contextPath}/admins/${adminId}/email"><i class="fas fa-envelope"></i> Email Configuration</a></li>
                    <li><a href="${pageContext.request.contextPath}/admins/${adminId}/storage"><i class="fas fa-hdd"></i> Storage Management</a></li>
                    <li><a href="${pageContext.request.contextPath}/admins/${adminId}/api"><i class="fas fa-code"></i> API Management</a></li>
                    <li><a href="${pageContext.request.contextPath}/admins/${adminId}/integrations"><i class="fas fa-plug"></i> Third-party Integrations</a></li>
                </ul>
            </div>
            
            <div class="footer-section">
                <h3><i class="fas fa-info-circle"></i> System Status</h3>
                <div class="status-indicators">
                    <div class="status-item">
                        <span class="status-dot online"></span>
                        <span>System Online</span>
                    </div>
                    <div class="status-item">
                        <span class="status-dot online"></span>
                        <span>Database Connected</span>
                    </div>
                    <div class="status-item">
                        <span class="status-dot online"></span>
                        <span>Email Service Active</span>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="footer-bottom">
            <p>&copy; 2026 JobFinder Admin Portal | System Version 2.1.0 | <a href="${pageContext.request.contextPath}/admins/${adminId}/help">Admin Help</a> | <a href="${pageContext.request.contextPath}/admins/${adminId}/support">Technical Support</a></p>
        </div>
    </div>
</footer>

