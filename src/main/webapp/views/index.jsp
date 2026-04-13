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

<c:set var="getStartedHref" value="${pageContext.request.contextPath}/signup-options" />
<c:if test="${isAuthenticated}">
    <c:choose>
        <c:when test="${userRole == 'APPLICANT'}">
            <c:set var="getStartedHref" value="${pageContext.request.contextPath}/applicant/dashboard" />
        </c:when>
        <c:when test="${userRole == 'COMPANY_ADMIN'}">
            <c:set var="getStartedHref" value="${pageContext.request.contextPath}/company/dashboard" />
        </c:when>
        <c:when test="${userRole == 'SYSTEM_ADMIN'}">
            <c:set var="getStartedHref" value="${pageContext.request.contextPath}/admin/dashboard" />
        </c:when>
    </c:choose>
</c:if>

<c:choose>
    <c:when test="${isAuthenticated}">
        <c:choose>
            <c:when test="${userRole == 'APPLICANT'}"><jsp:include page="/views/components/user-navbar.jsp" /></c:when>
            <c:when test="${userRole == 'COMPANY_ADMIN'}"><jsp:include page="/views/components/company-navbar.jsp" /></c:when>
            <c:when test="${userRole == 'SYSTEM_ADMIN'}"><jsp:include page="/views/components/system-navbar.jsp" /></c:when>
            <c:otherwise><jsp:include page="/views/components/default-navbar.jsp" /></c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise><jsp:include page="/views/components/default-navbar.jsp" /></c:otherwise>
</c:choose>

<!-- Modern Hero Section -->
<section class="hero">
    <div class="hero-content">
        <div class="hero-text">
            <h1 class="hero-title">
                <span class="gradient-text">Discover Your Dream Career</span>
            </h1>
            <p class="hero-subtitle">
                Connect with top companies, find internships, and kickstart your professional journey
            </p>
            <div class="hero-actions">
                <button type="button" id="hero-get-started" class="btn btn-primary" onclick="window.location.href='${getStartedHref}'">
                    <i class="fas fa-rocket"></i> Get Started
                </button>
                <button type="button" id="hero-browse-jobs" class="btn btn-secondary" onclick="window.location.href='${pageContext.request.contextPath}/jobs'">
                    <i class="fas fa-search"></i> Browse Jobs
                </button>
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
            <button type="button" class="btn btn-primary btn-large" onclick="window.location.href='${getStartedHref}'">
                Get Started Now
            </button>
        </div>
    </div>
</section>

<c:choose>
    <c:when test="${isAuthenticated}">
        <c:choose>
            <c:when test="${userRole == 'APPLICANT'}"><jsp:include page="/views/components/applicant-footer.jsp" /></c:when>
            <c:when test="${userRole == 'COMPANY_ADMIN'}"><jsp:include page="/views/components/company-footer.jsp" /></c:when>
            <c:when test="${userRole == 'SYSTEM_ADMIN'}"><jsp:include page="/views/components/admin-footer.jsp" /></c:when>
            <c:otherwise><jsp:include page="/views/components/footer.jsp" /></c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise><jsp:include page="/views/components/footer.jsp" /></c:otherwise>
</c:choose>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const heroButtons = [
        { element: document.getElementById('hero-get-started'), href: '${getStartedHref}' },
        { element: document.getElementById('hero-browse-jobs'), href: '${pageContext.request.contextPath}/jobs' }
    ].filter(item => item.element);

    function isInsideRect(rect, x, y) {
        return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom;
    }

    function syncManualHover(event) {
        let hoveringHeroButton = false;

        heroButtons.forEach(item => {
            const rect = item.element.getBoundingClientRect();
            const isHovering = isInsideRect(rect, event.clientX, event.clientY);
            item.element.classList.toggle('manual-hover', isHovering);
            if (isHovering) {
                hoveringHeroButton = true;
            }
        });

        document.body.classList.toggle('hero-button-hovering', hoveringHeroButton);
    }

    document.addEventListener('mousemove', syncManualHover, true);
    document.addEventListener('pointermove', syncManualHover, true);

    document.addEventListener('mouseleave', function() {
        heroButtons.forEach(item => item.element.classList.remove('manual-hover'));
        document.body.classList.remove('hero-button-hovering');
    }, true);

    document.addEventListener('click', function(event) {
        const x = event.clientX;
        const y = event.clientY;

        const targetButton = heroButtons.find(item => {
            const rect = item.element.getBoundingClientRect();
            return isInsideRect(rect, x, y);
        });

        if (targetButton) {
            window.location.href = targetButton.href;
        }
    }, true);
});
</script>

</body>
</html>
