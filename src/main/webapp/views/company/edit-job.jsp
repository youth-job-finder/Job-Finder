<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Job - Youth Job & Internship Finder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/company-navbar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/company-list-job.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/views/components/company-navbar.jsp" />

<section class="list-job">
    <div class="list-job-container">
        <h2><i class="fas fa-edit"></i> Edit Job</h2>
        <p>Update your job posting details below.</p>

        <!-- Error Message -->
        <c:if test="${not empty error}">
            <div class="alert alert-error">
                <i class="fas fa-exclamation-circle"></i>
                <span>${error}</span>
            </div>
        </c:if>

        <!-- Success Message -->
        <c:if test="${not empty success}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i>
                <span>${success}</span>
            </div>
        </c:if>

        <div class="form-card">
            <form action="${pageContext.request.contextPath}/company/edit-job" method="post">
                <input type="hidden" name="jobId" value="${job.id}">

                <div class="form-group">
                    <label for="jobTitle">
                        <i class="fas fa-briefcase"></i> Job Title <span class="required">*</span>
                    </label>
                    <input type="text" id="jobTitle" name="jobTitle" required
                           placeholder="e.g., Software Developer Intern"
                           value="${job.jobTitle}">
                    <small>Enter a clear and descriptive job title</small>
                </div>

                <div class="form-group">
                    <label for="jobDescription">
                        <i class="fas fa-align-left"></i> Job Description <span class="required">*</span>
                    </label>
                    <textarea id="jobDescription" name="jobDescription" rows="6" required
                              placeholder="Describe the job responsibilities, what the candidate will do, and any other relevant details...">${job.jobDescription}</textarea>
                    <small>Provide a detailed description of the role</small>
                </div>

                <div class="form-group">
                    <label for="jobRequirements">
                        <i class="fas fa-list-check"></i> Job Requirements
                    </label>
                    <textarea id="jobRequirements" name="jobRequirements" rows="4"
                              placeholder="List required skills, qualifications, experience level, education requirements...">${job.jobRequirements}</textarea>
                    <small>List qualifications, skills, and experience requirements</small>
                </div>

                <div class="form-row">
                    <div class="form-group half">
                        <label for="salaryRange">
                            <i class="fas fa-money-bill"></i> Salary Range
                        </label>
                        <input type="text" id="salaryRange" name="salaryRange"
                               placeholder="e.g., R50,000 - R70,000 or Competitive"
                               value="${job.salaryRange}">
                        <small>Specify the salary range or compensation</small>
                    </div>

                    <div class="form-group half">
                        <label for="jobType">
                            <i class="fas fa-clock"></i> Job Type
                        </label>
                        <select id="jobType" name="jobType">
                            <option value="Full-time" ${job.jobType == 'Full-time' ? 'selected' : ''}>Full-time</option>
                            <option value="Part-time" ${job.jobType == 'Part-time' ? 'selected' : ''}>Part-time</option>
                            <option value="Contract" ${job.jobType == 'Contract' ? 'selected' : ''}>Contract</option>
                            <option value="Internship" ${job.jobType == 'Internship' ? 'selected' : ''}>Internship</option>
                            <option value="Remote" ${job.jobType == 'Remote' ? 'selected' : ''}>Remote</option>
                        </select>
                        <small>Select the type of employment</small>
                    </div>
                </div>

                <div class="form-group">
                    <label for="physicalAddress">
                        <i class="fas fa-map-marker-alt"></i> Physical Address
                    </label>
                    <input type="text" id="physicalAddress" name="physicalAddress"
                           placeholder="e.g., 123 Main Street, Johannesburg, 2000"
                           value="${job.physicalAddress}">
                    <small>Enter the job location or office address</small>
                </div>

                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/company/listings" class="btn btn-secondary">
                        <i class="fas fa-times"></i> Cancel
                    </a>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> Save Changes
                    </button>
                </div>
            </form>
        </div>

        <!-- Help Card -->
        <div class="help-card">
            <h3><i class="fas fa-lightbulb"></i> Tips for Updating Job Postings</h3>
            <ul>
                <li><i class="fas fa-check"></i> Keep the job title clear and specific</li>
                <li><i class="fas fa-check"></i> Update the description to reflect any changes in responsibilities</li>
                <li><i class="fas fa-check"></i> Review requirements to ensure they match current needs</li>
                <li><i class="fas fa-check"></i> Update salary range if compensation has changed</li>
                <li><i class="fas fa-check"></i> Verify location details are still accurate</li>
            </ul>
        </div>
    </div>
</section>

<jsp:include page="/views/components/company-footer.jsp" />

</body>
</html>
