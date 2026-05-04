<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Company Reviews - Youth Job & Internship Finder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css?v=4">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/company-navbar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/company-reviews.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/views/components/company-navbar.jsp" />

<section class="reviews">
    <div class="reviews-container">
        <h2><i class="fas fa-star"></i> Company Reviews</h2>
        <p>View feedback and ratings from applicants who have interacted with your company.</p>

        <c:if test="${not empty company}">
            <!-- Reviews Summary -->
            <div class="reviews-summary">
                <div class="summary-card">
                    <div class="average-rating">
                        <span class="rating-number">
                            <c:choose>
                                <c:when test="${not empty reviews}">
                                    ${String.format("%.1f", reviews.stream().mapToInt(r -> r.getRating()).average().orElse(0.0))}
                                </c:when>
                                <c:otherwise>0.0</c:otherwise>
                            </c:choose>
                        </span>
                        <div class="rating-stars">
                            <c:forEach var="i" begin="1" end="5">
                                <i class="fas fa-star ${i <= (reviews.size() > 0 ? reviews.stream().mapToInt(r -> r.getRating()).average().orElse(0.0) : 0) ? 'filled' : ''}"></i>
                            </c:forEach>
                        </div>
                        <p class="total-reviews">${reviews.size()} review${reviews.size() != 1 ? 's' : ''}</p>
                    </div>
                </div>
            </div>

            <!-- Reviews List -->
            <div class="reviews-list">
                <c:choose>
                    <c:when test="${empty reviews}">
                        <div class="no-reviews">
                            <i class="fas fa-comment-slash"></i>
                            <h3>No Reviews Yet</h3>
                            <p>Your company hasn't received any reviews yet. Reviews will appear here once applicants rate their experience.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${reviews}" var="review">
                            <div class="review-card">
                                <div class="review-header">
                                    <div class="reviewer-info">
                                        <div class="reviewer-avatar">
                                            <i class="fas fa-user-circle"></i>
                                        </div>
                                        <div class="reviewer-details">
                                            <h4>${review.applicant.firstName} ${review.applicant.lastName}</h4>
                                            <span class="review-date">${review.createdAt}</span>
                                        </div>
                                    </div>
                                    <div class="review-rating">
                                        <c:forEach var="i" begin="1" end="5">
                                            <i class="fas fa-star ${i <= review.getRating() ? 'filled' : ''}"></i>
                                        </c:forEach>
                                    </div>
                                </div>
                                <div class="review-content">
                                    <p>${review.comment}</p>
                                </div>
                                <c:if test="${not empty review.job}">
                                    <div class="review-job">
                                        <i class="fas fa-briefcase"></i>
                                        <span>Review for: ${review.job.jobTitle}</span>
                                    </div>
                                </c:if>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Back Button -->
            <div class="reviews-actions">
                <a href="${pageContext.request.contextPath}/company/dashboard" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Back to Dashboard
                </a>
            </div>
        </c:if>

        <c:if test="${empty company}">
            <div class="no-data">
                <i class="fas fa-exclamation-circle"></i>
                <p>No company data available. Please complete your company profile first.</p>
                <a href="${pageContext.request.contextPath}/company/dashboard" class="btn btn-primary">
                    Go to Dashboard
                </a>
            </div>
        </c:if>
    </div>
</section>

<jsp:include page="/views/components/company-footer.jsp" />

</body>
</html>
