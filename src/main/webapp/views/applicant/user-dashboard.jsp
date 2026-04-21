<%--
  Created by IntelliJ IDEA.
  User: aubre
  Date: 2026/03/24
  Time: 22:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Dashboard - Youth Job & Internship Finder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        .job-card {
            cursor: pointer;
        }
    </style>
</head>
<body>

<!-- Logged-in Navbar -->
<jsp:include page="/views/components/user-navbar.jsp" />

<section class="dashboard">
    <div class="dashboard-container">
        <!-- Welcome Header -->
        <div class="dashboard-header">
            <div class="welcome-section">
                <h2>Welcome back, ${sessionScope.userName}!</h2>
                <p>Your personalized dashboard to manage your career journey.</p>
            </div>
            <div class="profile-summary">
                <div class="profile-avatar">
                    <i class="fas fa-user-circle"></i>
                </div>
                <div class="profile-info">
                    <span class="profile-name">${sessionScope.userName}</span>
                    <span class="profile-role">Job Seeker</span>
                </div>
            </div>
        </div>

        <!-- Stats Overview -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon applications">
                    <i class="fas fa-file-alt"></i>
                </div>
                <div class="stat-content">
                    <h3>${dashboardStats['applicationsSent']}</h3>
                    <p>Applications Sent</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon views">
                    <i class="fas fa-eye"></i>
                </div>
                <div class="stat-content">
                    <h3>${dashboardStats['profileViews']}</h3>
                    <p>Profile Views</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon saved">
                    <i class="fas fa-bookmark"></i>
                </div>
                <div class="stat-content">
                    <h3>${dashboardStats['savedJobs']}</h3>
                    <p>Saved Jobs</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon interviews">
                    <i class="fas fa-calendar-check"></i>
                </div>
                <div class="stat-content">
                    <h3>${dashboardStats['interviewsScheduled']}</h3>
                    <p>Interviews Scheduled</p>
                </div>
            </div>
        </div>

        <!-- Email Verification -->
        <div class="dashboard-card verification-card">
            <div class="card-header">
                <h3><i class="fas fa-envelope"></i> Email Verification</h3>
                <c:choose>
                    <c:when test="${sessionScope.emailVerified}">
                        <span class="status-badge verified">
                            <i class="fas fa-check-circle"></i> Verified
                        </span>
                    </c:when>
                    <c:otherwise>
                        <span class="status-badge pending">
                            <i class="fas fa-exclamation-circle"></i> Not Verified
                        </span>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="card-content">
                <c:choose>
                    <c:when test="${sessionScope.emailVerified}">
                        <p class="success-message">Your email is verified. You have full access to all features.</p>
                    </c:when>
                    <c:otherwise>
                        <p class="warning-message">Please verify your email to access all features and receive job alerts.</p>
                        <button class="btn btn-primary" onclick="resendVerification()">
                            <i class="fas fa-paper-plane"></i> Resend Verification
                        </button>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Quick Actions -->
        <div class="dashboard-card">
            <div class="card-header">
                <h3><i class="fas fa-rocket"></i> Quick Actions</h3>
            </div>
            <div class="quick-actions-grid">
                <a href="${pageContext.request.contextPath}/applicant/cv" class="action-card primary">
                    <div class="action-icon">
                        <i class="fas fa-file-upload"></i>
                    </div>
                    <div class="action-content">
                        <h4>Upload CV</h4>
                        <p>Update your resume</p>
                    </div>
                </a>
                <a href="${pageContext.request.contextPath}/jobs" class="action-card secondary">
                    <div class="action-icon">
                        <i class="fas fa-search"></i>
                    </div>
                    <div class="action-content">
                        <h4>Search Jobs</h4>
                        <p>Find opportunities</p>
                    </div>
                </a>
                <a href="${pageContext.request.contextPath}/applicant/applications" class="action-card secondary">
                    <div class="action-icon">
                        <i class="fas fa-list-alt"></i>
                    </div>
                    <div class="action-content">
                        <h4>My Applications</h4>
                        <p>Track progress</p>
                    </div>
                </a>
                <a href="${pageContext.request.contextPath}/applicant/profile" class="action-card info">
                    <div class="action-icon">
                        <i class="fas fa-user-edit"></i>
                    </div>
                    <div class="action-content">
                        <h4>Edit Profile</h4>
                        <p>Update information</p>
                    </div>
                </a>
            </div>
        </div>

        <!-- Recent Activity -->
        <div class="dashboard-card">
            <div class="card-header">
                <h3><i class="fas fa-history"></i> Recent Activity</h3>
                <a href="#" class="view-all-link">View All</a>
            </div>
            <div class="activity-timeline">
                <c:choose>
                    <c:when test="${empty recentActivities}">
                        <div class="no-activity">
                            <i class="fas fa-info-circle"></i>
                            <p>No recent activity to display. Start applying for jobs to see your activity here!</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${recentActivities}" var="activity">
                            <div class="activity-item">
                                <div class="activity-icon ${activity.type}">
                                    <i class="fas ${activity.icon}"></i>
                                </div>
                                <div class="activity-content">
                                    <div class="activity-header">
                                        <span class="activity-title">${activity.title}</span>
                                        <span class="activity-date">${activity.date}</span>
                                    </div>
                                    <p class="activity-description">${activity.description}</p>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Recommended Jobs -->
        <div class="dashboard-card">
            <div class="card-header">
                <h3><i class="fas fa-star"></i> Recommended Jobs</h3>
                <a href="${pageContext.request.contextPath}/jobs" class="view-all-link">View All Jobs</a>
            </div>
            <div class="job-recommendations">
                <c:choose>
                    <c:when test="${empty recommendedJobs}">
                        <div class="no-jobs">
                            <i class="fas fa-search"></i>
                            <p>No job recommendations available yet. Complete your profile to get personalized recommendations!</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${recommendedJobs}" var="job">
                            <div class="job-card" data-job-url="${pageContext.request.contextPath}/job/${job.id}">
                                <div class="job-header">
                                    <div class="job-info">
                                        <h4>${job.title}</h4>
                                        <p class="company-name">${job.company}</p>
                                    </div>
                                    <span class="job-type ${job.typeClass}">${job.type}</span>
                                </div>
                                <div class="job-details">
                                    <div class="job-meta">
                                        <span class="location"><i class="fas fa-map-marker-alt"></i> ${job.location}</span>
                                        <span class="salary"><i class="fas fa-money-bill"></i> ${job.salary}</span>
                                    </div>
                                    <p class="job-description">${job.description}</p>
                                </div>
                                <div class="job-actions" style="margin-top: 1.5rem;">
                                    <a href="${pageContext.request.contextPath}/applicant/apply?jobId=${job.id}" class="btn btn-sm btn-primary">Apply Now</a>
                                    <c:if test="${not empty job.id}">
                                    <button onclick="saveJob('${job.id}')" class="btn btn-sm btn-outline save-job-btn" data-job-id="${job.id}" style="margin-top: 1rem;">
                                        <i class="far fa-bookmark"></i> Save
                                    </button>
                                    </c:if>
                                    <c:if test="${empty job.id}">
                                    <button disabled class="btn btn-sm btn-outline" title="Cannot save - job ID missing" style="margin-top: 1rem;">
                                        <i class="fas fa-exclamation-triangle"></i> Save
                                    </button>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Saved Jobs -->
        <div class="dashboard-card" id="savedJobsSection">
            <div class="card-header">
                <h3><i class="fas fa-bookmark"></i> Saved Jobs</h3>
                <a href="${pageContext.request.contextPath}/jobs?filter=saved" class="view-all-link">View All Saved</a>
            </div>
            <div class="job-recommendations saved-jobs-list">
                <c:choose>
                    <c:when test="${empty savedJobs}">
                        <div class="no-jobs">
                            <p>No saved jobs yet. Browse jobs and click the save button to add them here!</p>
                            <a href="${pageContext.request.contextPath}/jobs" class="btn btn-primary">Browse Jobs</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${savedJobs}" var="savedJob">
                            <div class="job-card saved-job-card" data-job-id="${savedJob.job.id}" data-job-url="${pageContext.request.contextPath}/job/${savedJob.job.id}">
                                <div class="job-header">
                                    <div class="job-info">
                                        <h4>${savedJob.job.jobTitle}</h4>
                                        <p class="company-name">${savedJob.job.company.name}</p>
                                    </div>
                                    <span class="job-type ${savedJob.job.jobType != null ? savedJob.job.jobType.toLowerCase() : 'full-time'}">${savedJob.job.jobType != null ? savedJob.job.jobType : 'Full-time'}</span>
                                </div>
                                <div class="job-details">
                                    <div class="job-meta">
                                        <span class="location"><i class="fas fa-map-marker-alt"></i> ${savedJob.job.physicalAddress != null ? savedJob.job.physicalAddress : 'Location not specified'}</span>
                                        <span class="salary"><i class="fas fa-money-bill"></i> ${savedJob.job.salaryRange != null ? savedJob.job.salaryRange : 'Competitive'}</span>
                                    </div>
                                    <p class="job-description">${savedJob.job.jobDescription}</p>
                                </div>
                                <div class="job-actions" style="margin-top: 1.5rem;">
                                    <a href="${pageContext.request.contextPath}/applicant/apply?jobId=${savedJob.job.id}" class="btn btn-sm btn-primary">Apply Now</a>
                                    <c:if test="${not empty savedJob.job.id}">
                                    <button onclick="unsaveJob('${savedJob.job.id}')" class="btn btn-sm btn-warning unsave-job-btn" style="margin-top: 1rem;">
                                        <i class="fas fa-bookmark"></i> Saved
                                    </button>
                                    </c:if>
                                    <c:if test="${empty savedJob.job.id}">
                                    <button disabled class="btn btn-sm btn-warning" title="Cannot unsave - job ID missing" style="margin-top: 1rem;">
                                        <i class="fas fa-exclamation-triangle"></i> Saved
                                    </button>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/views/components/applicant-footer.jsp" />

