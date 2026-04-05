<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Youth Job & Internship Finder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<!-- Reusable Navbar -->
<jsp:include page="/views/components/default-navbar.jsp" />

<!-- Modern Hero Section -->
<section class="hero">
    <div class="hero-background">
        <div class="hero-particles"></div>
    </div>
    <div class="hero-content">
        <div class="hero-text">
            <h1 class="hero-title">
                <span class="gradient-text">Discover Your Dream Career</span>
            </h1>
            <p class="hero-subtitle">
                Connect with top companies, find internships, and kickstart your professional journey
            </p>
            <div class="hero-actions">
                <a href="${pageContext.request.contextPath}/signup-options" class="btn btn-primary">
                    <i class="fas fa-rocket"></i> Get Started
                </a>
                <a href="${pageContext.request.contextPath}/jobs" class="btn btn-secondary">
                    <i class="fas fa-search"></i> Browse Jobs
                </a>
            </div>
        </div>
    </div>
</section>

<!-- Features Section -->
<section class="features">
    <div class="container">
        <h2 class="section-title">Why Choose JobFinder?</h2>
        <div class="features-grid">
            <div class="feature-card">
                <div class="feature-icon">
                    <i class="fas fa-shield-alt"></i>
                </div>
                <h3>Verified Companies</h3>
                <p>All companies are thoroughly vetted to ensure authentic opportunities</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">
                    <i class="fas fa-user-tie"></i>
                </div>
                <h3>Career Guidance</h3>
                <p>Get personalized recommendations based on your skills and interests</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">
                    <i class="fas fa-comments"></i>
                </div>
                <h3>Company Reviews</h3>
                <p>Read authentic reviews from current and former employees</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon">
                    <i class="fas fa-mobile-alt"></i>
                </div>
                <h3>Mobile Friendly</h3>
                <p>Apply to jobs on the go with our responsive design</p>
            </div>
        </div>
    </div>
</section>

<!-- Call to Action -->
<section class="cta">
    <div class="container">
        <div class="cta-content">
            <h2>Ready to Start Your Career Journey?</h2>
            <p>Join thousands of students who found their dream jobs through JobFinder</p>
            <a href="${pageContext.request.contextPath}/signup-options" class="btn btn-primary btn-large">
                Get Started Now
            </a>
        </div>
    </div>
</section>

<!-- footer -->
<jsp:include page="/views/components/footer.jsp" />

</body>
</html>