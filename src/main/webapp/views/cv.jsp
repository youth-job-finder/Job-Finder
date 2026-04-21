<%--
  Created by IntelliJ IDEA.
  User: aubre
  Date: 2026/03/30
  Time: 11:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload / Update CV - JobFinder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cv.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<!-- Signed-in Navbar -->
<jsp:include page="/views/components/user-navbar.jsp" />

<section class="cv-section">
    <div class="cv-container">
        <h2><i class="fas fa-file-upload"></i> Upload / Update Your CV</h2>
        <p>Keep your resume up to date to improve your chances of landing jobs and internships.</p>

        <!-- Show current CV if available -->
        <c:choose>
            <c:when test="${not empty cvFileName}">
                <div class="current-cv-card">
                    <div class="cv-info">
                        <div class="cv-icon">
                            <i class="fas fa-file-pdf"></i>
                        </div>
                        <div class="cv-details">
                            <h3>${cvFileName}</h3>
                            <p class="upload-date">
                                <i class="fas fa-calendar-alt"></i>
                                Uploaded: ${cvUploadedAt != null ? cvUploadedAt : 'Recently'}
                            </p>
                        </div>
                    </div>
                    <div class="cv-actions">
                        <a href="${pageContext.request.contextPath}/view-cv"
                           target="_blank" class="btn btn-view">
                            <i class="fas fa-eye"></i> View CV
                        </a>
                        <form action="${pageContext.request.contextPath}/applicant/cv" method="post" class="inline-form">
                            <input type="hidden" name="action" value="download">
                            <button type="submit" class="btn btn-download">
                                <i class="fas fa-download"></i> Download
                            </button>
                        </form>
                        <form action="${pageContext.request.contextPath}/applicant/cv" method="post" class="inline-form"
                              onsubmit="return confirm('Are you sure you want to delete your CV? This action cannot be undone.');">
                            <input type="hidden" name="action" value="delete">
                            <button type="submit" class="btn btn-delete">
                                <i class="fas fa-trash-alt"></i> Delete
                            </button>
                        </form>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="no-cv-message">
                    <i class="fas fa-file-upload fa-3x"></i>
                    <p>No CV uploaded yet. Please upload your resume below to get started.</p>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Upload form -->
        <form action="${pageContext.request.contextPath}/applicant/cv" method="post" enctype="multipart/form-data" class="cv-form">
            <div class="form-group">
                <label for="cvFile">Choose CV File (PDF or DOCX):</label>
                <input type="file" id="cvFile" name="cvFile" accept=".pdf,.doc,.docx" required>
            </div>
            <button type="submit" class="btn btn-primary">
                <i class="fas fa-upload"></i> Upload / Update CV
            </button>
        </form>

        <!-- Feedback messages -->
        <c:if test="${not empty requestScope.successMessage}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i> ${requestScope.successMessage}
            </div>
        </c:if>
        <c:if test="${not empty requestScope.errorMessage}">
            <div class="alert alert-error">
                <i class="fas fa-exclamation-triangle"></i> ${requestScope.errorMessage}
            </div>
        </c:if>
    </div>
</section>

<jsp:include page="/views/components/applicant-footer.jsp" />

</body>
</html>