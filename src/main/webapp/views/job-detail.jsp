<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${job.jobTitle} - ${job.company.name} | JobFinder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/job-detail.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<c:set var="backToJobsHref" value="${pageContext.request.contextPath}/jobs" />
<c:if test="${isAuthenticated && userRole == 'COMPANY_ADMIN'}">
    <c:set var="backToJobsHref" value="${pageContext.request.contextPath}/company/listings" />
</c:if>

<!-- Dynamic Navbar based on auth status -->
<c:choose>
    <c:when test="${isAuthenticated}">
        <c:choose>
            <c:when test="${userRole == 'APPLICANT'}">
                <jsp:include page="/views/components/user-navbar.jsp" />
            </c:when>
            <c:when test="${userRole == 'COMPANY_ADMIN'}">
                <jsp:include page="/views/components/company-navbar.jsp" />
            </c:when>
            <c:when test="${userRole == 'SYSTEM_ADMIN'}">
                <jsp:include page="/views/components/system-navbar.jsp" />
            </c:when>
            <c:otherwise>
                <jsp:include page="/views/components/user-navbar.jsp" />
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <jsp:include page="/views/components/default-navbar.jsp" />
    </c:otherwise>
</c:choose>

<!-- Job Detail Header -->
<section class="job-detail-header">
    <div class="container">
        <a href="${backToJobsHref}" class="back-link">
            <i class="fas fa-arrow-left"></i> Back to Jobs
        </a>
        <h1>${job.jobTitle}</h1>
        <div class="company-info">
            <i class="fas fa-building"></i>
            <span>${job.company.name}</span>
            <span class="divider">|</span>
            <span class="industry">${job.company.industry != null ? job.company.industry : 'Technology'}</span>
        </div>
    </div>
</section>

<!-- Job Details Content -->
<div class="container" style="max-width: 1200px; margin: 0 auto; padding: 0 2rem 4rem;">
    
    <!-- Job Meta Information -->
    <div class="job-meta-grid">
        <div class="job-meta-item">
            <i class="fas fa-map-marker-alt"></i>
            <div>
                <strong>Location</strong>
                <p>${job.physicalAddress != null ? job.physicalAddress : 'Remote / Not specified'}</p>
            </div>
        </div>
        <div class="job-meta-item">
            <i class="fas fa-money-bill-wave"></i>
            <div>
                <strong>Salary</strong>
                <p>${job.salaryRange != null ? job.salaryRange : 'Competitive'}</p>
            </div>
        </div>
        <div class="job-meta-item">
            <i class="fas fa-briefcase"></i>
            <div>
                <strong>Job Type</strong>
                <p>${job.jobType != null ? job.jobType : 'Full-time'}</p>
            </div>
        </div>
        <div class="job-meta-item">
            <i class="fas fa-calendar-alt"></i>
            <div>
                <strong>Posted</strong>
                <p>${job.created_at != null ? job.created_at : 'Recently'}</p>
            </div>
        </div>
    </div>

    <!-- Job Description -->
    <div class="job-section">
        <h3><i class="fas fa-info-circle"></i> Job Description</h3>
        <p class="job-copy">
            ${job.jobDescription != null ? job.jobDescription : 'No description available.'}
        </p>
    </div>

    <!-- Requirements -->
    <c:if test="${not empty job.jobRequirements}">
        <div class="job-section">
            <h3><i class="fas fa-check-circle"></i> Requirements</h3>
            <p class="job-copy">
                ${job.jobRequirements}
            </p>
        </div>
    </c:if>

    <!-- Company Information -->
    <div class="job-section">
        <h3><i class="fas fa-building"></i> About ${job.company.name}</h3>
        <p class="job-copy">
            ${job.company.description != null ? job.company.description : 'No company description available.'}
        </p>
        <c:if test="${not empty job.company.url}">
            <p class="job-link-row">
                <a href="${job.company.url}" target="_blank" rel="noopener noreferrer" class="btn btn-outline btn-sm">
                    <i class="fas fa-external-link-alt"></i> Visit Company Website
                </a>
            </p>
        </c:if>
    </div>

    <!-- Action Buttons for Applicants -->
    <c:if test="${isAuthenticated && userRole == 'APPLICANT'}">
        <div class="job-section" style="text-align: center;">
            <c:choose>
                <c:when test="${hasApplied}">
                    <div class="alert alert-success" style="display: inline-block;">
                        <i class="fas fa-check-circle"></i> You have already applied for this job
                    </div>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/applicant/apply?jobId=${job.id}" class="btn btn-primary btn-lg" style="margin-right: 1rem;">
                        <i class="fas fa-paper-plane"></i> Apply Now
                    </a>
                </c:otherwise>
            </c:choose>
            
            <c:choose>
                <c:when test="${isSaved}">
                    <button onclick="unsaveJob('${job.id}')" class="btn btn-warning btn-lg">
                        <i class="fas fa-bookmark"></i> Saved
                    </button>
                </c:when>
                <c:otherwise>
                    <button onclick="saveJob('${job.id}')" class="btn btn-outline btn-lg">
                        <i class="far fa-bookmark"></i> Save Job
                    </button>
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>

    <!-- Message for non-logged in users -->
    <c:if test="${!isAuthenticated}">
        <div class="job-section guest-apply-panel">
            <p class="guest-apply-copy">
                <i class="fas fa-info-circle"></i> 
                Please <a href="${pageContext.request.contextPath}/login">log in</a> or 
                <a href="${pageContext.request.contextPath}/signup-options">create an account</a> to apply for this job.
            </p>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-lg">
                <i class="fas fa-sign-in-alt"></i> Log In to Apply
            </a>
        </div>
    </c:if>

    <!-- Company Admin View -->
    <c:if test="${isAuthenticated && userRole == 'COMPANY_ADMIN'}">
        <div class="job-section company-admin-panel">
            <h3><i class="fas fa-info-circle"></i> Company Admin View</h3>
            <p>This is your company's job posting. You can manage it from your <a href="${pageContext.request.contextPath}/company/listings">listings page</a>.</p>
        </div>
    </c:if>

