<%--
  Created by IntelliJ IDEA.
  User: aubre
  Date: 2026/03/24
  Time: 22:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="currentUri" value="${pageContext.request.requestURI}" />

<nav class="navbar">
    <div class="navbar-container">
        <!-- Logo centered -->
        <div class="logo">JobFinder</div>
        
        <!-- Hamburger menu for mobile -->
        <button class="hamburger" id="hamburger-menu" aria-label="Toggle menu">
            <span></span>
            <span></span>
            <span></span>
        </button>
        
        <!-- Navigation links -->
        <ul class="nav-links" id="nav-menu">
            <li><a href="${pageContext.request.contextPath}/home"
                   class="${fn:contains(currentUri, '/home') ? 'active' : ''}">Home</a></li>
            <li><a href="${pageContext.request.contextPath}/jobs"
                   class="${fn:contains(currentUri, '/jobs') ? 'active' : ''}">Jobs</a></li>
            <li><a href="${pageContext.request.contextPath}/internships"
                   class="${fn:contains(currentUri, '/internships') ? 'active' : ''}">Internships</a></li>
            <li><a href="${pageContext.request.contextPath}/companies"
                   class="${fn:contains(currentUri, '/companies') ? 'active' : ''}">Companies</a></li>
            <li><a href="${pageContext.request.contextPath}/reviews"
                   class="${fn:contains(currentUri, '/reviews') ? 'active' : ''}">Reviews</a></li>

            <!-- Sign In as button -->
            <li><a href="${pageContext.request.contextPath}/login"
                   class="btn btn-primary ${fn:contains(currentUri, '/login') ? 'active' : ''}">Sign In</a></li>

            <!-- Sign Up as button -->
            <li><a href="${pageContext.request.contextPath}/signup-options"
                   class="btn btn-primary ${fn:contains(currentUri, '/signup-options') ? 'active' : ''}">Sign Up</a></li>
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
    
    // Close menu when clicking outside
    document.addEventListener('click', function(event) {
        if (!hamburger.contains(event.target) && !navMenu.contains(event.target)) {
            hamburger.classList.remove('active');
            navMenu.classList.remove('active');
        }
    });
});
</script>
