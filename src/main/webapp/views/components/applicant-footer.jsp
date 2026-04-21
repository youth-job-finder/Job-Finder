<%--
  Applicant Dashboard Footer - Job Seeker Focus
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/applicant-footer.css">

<footer class="applicant-footer">
    <div class="container">
        <div class="footer-content">
            <div class="footer-section">
                <h3><i class="fas fa-user"></i> My Career</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/applicant/dashboard"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>
                    <li><a href="${pageContext.request.contextPath}/applicant/profile"><i class="fas fa-edit"></i> Edit Profile</a></li>
                    <li><a href="${pageContext.request.contextPath}/applicant/cv"><i class="fas fa-file-alt"></i> My Resume</a></li>
                    <li><a href="${pageContext.request.contextPath}/applicant/applications"><i class="fas fa-paper-plane"></i> Applications</a></li>
                    <li><a href="${pageContext.request.contextPath}/jobs?filter=saved"><i class="fas fa-bookmark"></i> Saved Jobs</a></li>
                </ul>
            </div>
            
            <div class="footer-section">
                <h3><i class="fas fa-chart-line"></i> Career Progress</h3>
                <div class="career-progress">
                    <p><i class="fas fa-info-circle"></i> Track your profile completion, resume status, and application activity from your dashboard.</p>
                </div>
            </div>
            
            <div class="footer-section">
                <h3><i class="fas fa-lightbulb"></i> Recommended</h3>
                <div class="recommendations">
                    <div class="rec-item">
                        <i class="fas fa-graduation-cap"></i>
                        <div>
                            <strong>Skills Development</strong>
                            <p>Complete your profile with in-demand skills</p>
                        </div>
                    </div>
                    <div class="rec-item">
                        <i class="fas fa-certificate"></i>
                        <div>
                            <strong>Certifications</strong>
                            <p>Add relevant certifications to stand out</p>
                        </div>
                    </div>
                    <div class="rec-item">
                        <i class="fas fa-network-wired"></i>
                        <div>
                            <strong>Networking</strong>
                            <p>Connect with professionals in your field</p>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="footer-section">
                <h3><i class="fas fa-bell"></i> Stay Updated</h3>
                <p>Get notified about new jobs matching your profile</p>
                <div class="job-alert-signup">
                    <p><i class="fas fa-envelope"></i> Subscribe to job alerts</p>
                    <button class="btn-alert" onclick="subscribeToAlerts()">
                        <i class="fas fa-bell"></i> Enable Alerts
                    </button>
                </div>
            </div>
        </div>
        
        <div class="footer-bottom">
            <p>&copy; 2026 JobFinder Career Portal | <a href="${pageContext.request.contextPath}/contact">Applicant Help</a> | <a href="${pageContext.request.contextPath}/terms">Terms of Service</a> | <a href="${pageContext.request.contextPath}/privacy">Privacy Policy</a></p>
        </div>
    </div>
</footer>

<script>
function subscribeToAlerts() {
    // Simple notification for demo
    const button = document.querySelector('.btn-alert');
    const originalText = button.innerHTML;
    
    button.innerHTML = '<i class="fas fa-check"></i> Alerts Enabled!';
    button.style.background = 'linear-gradient(45deg, #27ae60, #229954)';
    
    setTimeout(() => {
        button.innerHTML = originalText;
        button.style.background = '';
    }, 2000);
}
</script>
