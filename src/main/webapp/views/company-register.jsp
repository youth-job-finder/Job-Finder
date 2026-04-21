<%--
  Combined Company and Company Admin Registration Page
  
  This form collects both company admin user information and company details.
  On submission, both the user (with COMPANY_ADMIN role) and company are created.
  The company admin is linked to the company via user_id.
  
  Flow:
  1. User submits form with admin + company info
  2. Company admin user is created with email verification token
  3. Company is created linked to the admin user
  4. Email verification sent to admin
  5. Company URL is verified automatically
  6. Company status set to PENDING (awaiting system admin approval)
  7. System admins notified of pending approval
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Company Registration - JobFinder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mobile-responsive.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<jsp:include page="/views/components/default-navbar.jsp" />

<section class="signup-section">
    <div class="signup-container">
        <h2><i class="fas fa-building"></i> Register Your Company</h2>
        <p class="form-description">Create a company account to post jobs and connect with talent. Both you and your company will be registered.</p>

        <c:if test="${not empty error}">
            <div class="alert alert-error">
                <i class="fas fa-exclamation-circle"></i> ${error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/company-register" method="post" class="signup-form">
            
            <!-- Company Admin Section -->
            <div class="form-section">
                <h3><i class="fas fa-user-tie"></i> Company Administrator</h3>
                <p class="section-description">This person will manage the company account and job postings.</p>
                
                <div class="form-group">
                    <label for="adminName">Full Name <span class="required">*</span></label>
                    <input type="text" id="adminName" name="adminName" 
                           value="${adminName}" required 
                           placeholder="Enter administrator's full name">
                </div>

                <div class="form-group">
                    <label for="adminEmail">Email Address <span class="required">*</span></label>
                    <input type="email" id="adminEmail" name="adminEmail" 
                           value="${adminEmail}" required 
                           placeholder="admin@company.com">
                    <small class="form-hint">This will be your login email. A verification link will be sent.</small>
                </div>

                <div class="form-group">
                    <label for="adminPassword">Password <span class="required">*</span></label>
                    <input type="password" id="adminPassword" name="adminPassword" 
                           required minlength="8"
                           placeholder="At least 8 characters">
                    
                    <div class="password-match" id="passwordMatch"></div>
                    
                    <div class="password-requirements">
                        <small class="requirement" id="length"><i class="fas fa-times"></i> At least 8 characters</small>
                        <small class="requirement" id="uppercase"><i class="fas fa-times"></i> One uppercase letter</small>
                        <small class="requirement" id="lowercase"><i class="fas fa-times"></i> One lowercase letter</small>
                        <small class="requirement" id="number"><i class="fas fa-times"></i> One number</small>
                        <small class="requirement" id="special"><i class="fas fa-times"></i> One special character (@$!%*?&)</small>
                    </div>
                </div>

                <div class="form-group">
                    <label for="confirmPassword">Confirm Password <span class="required">*</span></label>
                    <input type="password" id="confirmPassword" name="confirmPassword" 
                           required minlength="8"
                           placeholder="Re-enter password">
                </div>
            </div>

            <!-- Company Section -->
            <div class="form-section">
                <h3><i class="fas fa-building"></i> Company Information</h3>
                
                <div class="form-group">
                    <label for="companyName">Company Name <span class="required">*</span></label>
                    <input type="text" id="companyName" name="companyName" 
                           value="${companyName}" required 
                           placeholder="Enter company name">
                </div>

                <div class="form-group">
                    <label for="registrationNumber">Registration Number <span class="required">*</span></label>
                    <input type="text" id="registrationNumber" name="registrationNumber" 
                           value="${registrationNumber}" required 
                           placeholder="e.g., CAC/IT/123456">
                    <small class="form-hint">Your official business registration number</small>
                </div>

                <div class="form-group">
                    <label for="companyEmail">Company Email <span class="required">*</span></label>
                    <input type="email" id="companyEmail" name="companyEmail" 
                           value="${companyEmail}" required 
                           placeholder="contact@company.com">
                </div>

                <div class="form-group">
                    <label for="companyUrl">Company Website</label>
                    <input type="url" id="companyUrl" name="companyUrl" 
                           value="${companyUrl}"
                           placeholder="https://www.company.com">
                    <small class="form-hint">We'll automatically verify this URL</small>
                </div>

                <div class="form-group">
                    <label for="description">Company Description</label>
                    <textarea id="description" name="description" rows="4" 
                              placeholder="Briefly describe your company, its mission, and what you do...">${description}</textarea>
                    <small class="form-hint">Max 1000 characters. This will be displayed on your company profile.</small>
                </div>

                <div class="form-group">
                    <label for="industry">Industry</label>
                    <input type="text" id="industry" name="industry" 
                           value="${industry}" 
                           placeholder="e.g., Technology, Healthcare, Finance">
                    <small class="form-hint">The industry your company operates in</small>
                </div>

                <div class="form-group">
                    <label for="location">Location</label>
                    <input type="text" id="location" name="location" 
                           value="${location}" 
                           placeholder="e.g., Johannesburg, South Africa">
                    <small class="form-hint">Your company's primary location or headquarters</small>
                </div>
            </div>

            <!-- Terms and Submit -->
            <div class="form-section">
                <div class="form-group checkbox-group">
                    <label class="checkbox-label">
                        <input type="checkbox" name="terms" required>
                        <span class="checkmark"></span>
                        I agree to the <a href="#" target="_blank">Terms of Service</a> and 
                        <a href="#" target="_blank">Privacy Policy</a>
                    </label>
                </div>

                <button type="submit" class="btn btn-primary btn-block">
                    <i class="fas fa-paper-plane"></i> Register Company
                </button>
            </div>

            <div class="form-footer">
                <p>Already have an account? <a href="${pageContext.request.contextPath}/login">Log in</a></p>
                <p>Looking for a job? <a href="${pageContext.request.contextPath}/signup">Register as applicant</a></p>
            </div>
        </form>

        <!-- Info Box -->
        <div class="info-box">
            <h4><i class="fas fa-info-circle"></i> What happens next?</h4>
            <ol>
                <li><strong>Email Verification:</strong> Check your email and click the verification link.</li>
                <li><strong>URL Verification:</strong> We'll automatically verify your company website.</li>
                <li><strong>Admin Review:</strong> Your registration will be reviewed by our team.</li>
                <li><strong>Approval:</strong> You'll receive an email once approved and can start posting jobs!</li>
            </ol>
        </div>
    </div>
</section>

<jsp:include page="/views/components/footer.jsp" />

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const password = document.getElementById('adminPassword');
        const confirmPassword = document.getElementById('confirmPassword');
        const reqs = {
            length: document.getElementById('length'),
            uppercase: document.getElementById('uppercase'),
            lowercase: document.getElementById('lowercase'),
            number: document.getElementById('number'),
            special: document.getElementById('special')
        };
        const passwordMatch = document.getElementById('passwordMatch');

        function update(el, valid) {
            el.className = 'requirement ' + (valid ? 'valid' : 'invalid');
            el.querySelector('i').className = 'fas fa-' + (valid ? 'check' : 'times');
        }

        function validate() {
            const v = password.value;
            update(reqs.length, v.length >= 8);
            update(reqs.uppercase, /[A-Z]/.test(v));
            update(reqs.lowercase, /[a-z]/.test(v));
            update(reqs.number, /\d/.test(v));
            update(reqs.special, /[@$!%*?&]/.test(v));
            checkMatch();
        }

        function checkMatch() {
            if (!confirmPassword.value) { passwordMatch.textContent = ''; return; }
            const match = password.value === confirmPassword.value;
            passwordMatch.innerHTML = '<i class="fas fa-' + (match ? 'check' : 'times') + '"></i> Passwords ' + (match ? 'match' : 'do not match');
            passwordMatch.className = 'password-match ' + (match ? 'match' : 'mismatch');
        }

        password.addEventListener('input', validate);
        confirmPassword.addEventListener('input', checkMatch);
    });
</script>

</body>
</html>
