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

    <style>
        /* 🏛️ Enterprise Navy & Gold Styling Overlays (The "Blend" Theme) */
        :root {
            --deep-navy: #0f172a;
            --slate-navy: #1e293b;
            --darker-navy: #0b1120;
            --accent-gold: #d4af37;
            --slate-text: #94a3b8;
            --glass-white: rgba(255, 255, 255, 0.05);
        }

        body {
            background-color: var(--deep-navy) !important;
            color: #f1f5f9 !important;
            margin: 0;
            font-family: 'Segoe UI', Arial, sans-serif;
        }

        /* Header & Titles */
        .page-header {
            background: linear-gradient(to bottom, #000, var(--deep-navy)) !important;
            border-bottom: 2px solid rgba(212, 175, 55, 0.1);
            padding: 60px 0;
            text-align: center;
        }
        .page-title { color: var(--accent-gold) !important; font-weight: 800 !important; font-size: 2.8rem; margin-bottom: 0.5rem; }
        .page-subtitle { color: var(--slate-text) !important; font-size: 1.1rem; }

        /* Search Section */
        .search-section { background-color: var(--deep-navy) !important; padding: 40px 0 50px !important; }
        .search-box {
            background: white !important;
            border: 2px solid var(--accent-gold) !important;
            border-radius: 50px !important;
            display: flex;
            align-items: center;
            padding: 5px 20px;
            max-width: 650px;
            margin: 0 auto 20px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.2);
        }
        .search-input { 
            color: #0f172a !important; 
            font-weight: 500 !important; 
            border: none !important; 
            outline: none !important; 
            width: 100%;
            padding: 12px;
            background: transparent !important;
        }

        /* Filter Buttons */
        .filter-buttons { display: flex; justify-content: center; gap: 15px; }
        .filter-btn {
            background: var(--glass-white) !important;
            border: 1px solid rgba(255,255,255,0.1) !important;
            color: white !important;
            border-radius: 50px !important;
            padding: 10px 25px;
            text-decoration: none;
            transition: all 0.3s ease;
            font-weight: 600;
        }
        .filter-btn.active, .filter-btn:hover {
            background: var(--accent-gold) !important;
            color: var(--deep-navy) !important;
            border-color: var(--accent-gold) !important;
        }

        /* Job Cards */
        .jobs-section { background-color: var(--darker-navy) !important; padding: 60px 0; min-height: 50vh; }
        .dashboard-card { background: transparent !important; border: none !important; box-shadow: none !important; }
        .card-header { border-bottom: 1px solid rgba(255,255,255,0.05); margin-bottom: 30px; display: flex; justify-content: space-between; align-items: center; padding-bottom: 15px;}
        .card-header h3 { color: var(--accent-gold) !important; font-size: 1.5rem; margin: 0;}
        
        .job-card {
            background: var(--slate-navy) !important; 
            border: 1px solid rgba(255,255,255,0.05) !important;
            border-radius: 16px !important;
            padding: 25px !important;
            transition: all 0.3s ease !important;
            cursor: pointer;
        }
        .job-card:hover { transform: translateY(-5px) !important; border-color: var(--accent-gold) !important; box-shadow: 0 10px 20px rgba(0,0,0,0.2) !important;}
        .job-card h4 { color: var(--accent-gold) !important; font-weight: 700 !important; margin: 0 0 5px 0; font-size: 1.3rem;}
        .company-name { color: #cbd5e1 !important; font-weight: 600 !important; margin: 0 0 15px 0; }
        .job-description { color: var(--slate-text) !important; font-size: 0.95rem !important; margin-bottom: 20px; line-height: 1.6;}
        
        /* Badges & Meta */
        .job-type { background: var(--accent-gold) !important; color: var(--deep-navy) !important; font-weight: 700 !important; padding: 4px 12px; border-radius: 4px; font-size: 0.8rem; }
        .job-meta { margin-bottom: 15px; display: flex; gap: 15px; flex-wrap: wrap;}
        .job-meta span { color: #64748b !important; font-size: 0.9rem; }
        .job-meta i { color: var(--accent-gold) !important; margin-right: 5px; }

        /* Buttons */
        .btn-primary { background: var(--accent-gold) !important; color: var(--deep-navy) !important; font-weight: 700 !important; border: none !important; cursor: pointer; padding: 8px 20px; border-radius: 50px; text-decoration: none;}
        .btn-outline { border: 1px solid var(--accent-gold) !important; color: var(--accent-gold) !important; text-decoration: none; padding: 8px 20px; border-radius: 50px; font-size: 0.85rem; background: transparent !important;}
        .btn-warning { background: #f59e0b !important; color: var(--deep-navy) !important; border: none !important; padding: 8px 20px; border-radius: 50px; font-weight: 700;}
        .view-all-link { color: var(--accent-gold) !important; font-weight: 600 !important; text-decoration: none; }
        .view-all-link:hover { text-decoration: underline; }
    </style>
</head>
<body>

<c:choose>
    <c:when test="${isAuthenticated}">
        <c:choose>
            <c:when test="${userRole == 'APPLICANT'}"><jsp:include page="/views/components/user-navbar.jsp" /></c:when>
            <c:when test="${userRole == 'COMPANY_ADMIN'}"><jsp:include page="/views/components/company-navbar.jsp" /></c:when>
            <c:when test="${userRole == 'SYSTEM_ADMIN'}"><jsp:include page="/views/components/system-navbar.jsp" /></c:when>
            <c:otherwise><jsp:include page="/views/components/user-navbar.jsp" /></c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise><jsp:include page="/views/components/default-navbar.jsp" /></c:otherwise>
</c:choose>

<section class="page-header">
    <div class="container">
        <h1 class="page-title">Job Opportunities</h1>
        <p class="page-subtitle">Discover your next career move from our curated job listings</p>
    </div>
</section>

<section class="search-section">
    <div class="container">
        <div class="search-container">
            <div class="search-box">
                <i class="fas fa-search" style="color: #64748b; margin-left: 10px;"></i>
                <input type="text" placeholder="Search jobs by title, company, or keyword..." class="search-input">
            </div>
            <div class="filter-buttons">
                <a href="${pageContext.request.contextPath}/jobs" class="filter-btn ${empty param.filter ? 'active' : ''}">
                    <i class="fas fa-briefcase"></i> All Jobs
                </a>
                <a href="${pageContext.request.contextPath}/jobs?filter=remote" class="filter-btn ${param.filter == 'remote' ? 'active' : ''}">
                    <i class="fas fa-laptop"></i> Remote
                </a>
                <a href="${isAuthenticated && userRole == 'APPLICANT' ? pageContext.request.contextPath.concat('/jobs?filter=saved') : pageContext.request.contextPath.concat('/login')}" class="filter-btn ${param.filter == 'saved' ? 'active' : ''}">
                    <i class="fas fa-bookmark"></i> Saved Jobs
                </a>
            </div>
        </div>
    </div>
</section>

<section class="jobs-section">
    <div class="container">
        <div class="dashboard-card">
            <div class="card-header">
                <h3><i class="fas fa-star"></i> Recommended Jobs</h3>
                <a href="${pageContext.request.contextPath}/jobs" class="view-all-link">View All Jobs</a>
            </div>
            <div class="job-recommendations" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 20px;">
                <c:choose>
                    <c:when test="${empty jobs}">
                        <div class="no-jobs" style="grid-column: 1/-1; text-align: center; padding: 50px;">
                            <i class="fas fa-search fa-3x" style="opacity: 0.2; margin-bottom: 15px; color: #d4af37;"></i>
                            <p style="color: #94a3b8;">No job listings available at the moment. Check back soon for new opportunities!</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${jobs}" var="job">
                            <div class="job-card" data-job-url="${pageContext.request.contextPath}/job/${job.id}">
                                <div class="job-header" style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 15px;">
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
                                    <p class="job-description">${fn:substring(job.jobDescription, 0, 150)}...</p>
                                </div>
                                <div class="job-actions" style="display: flex; gap: 10px; margin-top: 20px;">
                                    <a href="${isAuthenticated && userRole == 'APPLICANT' ? pageContext.request.contextPath.concat('/applicant/apply?jobId=').concat(job.id) : pageContext.request.contextPath.concat('/login')}" class="btn btn-sm btn-primary">Apply Now</a>
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
    </div>
</section>

<c:choose>
    <c:when test="${isAuthenticated}">
        <c:choose>
            <c:when test="${userRole == 'APPLICANT'}"><jsp:include page="/views/components/applicant-footer.jsp" /></c:when>
            <c:when test="${userRole == 'COMPANY_ADMIN'}"><jsp:include page="/views/components/company-footer.jsp" /></c:when>
            <c:when test="${userRole == 'SYSTEM_ADMIN'}"><jsp:include page="/views/components/admin-footer.jsp" /></c:when>
            <c:otherwise><jsp:include page="/views/components/applicant-footer.jsp" /></c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise><jsp:include page="/views/components/footer.jsp" /></c:otherwise>
</c:choose>

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

<c:if test="${isAuthenticated && userRole == 'APPLICANT'}">
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
</c:if>
</script>

</body>
</html>
