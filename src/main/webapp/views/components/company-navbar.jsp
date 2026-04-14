<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="currentUri" value="${pageContext.request.requestURI}" />

<nav class="navbar company-navbar">
    <div class="navbar-container">
        <!-- Logo -->
        <a href="${pageContext.request.contextPath}/company/dashboard" class="logo-link company-logo-link" aria-label="JobFinder dashboard">
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
                <a href="${pageContext.request.contextPath}/company/dashboard"
                   class="${fn:contains(currentUri, '/company/dashboard') ? 'active' : ''}">
                    Dashboard
                </a>
            </li>

            <!-- Listings -->
            <li>
                <a href="${pageContext.request.contextPath}/company/listings"
                   class="${fn:contains(currentUri, '/company/listings') ? 'active' : ''}">
                    Listings
                </a>
            </li>

            <!-- Profile -->
            <li>
                <a href="${pageContext.request.contextPath}/company/profile"
                   class="${fn:contains(currentUri, '/company/profile') ? 'active' : ''}">
                    Profile
                </a>
            </li>

            <!-- List Job -->
            <li>
                <a href="${pageContext.request.contextPath}/company/list-job"
                   class="${fn:contains(currentUri, '/company/list-job') ? 'active' : ''}">
                    List Job
                </a>
            </li>

            <!-- Applicants -->
            <li>
                <a href="${pageContext.request.contextPath}/company/applicants"
                   class="${fn:contains(currentUri, '/company/applicants') ? 'active' : ''}">
                    Applications
                </a>
            </li>

            <!-- Reviews -->
            <li>
                <a href="${pageContext.request.contextPath}/company/reviews"
                   class="${fn:contains(currentUri, '/company/reviews') ? 'active' : ''}">
                    Reviews
                </a>
            </li>

            <!-- Analytics -->
            <li>
                <a href="${pageContext.request.contextPath}/company/analytics"
                   class="${fn:contains(currentUri, '/company/analytics') ? 'active' : ''}">
                    Analytics
                </a>
            </li>
        </ul>

        <!-- Actions (right side) -->
        <div class="nav-actions">
            <form action="${pageContext.request.contextPath}/logout" method="post" style="display: inline;">
                <button type="submit" class="btn btn-primary">
                    Logout
                </button>
            </form>
        </div>
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
