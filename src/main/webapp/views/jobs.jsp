<%--
  Created by IntelliJ IDEA.
  User: aubre
  Date: 2026/03/24
  Time: 23:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Job Opportunities - JobFinder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user-dashboard.css">
</head>
<body>

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

<!-- Jobs Header -->
<section class="page-header">
    <div class="container">
        <h1 class="page-title">Job Opportunities</h1>
        <p class="page-subtitle">Discover your next career move from our curated job listings</p>
    </div>
</section>

<!-- Search and Filter Section -->
<section class="search-section">
    <div class="container">
        <div class="search-container">
            <div class="search-box">
                <i class="fas fa-search"></i>
                <input type="text" placeholder="Search jobs by title, company, or keyword..." class="search-input">
            </div>
            <div class="filter-buttons">
                <a href="${pageContext.request.contextPath}/jobs" class="filter-btn ${empty param.filter ? 'active' : ''}">
                    <i class="fas fa-briefcase"></i> All Jobs
                </a>
                <a href="${pageContext.request.contextPath}/jobs?filter=remote" class="filter-btn ${param.filter == 'remote' ? 'active' : ''}">
                    <i class="fas fa-laptop"></i> Remote
                </a>
                <c:if test="${isAuthenticated && userRole == 'APPLICANT'}">
                    <a href="${pageContext.request.contextPath}/jobs?filter=saved" class="filter-btn ${param.filter == 'saved' ? 'active' : ''}">
                        <i class="fas fa-bookmark"></i> Saved Jobs
                    </a>
                </c:if>
            </div>
        </div>
    </div>
</section>

<!-- Jobs Grid -->
<section class="jobs-section">
    <!-- Recommended Jobs -->
    <div class="dashboard-card">
        <div class="card-header">
            <h3><i class="fas fa-star"></i> Recommended Jobs</h3>
            <a href="${pageContext.request.contextPath}/jobs" class="view-all-link">View All Jobs</a>
        </div>
        <div class="job-recommendations">
            <c:choose>
                <c:when test="${empty jobs}">
                    <div class="no-jobs">
                        <i class="fas fa-search"></i>
                        <p>No job listings available at the moment. Check back soon for new opportunities!</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${jobs}" var="job">
                        <div class="job-card">
                            <div class="job-header">
                                <div class="job-info">
                                    <h4>${job.jobTitle}</h4>
                                    <p class="company-name">${job.company.name}</p>
                                </div>
                                <span class="job-type ${job.jobType != null ? job.jobType.toLowerCase() : 'full-time'}">${job.jobType != null ? job.jobType : 'Full-time'}</span>
                            </div>
                            <div class="job-details">
                                <div class="job-meta">
                                    <span class="location"><i class="fas fa-map-marker-alt"></i> ${job.physicalAddress != null ? job.physicalAddress : 'Location not specified'}</span>
                                    <span class="salary"><i class="fas fa-money-bill"></i> ${job.salaryRange != null ? job.salaryRange : 'Competitive'}</span>
                                </div>
                                <p class="job-description">${job.jobDescription != null ? job.jobDescription : ''}</p>
                            </div>
                            <div class="job-actions">
                                <a href="${pageContext.request.contextPath}/applicant/apply?jobId=${job.id}" class="btn btn-sm btn-primary">Apply Now</a>
                                <a href="${pageContext.request.contextPath}/job/${job.id}" class="btn btn-sm btn-outline">View Details</a>
                                <c:if test="${isAuthenticated && userRole == 'APPLICANT'}">
                                    <c:choose>
                                        <c:when test="${savedJobIds != null && savedJobIds.contains(job.id)}">
                                            <button onclick="unsaveJob('${job.id}')" class="btn btn-sm btn-warning unsave-job-btn" data-job-id="${job.id}">
                                                <i class="fas fa-bookmark"></i> Saved
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button onclick="saveJob('${job.id}')" class="btn btn-sm btn-outline save-job-btn" data-job-id="${job.id}">
                                                <i class="far fa-bookmark"></i> Save
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</section>

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
            <c:otherwise>
                <jsp:include page="/views/components/applicant-footer.jsp" />
            </c:otherwise>
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
            const btn = document.querySelector('.save-job-btn[data-job-id="' + jobId + '"], .unsave-job-btn[data-job-id="' + jobId + '"]');
            if (btn) {
                btn.innerHTML = '<i class="fas fa-bookmark"></i> Saved';
                btn.classList.remove('btn-outline', 'save-job-btn');
                btn.classList.add('btn-warning', 'unsave-job-btn');
                btn.onclick = function() { unsaveJob(jobId); };
            }
            alert('Job saved successfully!');
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
            const btn = document.querySelector('.save-job-btn[data-job-id="' + jobId + '"], .unsave-job-btn[data-job-id="' + jobId + '"]');
            if (btn) {
                btn.innerHTML = '<i class="far fa-bookmark"></i> Save';
                btn.classList.remove('btn-warning', 'unsave-job-btn');
                btn.classList.add('btn-outline', 'save-job-btn');
                btn.onclick = function() { saveJob(jobId); };
            }
            alert('Job removed from saved');
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

</body>
</html>