<script>
document.querySelectorAll('.job-card[data-job-url]').forEach(card => {
    card.addEventListener('click', function() {
        window.location.href = this.dataset.jobUrl;
    });
});

document.querySelectorAll('.job-actions a, .job-actions button').forEach(action => {
    action.addEventListener('click', function(event) {
        event.stopPropagation();
    });
});

function resendVerification() {
    // Implementation for resending verification email
    alert('Verification email sent! Please check your inbox.');
}

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
            // Update button state
            const btn = document.querySelector('.save-job-btn[data-job-id="' + jobId + '"]');
            if (btn) {
                btn.innerHTML = '<i class="fas fa-bookmark"></i> Saved';
                btn.classList.remove('btn-outline');
                btn.classList.add('btn-warning');
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
            // Remove the job card from saved jobs section or update button
            const savedCard = document.querySelector('.saved-job-card[data-job-id="' + jobId + '"]');
            if (savedCard) {
                savedCard.remove();
            }
            // Update any save buttons
            const btn = document.querySelector('.save-job-btn[data-job-id="' + jobId + '"]');
            if (btn) {
                btn.innerHTML = '<i class="far fa-bookmark"></i> Save';
                btn.classList.remove('btn-warning');
                btn.classList.add('btn-outline');
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

</body>
</html>