</div>

<!-- Dynamic Footer based on auth status -->
<c:choose>
    <c:when test="${isAuthenticated}">
        <c:choose>
            <c:when test="${userRole == 'APPLICANT'}">
                <jsp:include page="/views/components/applicant-footer.jsp" />
            </c:when>
            <c:when test="${userRole == 'COMPANY_ADMIN'}">
                <jsp:include page="/views/components/company-footer.jsp" />
            </c:when>
            <c:when test="${userRole == 'SYSTEM_ADMIN'}">
                <jsp:include page="/views/components/admin-footer.jsp" />
            </c:when>
        </c:choose>
    </c:when>
    <c:otherwise>
        <jsp:include page="/views/components/footer.jsp" />
    </c:otherwise>
</c:choose>

<c:if test="${isAuthenticated && userRole == 'APPLICANT'}">
<script>
function saveJob(jobId) {
    if (!jobId || jobId === 'null' || jobId === '') {
        alert('Error: Cannot save job - invalid job ID');
        return;
    }
    fetch('${pageContext.request.contextPath}/applicant/save-job', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'jobId=' + encodeURIComponent(jobId)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('Job saved successfully!');
            location.reload();
        } else {
            alert(data.message || 'Failed to save job');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('An error occurred while saving the job');
    });
}

function unsaveJob(jobId) {
    if (!jobId || jobId === 'null' || jobId === '') {
        alert('Error: Cannot unsave job - invalid job ID');
        return;
    }
    fetch('${pageContext.request.contextPath}/applicant/unsave-job', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'jobId=' + encodeURIComponent(jobId)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('Job removed from saved');
            location.reload();
        } else {
            alert(data.message || 'Failed to remove job');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('An error occurred while removing the job');
    });
}
</script>
</c:if>

<script>
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('[data-nav-target]').forEach(link => {
        link.addEventListener('click', function() {
            window.location.href = this.dataset.navTarget;
        });
    });
});
</script>

</body>
</html>
