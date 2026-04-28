<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="currentUri" value="${pageContext.request.requestURI}" />

<nav class="navbar">
    <div class="navbar-container">
        <!-- Logo -->
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="logo-link admin-logo-link" aria-label="JobFinder dashboard">
            <span class="logo-icon"><i class="fas fa-search"></i></span>
            <span class="logo">JobFinder</span>
        </a>
        
        <!-- Hamburger menu for mobile -->
        <button class="hamburger" id="hamburger-menu" aria-label="Toggle menu">
            <span></span>
            <span></span>
            <span></span>
        </button>
        
        <!-- Navigation links -->
        <ul class="nav-links" id="nav-menu">
            <!-- Dashboard -->
            <li>
                <a href="${pageContext.request.contextPath}/admin/dashboard"
                   class="${fn:contains(currentUri, '/admin/dashboard') ? 'active' : ''}">
                    Dashboard
                </a>
            </li>

            <!-- Users Management -->
            <li>
                <a href="${pageContext.request.contextPath}/admin/users"
                   class="${fn:contains(currentUri, '/admin/users') ? 'active' : ''}">
                    Users
                </a>
            </li>

            <!-- Companies Management -->
            <li>
                <a href="${pageContext.request.contextPath}/admin/companies"
                   class="${fn:contains(currentUri, '/admin/companies') ? 'active' : ''}">
                    Companies
                </a>
            </li>

            <!-- Jobs Management -->
            <li>
                <a href="${pageContext.request.contextPath}/admin/jobs"
                   class="${fn:contains(currentUri, '/admin/jobs') ? 'active' : ''}">
                    Jobs
                </a>
            </li>

            <!-- Reports -->
            <li>
                <a href="${pageContext.request.contextPath}/admin/reports"
                   class="${fn:contains(currentUri, '/admin/reports') ? 'active' : ''}">
                    Reports
                </a>
            </li>

            <!-- Settings -->
            <li>
                <a href="${pageContext.request.contextPath}/admin/settings"
                   class="${fn:contains(currentUri, '/admin/settings') ? 'active' : ''}">
                    Settings
                </a>
            </li>

            <!-- Sign Out -->
            <li>
                <form action="${pageContext.request.contextPath}/logout" method="post" style="display: inline;">
                    <button type="submit" class="btn btn-primary">
                        Logout
                    </button>
                </form>
            </li>
        </ul>
    </div>
</nav>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const hamburger = document.getElementById('hamburger-menu');
    const navMenu = document.getElementById('nav-menu');
    
    hamburger.addEventListener('click', function() {
        hamburger.classList.toggle('active');
        navMenu.classList.toggle('active');
    });
    
    // Close menu when clicking on a link
    document.querySelectorAll('.nav-links a').forEach(link => {
        link.addEventListener('click', () => {
            hamburger.classList.remove('active');
            navMenu.classList.remove('active');
        });
    });
    
    // Close menu when clicking on logout button
    document.querySelectorAll('.nav-links button').forEach(button => {
        button.addEventListener('click', () => {
            hamburger.classList.remove('active');
            navMenu.classList.remove('active');
        });
    });
    
    // Close menu when clicking outside
    document.addEventListener('click', function(event) {
        if (!hamburger.contains(event.target) && !navMenu.contains(event.target)) {
            hamburger.classList.remove('active');
            navMenu.classList.remove('active');
        }
    });
});
</script>
