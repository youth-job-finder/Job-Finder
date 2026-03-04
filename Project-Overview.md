

# Youth Job & Internship Finder Web Application

## 1. Project Overview
The **Youth Job & Internship Finder** is a web-based platform designed to connect students with legitimate companies offering jobs and internships. The system emphasizes **trust, transparency, and accessibility** through company vetting workflows, student reviews, and admin oversight.

### Objectives
- Provide students with a secure, user-friendly portal to discover opportunities, manage applications, and track progress.
- Enable companies to post verified job listings, manage applicants, and gain insights through analytics.
- Empower administrators to oversee the ecosystem, ensuring legitimacy and fairness via semi-automated vetting and approval workflows.

---

## 2. User Roles & Permissions
### Students
- Register with a valid student/personal email.
- Upload and update CVs.
- Apply for jobs/internships and track application status.
- Review companies and flag suspicious behavior.
- Personalized dashboard with recommendations.

### Companies
- Register with company details (registration number, email, URL).
- Undergo semi-automated vetting + admin approval.
- Manage job listings and applications via dashboard.
- Access analytics on job postings and applicant engagement.

### Admins
- Approve/reject company registrations.
- Monitor flagged companies and suspicious activity.
- Access system-wide analytics.
- Manage platform integrity.

---

## 3. Core Features
- **Role-Based Access Control (RBAC):** Distinct permissions for students, companies, and admins.
- **Authentication & Authorization:** Secure session management using JWT.
- **Job Search & Filtering:** Filter by category, location, duration, and requirements.
- **Company Vetting Workflow:** Automated checks (email domain, URL validation) + admin approval.
- **Analytics Dashboards:**
    - Students: application history, success rates.
    - Companies: applicant statistics, job performance.
    - Admins: system-wide metrics.

---

## 4. Technology Stack
| Layer              | Technology |
|--------------------|------------|
| Backend            | Jakarta EE (Servlets, CDI, JSP, JPA) |
| Frontend           | JSP + CSS |
| Database           | MySQL |
| Authentication     | JWT |
| Deployment         | GlassFish  |
| Security           | RBAC, bcrypt password storage |


---

## 5. System Architecture (Logic Flow)
### Registration & Vetting
- **Students:** Register → immediate access after email verification.
- **Companies:** Register → semi-automated vetting → admin approval required.

### Dashboards
- **Students:** CV upload, job search, applications, reviews.
- **Companies:** Job posting, applicant management, analytics.
- **Admins:** User/company management, flagged reports, system analytics.

### Security
- JWT tokens issued at login.
- Role-based access enforced at controller/service layer.
- Sensitive data (passwords, CVs) stored securely.

---

## 6. Expected Outcomes
- A trusted platform for youth employment and internships.
- Reduced risk of fraudulent job postings through company vetting.
- Transparent oversight with admin dashboards and analytics.
- Empowered students with accessible opportunities and progress tracking.

---

## 7. Next Steps
- Define **database schema** (users, companies, jobs, applications, reviews).
- Create **wireframes** for student, company, and admin dashboards.
- Draft **API endpoints** for authentication, job posting, application management.
- Plan **deployment strategy** (GlassFish/Payara server setup).

---

👉 This document is your **foundation**. As development begins, you can expand it into a full **Software Requirements Specification (SRS)** with detailed use cases, ER diagrams, and API contracts.

Would you like me to **extend this into a full SRS template** (with sections like functional requirements, non-functional requirements, and use case diagrams) so your team can use it directly for onboarding and planning?
