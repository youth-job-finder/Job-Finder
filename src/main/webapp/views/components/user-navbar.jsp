<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="currentUri" value="${pageContext.request.requestURI}" />

<nav class="navbar navbar-with-actions">
    <div class="navbar-container">
        <!-- Logo -->
        <a href="${pageContext.request.contextPath}/applicant/dashboard" class="logo-link" aria-label="JobFinder dashboard">
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
            <!-- Home / Dashboard -->
            <li>
                <a href="${pageContext.request.contextPath}/applicant/dashboard"
                   class="${fn:contains(currentUri, '/applicant/dashboard') ? 'active' : ''}">
                    Home
                </a>
            </li>

            <!-- Upload / Update CV -->
            <li>
                <a href="${pageContext.request.contextPath}/applicant/cv"
                   class="${fn:contains(currentUri, '/applicant/cv') ? 'active' : ''}">
                    CV
                </a>
            </li>

            <!-- Search & Filter Jobs -->
            <li>
                <a href="${pageContext.request.contextPath}/jobs"
                   class="${fn:contains(currentUri, '/jobs') ? 'active' : ''}">
                    Jobs
                </a>
            </li>

            <!-- Applications -->
            <li>
                <a href="${pageContext.request.contextPath}/applicant/applications"
                   class="${fn:contains(currentUri, '/applicant/applications') ? 'active' : ''}">
                    Applications
                </a>
            </li>

            <!-- Profile -->
            <li>
                <a href="${pageContext.request.contextPath}/applicant/profile"
                   class="${fn:contains(currentUri, '/applicant/profile') ? 'active' : ''}">
                    Profile
                </a>
            </li>
        </ul>

        <!-- Actions (right side) -->
        <div class="nav-actions">
            <form action="${pageContext.request.contextPath}/logout" method="post" style="display: inline;">
                <button type="submit" class="btn btn-primary">Logout</button>
            </form>
        </div>
    </div>
</nav>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const hamburger = document.getElementById('hamburger-menu');
    const navMenu = document.getElementById('nav-menu');
    const navActions = document.querySelector('.nav-actions');
    
    hamburger.addEventListener('click', function() {
        hamburger.classList.toggle('active');
        navMenu.classList.toggle('active');
        if (navActions) navActions.classList.toggle('active');
    });
    
    // Close menu when clicking on a link
    document.querySelectorAll('.nav-links a').forEach(link => {
        link.addEventListener('click', () => {
            hamburger.classList.remove('active');
            navMenu.classList.remove('active');
            if (navActions) navActions.classList.remove('active');
        });
    });
    
    // Close menu when clicking on logout button in nav-links or nav-actions
    document.querySelectorAll('.nav-links button, .nav-actions button').forEach(button => {
        button.addEventListener('click', () => {
            hamburger.classList.remove('active');
            navMenu.classList.remove('active');
            if (navActions) navActions.classList.remove('active');
        });
    });
    
    // Close menu when clicking outside
    document.addEventListener('click', function(event) {
        if (!hamburger.contains(event.target) && !navMenu.contains(event.target) && 
            !(navActions && navActions.contains(event.target))) {
            hamburger.classList.remove('active');
            navMenu.classList.remove('active');
            if (navActions) navActions.classList.remove('active');
        }
    });
});
</script>
