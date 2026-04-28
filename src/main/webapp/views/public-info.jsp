<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} - JobFinder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        :root {
            --info-bg: radial-gradient(circle at top, rgba(212, 175, 55, 0.14), transparent 30%), linear-gradient(180deg, #10192f 0%, #0b1120 100%);
            --panel-bg: rgba(255, 255, 255, 0.05);
            --panel-border: rgba(212, 175, 55, 0.18);
            --muted: #b6c2d1;
            --bright: #f8fbff;
        }

        .info-page {
            background: var(--info-bg);
            min-height: 70vh;
            padding: 5rem 0 6rem;
            position: relative;
            overflow: hidden;
        }

        .info-shell {
            max-width: 1040px;
            margin: 0 auto;
            padding: 0 1.5rem;
            position: relative;
            z-index: 1;
        }

        .info-intro {
            text-align: center;
            margin-bottom: 3rem;
        }

        .info-badge {
            display: inline-flex;
            align-items: center;
            gap: 0.55rem;
            padding: 0.55rem 1rem;
            border-radius: 999px;
            background: rgba(212, 175, 55, 0.1);
            color: var(--gold-accent);
            border: 1px solid rgba(212, 175, 55, 0.2);
            font-size: 0.9rem;
            font-weight: 700;
            letter-spacing: 0.02em;
            margin-bottom: 1.25rem;
        }

        .info-badge i {
            font-size: 0.95rem;
        }

        .info-hero {
            background: linear-gradient(135deg, rgba(255, 255, 255, 0.05), rgba(212, 175, 55, 0.05));
            border: 1px solid var(--panel-border);
            border-radius: 28px;
            padding: 2.5rem;
            box-shadow: 0 24px 60px rgba(0, 0, 0, 0.24);
            backdrop-filter: blur(10px);
            margin-bottom: 2rem;
        }

        .info-hero h2 {
            font-size: clamp(2rem, 5vw, 3.5rem);
            color: var(--bright);
            line-height: 1.05;
            margin-bottom: 1rem;
        }

        .info-hero p {
            color: var(--muted);
            max-width: 700px;
            margin: 0 auto 1.75rem;
            font-size: 1.05rem;
        }

        .info-actions {
            display: flex;
            justify-content: center;
            gap: 1rem;
            flex-wrap: wrap;
        }

        .info-actions .btn-secondary {
            border: 1px solid rgba(212, 175, 55, 0.28);
            background: rgba(255, 255, 255, 0.03);
            color: var(--bright);
        }

        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
            gap: 1.25rem;
        }

        .info-card {
            background: var(--panel-bg);
            border: 1px solid rgba(255, 255, 255, 0.08);
            border-radius: 22px;
            padding: 1.6rem;
            box-shadow: 0 18px 40px rgba(0, 0, 0, 0.18);
            min-height: 220px;
            transition: transform 0.25s ease, border-color 0.25s ease, box-shadow 0.25s ease;
        }

        .info-card:hover {
            transform: translateY(-4px);
            border-color: var(--panel-border);
            box-shadow: 0 24px 48px rgba(0, 0, 0, 0.24);
        }

        .info-card-icon {
            width: 52px;
            height: 52px;
            border-radius: 16px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            background: rgba(212, 175, 55, 0.12);
            color: var(--gold-accent);
            font-size: 1.15rem;
            margin-bottom: 1rem;
        }

        .info-card-icon i {
            transform: translateY(1px);
        }

        .info-card h3 {
            color: var(--gold-accent);
            margin-bottom: 0.75rem;
            font-size: 1.2rem;
        }

        .info-card p {
            color: var(--muted);
            margin: 0;
            line-height: 1.7;
        }

        .info-page::before,
        .info-page::after {
            content: "";
            position: absolute;
            border-radius: 999px;
            filter: blur(10px);
            opacity: 0.35;
        }

        .info-page::before {
            width: 220px;
            height: 220px;
            background: rgba(212, 175, 55, 0.1);
            top: 4rem;
            right: -4rem;
        }

        .info-page::after {
            width: 180px;
            height: 180px;
            background: rgba(255, 255, 255, 0.04);
            bottom: 3rem;
            left: -3rem;
        }

        @media (max-width: 640px) {
            .info-page {
                padding: 3.5rem 0 4.5rem;
            }

            .info-hero {
                padding: 1.6rem;
                border-radius: 22px;
            }

            .info-card {
                min-height: unset;
            }
        }
    </style>
</head>
<body>

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

<section class="page-header">
    <div class="container">
        <h1 class="page-title">${pageTitle}</h1>
        <p class="page-subtitle">${pageSubtitle}</p>
    </div>
</section>

<section class="info-page">
    <div class="info-shell">
        <div class="info-intro">
            <div class="info-hero">
                <div class="info-badge">
                    <i class="fas ${pageIcon}"></i>
                    ${pageLabel}
                </div>
                <h2>${pageTitle}</h2>
                <p>${pageSubtitle}</p>
                <div class="info-actions">
                    <c:choose>
                        <c:when test="${isAuthenticated && userRole == 'SYSTEM_ADMIN'}">
                            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary">
                                <i class="fas fa-arrow-left"></i> Back Home
                            </a>
                        </c:when>
                        <c:when test="${isAuthenticated && userRole == 'COMPANY_ADMIN'}">
                            <a href="${pageContext.request.contextPath}/company/dashboard" class="btn btn-secondary">
                                <i class="fas fa-arrow-left"></i> Back Home
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary">
                                <i class="fas fa-arrow-left"></i> Back Home
                            </a>
                            <a href="${pageContext.request.contextPath}/jobs" class="btn btn-primary">
                                <i class="fas fa-search"></i> Explore Jobs
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <div class="info-grid">
            <c:forEach items="${sections}" var="section" varStatus="status">
                <article class="info-card">
                    <div class="info-card-icon">
                        <i class="fas ${section.icon}"></i>
                    </div>
                    <h3>${section.heading}</h3>
                    <p>${section.body}</p>
                </article>
            </c:forEach>
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

</body>
</html>
