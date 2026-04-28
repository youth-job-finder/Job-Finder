<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Apply for Job - Youth Job & Internship Finder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/applicant-applications.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/apply-job.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/views/components/user-navbar.jsp" />

<section class="apply-job">
    <div class="apply-container">
        <h2><i class="fas fa-paper-plane"></i> Apply for Job</h2>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">
                <i class="fas fa-exclamation-circle"></i>
                <span>${error}</span>
            </div>
        </c:if>

        <c:if test="${param.cvUploaded eq 'true'}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i>
                <span>Your CV was uploaded successfully. You can now submit your application.</span>
            </div>
        </c:if>
        
        <c:if test="${not empty job}">
            <div class="job-summary">
                <div class="job-header">
                    <h3>${job.jobTitle}</h3>
                    <span class="company-name">
                        <i class="fas fa-building"></i> ${job.company.name}
                    </span>
                </div>
                
                <div class="job-details">
                    <c:if test="${not empty job.physicalAddress}">
                        <div class="detail-item">
                            <i class="fas fa-map-marker-alt"></i>
                            <span>${job.physicalAddress}</span>
                        </div>
                    </c:if>
                    <div class="detail-item">
                        <i class="fas fa-calendar"></i>
                        <span>Posted: ${job.createdAt}</span>
                    </div>
                </div>
                
                <div class="job-description">
                    <h4>Job Description</h4>
                    <p>${job.jobDescription}</p>
                </div>
                
                <c:if test="${not empty job.jobRequirements}">
                    <div class="job-requirements">
                        <h4>Requirements</h4>
                        <p>${job.jobRequirements}</p>
                    </div>
                </c:if>
            </div>
            
            <div class="application-form">
                <h4><i class="fas fa-user-check"></i> Confirm Your Application</h4>
                <p>You are about to apply for this position. Please review the job details above before confirming.</p>

                <div class="applicant-info">
                    <div class="info-row">
                        <span class="info-label">Applicant Name:</span>
                        <span class="info-value">${sessionScope.userName}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Email:</span>
                        <span class="info-value">${sessionScope.userEmail}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Attached CV:</span>
                        <span class="info-value">
                            <c:choose>
                                <c:when test="${not empty cvFileName}">
                                    <i class="fas fa-file-pdf"></i> ${cvFileName}
                                </c:when>
                                <c:otherwise>
                                    <i class="fas fa-exclamation-triangle"></i> No CV uploaded
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                </div>
                
                <form action="${pageContext.request.contextPath}/applicant/apply" method="post">
                    <input type="hidden" name="jobId" value="${job.id}">

                    <div class="form-actions">
                        <a href="${pageContext.request.contextPath}/jobs" class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Cancel
                        </a>
                        <c:choose>
                            <c:when test="${missingCv}">
                                <a href="${pageContext.request.contextPath}/applicant/cv?returnJobId=${job.id}" class="btn btn-primary">
                                    <i class="fas fa-upload"></i> Upload CV to Continue
                                </a>
                            </c:when>
                            <c:otherwise>
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-paper-plane"></i> Submit Application
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </form>
            </div>
        </c:if>
        
        <c:if test="${empty job}">
            <div class="no-job">
                <i class="fas fa-exclamation-triangle"></i>
                <h3>Job Not Found</h3>
                <p>The job you are trying to apply for could not be found.</p>
                <a href="${pageContext.request.contextPath}/jobs" class="btn btn-primary">
                    <i class="fas fa-search"></i> Browse Jobs
                </a>
            </div>
        </c:if>
    </div>
</section>

<jsp:include page="/views/components/applicant-footer.jsp" />

</body>
</html>
