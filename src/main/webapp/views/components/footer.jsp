<%--
  Created by IntelliJ IDEA.
  User: aubre
  Date: 2026/03/24
  Time: 22:50
  
  Modified by: Pilot
  Date: 2026/04/13
  Description: Updated legacy JSTL taglib to modern Jakarta EE 10 standard to fix GlassFish 500 errors.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<footer class="footer">
    <div class="container">
        <div class="footer-content">
            <div class="footer-section">
                <h3>
                    <a href="${pageContext.request.contextPath}/home" class="footer-brand-link" aria-label="JobFinder home">
                        <span class="logo-icon"><i class="fas fa-search"></i></span>
                        <span>JobFinder</span>
                    </a>
                </h3>
                <p>Your gateway to amazing career opportunities and internships.</p>
                <div class="social-links">
                    <a href="#" aria-label="Facebook"><i class="fab fa-facebook"></i></a>
                    <a href="#" aria-label="Twitter"><i class="fab fa-twitter"></i></a>
                    <a href="#" aria-label="LinkedIn"><i class="fab fa-linkedin"></i></a>
                    <a href="#" aria-label="Instagram"><i class="fab fa-instagram"></i></a>
                </div>
            </div>
            
            <div class="footer-section">
                <h4>Quick Links</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/home">Home</a></li>
                    <li><a href="${pageContext.request.contextPath}/jobs">Jobs</a></li>
                    <li><a href="${pageContext.request.contextPath}/companies">Companies</a></li>
                </ul>
            </div>
            
            <div class="footer-section">
                <h4>Support</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/about">About Us</a></li>
                    <li><a href="${pageContext.request.contextPath}/contact">Contact</a></li>
                    <li><a href="${pageContext.request.contextPath}/faq">FAQ</a></li>
                    <li><a href="${pageContext.request.contextPath}/privacy">Privacy Policy</a></li>
                </ul>
            </div>
            
            <div class="footer-section">
                <h4>Contact Info</h4>
                <p><i class="fas fa-envelope"></i> info@jobfinder.com</p>
                <p><i class="fas fa-phone"></i> +27 (71) 234-5678</p>
                <p><i class="fas fa-map-marker-alt"></i> Cnr R40 and D725 Roads, Mbombela, 1200</p>
            </div>
        </div>
        
        <div class="footer-bottom">
            <p>&copy; 2026 JobFinder. All rights reserved. | <a href="${pageContext.request.contextPath}/terms">Terms of Service</a> | <a href="${pageContext.request.contextPath}/privacy">Privacy Policy</a></p>
        </div>
    </div>
</footer>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer-component.css">