<%--
  Company Dashboard Footer - Business & Employer Focus
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/company-footer.css">

<footer class="company-footer">
    <div class="container">
        <div class="footer-content">
            <div class="footer-section">
                <h3><i class="fas fa-briefcase"></i> Employer Tools</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/company/dashboard"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>
                    <li><a href="${pageContext.request.contextPath}/company/list-job"><i class="fas fa-plus-circle"></i> Post New Job</a></li>
                    <li><a href="${pageContext.request.contextPath}/company/listings"><i class="fas fa-list"></i> Manage Listings</a></li>
                    <li><a href="${pageContext.request.contextPath}/company/applicants"><i class="fas fa-users"></i> View Applicants</a></li>
                    <li><a href="${pageContext.request.contextPath}/company/analytics"><i class="fas fa-chart-bar"></i> Job Analytics</a></li>
                </ul>
            </div>
            
            <div class="footer-section">
                <h3><i class="fas fa-building"></i> Company Profile</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/company/profile"><i class="fas fa-edit"></i> Edit Profile</a></li>
                    <li><a href="${pageContext.request.contextPath}/company/reviews"><i class="fas fa-star"></i> Company Reviews</a></li>
                    <li><a href="${pageContext.request.contextPath}/jobs"><i class="fas fa-briefcase"></i> Browse Open Roles</a></li>
                    <li><a href="${pageContext.request.contextPath}/contact"><i class="fas fa-headset"></i> Contact Support</a></li>
                    <li><a href="${pageContext.request.contextPath}/terms"><i class="fas fa-scale-balanced"></i> Terms of Service</a></li>
                </ul>
            </div>
            
            <div class="footer-section">
                <h3><i class="fas fa-lightbulb"></i> Hiring Tips</h3>
                <div class="tips-section">
                    <div class="tip-item">
                        <i class="fas fa-check-circle"></i>
                        <div>
                            <strong>Write Clear Job Descriptions</strong>
                            <p>Be specific about requirements and responsibilities</p>
                        </div>
                    </div>
                    <div class="tip-item">
                        <i class="fas fa-check-circle"></i>
                        <div>
                            <strong>Respond Quickly</strong>
                            <p>Average response time affects candidate interest</p>
                        </div>
                    </div>
                    <div class="tip-item">
                        <i class="fas fa-check-circle"></i>
                        <div>
                            <strong>Showcase Company Culture</strong>
                            <p>Highlight what makes your workplace unique</p>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="footer-section">
                <h3><i class="fas fa-chart-line"></i> Your Activity</h3>
                <div class="company-quick-stats">
                    <p><i class="fas fa-info-circle"></i> View your job postings, applications received, and analytics from your dashboard.</p>
                </div>
            </div>
        </div>
        
        <div class="footer-bottom">
            <p>&copy; 2026 JobFinder for Business | <a href="${pageContext.request.contextPath}/company/dashboard">Employer Dashboard</a> | <a href="${pageContext.request.contextPath}/terms">Terms of Service</a> | <a href="${pageContext.request.contextPath}/contact">Contact Support</a></p>
        </div>
    </div>
</footer>
