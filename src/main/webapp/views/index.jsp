<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Youth Job & Internship Finder</title>
    <link rel="stylesheet" href="<c:url value='/css/index.css'/>">
</head>
<body>

<!-- Navigation Bar -->
<nav class="navbar">
    <div class="logo">JobFinder</div>
    <ul class="nav-links">
        <li><a href="#">Home</a></li>
        <li><a href="#">Jobs</a></li>
        <li><a href="#">Internships</a></li>
        <li><a href="#">Companies</a></li>
        <li><a href="#">Reviews</a></li>
        <li><a href="#">Login</a></li>
        <li><a href="#" class="signup-link">Sign Up</a></li>
    </ul>
</nav>

<!-- Hero Section -->
<section class="hero">
    <div class="hero-content">
        <h1>Connecting Students with Opportunities</h1>
        <p>Discover trusted jobs and internships from vetted companies.
            Empower your career journey with transparency and support.</p>
        <a href="#" class="cta-btn">Get Started</a>

        <!-- Hero Image -->
        <div class="hero-image">
            <img src="<c:url value='/images/hero-bg.jpg'/>" alt="Students and Companies">
        </div>
    </div>
</section>

<!-- Footer -->
<footer class="footer">
    <p>&copy; 2026 Youth Job & Internship Finder. All rights reserved.</p>
    <p>Built with trust, transparency, and accessibility.</p>
</footer>

</body>
</html>