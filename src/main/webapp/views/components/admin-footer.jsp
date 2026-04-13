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
                    <li><a href="${pageContext.request.contextPath}/admin/dashboard"><i class="fas fa-tachometer-alt"></i> Admin Dashboard</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/users"><i class="fas fa-users-cog"></i> User Management</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/companies"><i class="fas fa-building"></i> Company Administration</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/jobs"><i class="fas fa-briefcase"></i> Job Oversight</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/reports"><i class="fas fa-chart-line"></i> System Reports</a></li>
                </ul>
            </div>
            
            <div class="footer-section">
                <h3><i class="fas fa-tools"></i> System Tools</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/admin/settings"><i class="fas fa-cog"></i> System Settings</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/reports"><i class="fas fa-file-alt"></i> System Reports</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/jobs"><i class="fas fa-briefcase"></i> Job Oversight</a></li>
                    <li><a href="${pageContext.request.contextPath}/contact"><i class="fas fa-headset"></i> Technical Support</a></li>
                    <li><a href="${pageContext.request.contextPath}/terms"><i class="fas fa-scale-balanced"></i> Platform Terms</a></li>
                </ul>
            </div>
            
            <div class="footer-section">
                <h3><i class="fas fa-cogs"></i> System Configuration</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/admin/settings"><i class="fas fa-cog"></i> System Settings</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/users"><i class="fas fa-users"></i> Access Control</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/companies"><i class="fas fa-building"></i> Company Approval</a></li>
                    <li><a href="${pageContext.request.contextPath}/home"><i class="fas fa-house"></i> Public Home</a></li>
                    <li><a href="${pageContext.request.contextPath}/privacy"><i class="fas fa-user-shield"></i> Privacy Policy</a></li>
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
            <p>&copy; 2026 JobFinder Admin Portal | System Version 2.1.0 | <a href="${pageContext.request.contextPath}/admin/dashboard">Admin Home</a> | <a href="${pageContext.request.contextPath}/contact">Technical Support</a></p>
        </div>
    </div>
</footer>

