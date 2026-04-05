# JobFinder v1.0 - Comprehensive System Documentation

## Table of Contents

1. [Introduction](#1-introduction)
2. [System Architecture](#2-system-architecture)
3. [All Servlets and Endpoints](#3-all-servlets-and-endpoints)
4. [Security & Authentication](#4-security--authentication)
5. [Database Layer](#5-database-layer)
6. [Business Logic Layer](#6-business-logic-layer)
7. [Presentation Layer](#7-presentation-layer)
8. [Email System](#8-email-system)
9. [Logging & Monitoring](#9-logging--monitoring)
10. [Deployment Guide](#10-deployment-guide)

---

## 1. Introduction

### 1.1 Purpose
This document provides comprehensive technical documentation for the **JobFinder v1.0** web application. It covers all aspects of the system architecture, implementation details, and operational procedures.

### 1.2 Scope
JobFinder is a Jakarta EE-based web application that connects students/applicants with companies offering jobs and internships. The system features:
- Multi-role user management (Students, Companies, Admins)
- Job posting and application workflow
- Email notification system
- Company vetting and approval process
- Dashboard analytics

### 1.3 Target Audience
- Developers maintaining or extending the system
- System administrators deploying the application
- Technical stakeholders reviewing the architecture

---

## 2. System Architecture

### 2.1 Overview
JobFinder follows a layered architecture based on Jakarta EE standards:

```
┌─────────────────────────────────────────┐
│           Presentation Layer            │
│  (JSP Views, CSS, JavaScript, Servlets) │
├─────────────────────────────────────────┤
│           Business Logic Layer          │
│  (Services: AuthService, JobService,    │
│   EmailService, CompanyService, etc.)   │
├─────────────────────────────────────────┤
│           Data Access Layer             │
│  (DAOs: UserDAO, JobDAO, etc.)          │
├─────────────────────────────────────────┤
│           Database Layer                │
│  (MySQL with JPA/Hibernate)             │
└─────────────────────────────────────────┘
```

### 2.2 Technology Stack Details

| Component | Technology | Version |
|-----------|------------|---------|
| Platform | Jakarta EE | 10 |
| Web Container | GlassFish | 8 |
| Build Tool | Maven | 3.9+ |
| ORM | Hibernate (JPA) | 6.x |
| Database | MySQL | 8.0+ |
| Mail API | Jakarta Mail | 2.1 |
| CDI | Weld (Jakarta CDI) | 4.x |

### 2.3 Package Structure

```
com.jakartaee.jobfinder/
├── entity/          # JPA Entities
│   ├── User.java
│   ├── Company.java
│   ├── Job.java
│   ├── Application.java
│   ├── Review.java
│   ├── role/        # Role enum
│   └── status/      # Status enum
├── dao/             # Data Access Objects
│   ├── UserDAO.java
│   ├── CompanyDAO.java
│   ├── JobDAO.java
│   ├── ApplicationDAO.java
│   └── ReviewDAO.java
├── services/        # Business Logic Services
│   ├── AuthService.java
│   ├── EmailService.java
│   ├── JobService.java
│   ├── CompanyService.java
│   ├── ApplicationService.java
│   └── UserService.java
├── servlet/         # Web Controllers
│   ├── applicant/   # Applicant servlets
│   ├── company/     # Company admin servlets
│   ├── admin/       # System admin servlets
│   ├── dashboard/   # Dashboard servlets
│   └── *.java       # General servlets
├── dto/             # Data Transfer Objects
├── logging/         # Logging utilities
└── security/        # Security utilities
```

---

## 3. All Servlets and Endpoints

### 3.1 Public Endpoints (No Authentication Required)

| Endpoint | Servlet | Methods | Description | JSP View |
|----------|---------|---------|-------------|----------|
| `/home` | HomeServlet | GET | Home/landing page with hero section | index.jsp |
| `/login` | LoginServlet | GET, POST | User authentication page and form submission | login.jsp |
| `/logout` | LogoutServlet | POST | User logout, invalidates session, redirects to login | - |
| `/signout` | LogoutServlet | POST | Alias for /logout | - |
| `/jobs` | JobsServlet | GET | Job listings with filtering (all, remote, saved) | jobs.jsp |
| `/job/*` | JobDetailServlet | GET | Job details page (e.g., /job/123) with apply/save buttons | job-detail.jsp |
| `/companies` | CompaniesServlet | GET | Company listings page | companies.jsp |
| `/reviews` | ReviewsServlet | GET | Company reviews page | reviews.jsp |
| `/internships` | InternshipsServlet | GET | Internship listings page | internships.jsp |
| `/signup-options` | RegistrationOptionsServlet | GET | Registration type selection page | signup-options.jsp |
| `/user-signup` | UserRegistrationServlet | GET, POST | Applicant registration form and submission | user-signup.jsp |
| `/company-register` | CompanyRegistrationServlet | GET, POST | Company registration form and submission | company-register.jsp |
| `/verify-email` | EmailVerificationServlet | GET | Email verification with token parameter | verification-result.jsp |
| `/resend-verification` | ResendVerificationServlet | POST | Resend verification email | - |
| `/password-reset-request` | PasswordResetServlet | GET, POST | Password reset request form | password-reset-request.jsp |
| `/password-reset` | PasswordResetServlet | GET, POST | Password reset with token | password-reset.jsp |

### 3.2 Applicant Endpoints (Requires APPLICANT Role)

| Endpoint | Servlet | Methods | Description | JSP View |
|----------|---------|---------|-------------|----------|
| `/applicant/dashboard` | ApplicantDashboardServlet | GET | Applicant dashboard with stats and recommendations | applicant/user-dashboard.jsp |
| `/applicant/profile` | ApplicantProfileServlet | GET, POST | View and edit applicant profile | applicant/profile.jsp |
| `/applicant/cv` | ApplicantCVServlet | GET, POST | CV upload and management | applicant/cv.jsp |
| `/applicant/applications` | ApplicantApplicationsServlet | GET | View all submitted applications | applicant/applications.jsp |
| `/applicant/apply` | ApplyJobServlet | GET, POST | Job application form and submission | applicant/apply-job.jsp |
| `/applicant/save-job` | SaveJobServlet | POST | Save a job to favorites (JSON response) | - |
| `/applicant/unsave-job` | SaveJobServlet | POST | Remove job from favorites (JSON response) | - |

### 3.3 Company Endpoints (Requires COMPANY_ADMIN Role)

| Endpoint | Servlet | Methods | Description | JSP View |
|----------|---------|---------|-------------|----------|
| `/company/dashboard` | CompanyDashboardServlet | GET | Company dashboard with analytics | company/company-dashboard.jsp |
| `/company/profile` | CompanyProfileServlet | GET, POST | View and edit company profile | company/profile.jsp |
| `/company/listings` | CompanyListingsServlet | GET, POST | Manage job listings (includes delete) | company/listings.jsp |
| `/company/list-job` | CompanyListJobServlet | GET, POST | Create new job posting | company/list-job.jsp |
| `/company/edit-job` | CompanyEditJobServlet | GET, POST | Edit existing job | company/edit-job.jsp |
| `/company/applicants` | CompanyApplicantsServlet | GET, POST | View and manage applicants | company/applicants.jsp |
| `/company/analytics` | CompanyAnalyticsServlet | GET | Company analytics dashboard | company/analytics.jsp |
| `/company/reviews` | CompanyReviewsServlet | GET | Manage company reviews | company/reviews.jsp |

### 3.4 Admin Endpoints (Requires SYSTEM_ADMIN Role)

| Endpoint | Servlet | Methods | Description | JSP View |
|----------|---------|---------|-------------|----------|
| `/admin/dashboard` | AdminDashboardServlet | GET | Admin dashboard with system stats | admin/admin-dashboard.jsp |
| `/admin/users` | AdminUsersServlet | GET, POST | User management (view, delete, change role) | admin/users.jsp |
| `/admin/companies` | AdminCompaniesServlet | GET, POST | Company management and approval queue | admin/companies.jsp |
| `/admin/companies/approve` | AdminCompanyApprovalServlet | POST | Approve or reject company registration | - |
| `/admin/jobs` | AdminJobsServlet | GET, POST | Job management (view all, delete) | admin/jobs.jsp |
| `/admin/reports` | AdminReportsServlet | GET | System reports | admin/reports.jsp |
| `/admin/settings` | AdminSettingsServlet | GET, POST | System settings | admin/settings.jsp |
| `/admin/pending-companies` | AdminCompaniesServlet | GET | Pending company approval queue | admin/pending-companies.jsp |

### 3.5 CV Endpoints (Authenticated)

| Endpoint | Servlet | Methods | Description |
|----------|---------|---------|-------------|
| `/cv/download` | CVDownloadServlet | GET | Download CV file (sets content-disposition) |
| `/cv/view` | CVViewServlet | GET | View CV file inline in browser |

### 3.6 Complete Servlet Package Structure

```
com.jakartaee.jobfinder.servlet/
├── AdminCompanyApprovalServlet.java    # POST /admin/companies/approve
├── ApplicantApplicationsServlet.java   # GET /applicant/applications
├── ApplicantCVServlet.java            # GET, POST /applicant/cv
├── ApplicantProfileServlet.java       # GET, POST /applicant/profile
├── CVDownloadServlet.java              # GET /cv/download
├── CVViewServlet.java                  # GET /cv/view
├── CompaniesServlet.java              # GET /companies
├── CompanyRegistrationServlet.java    # GET, POST /company-register
├── EmailVerificationServlet.java      # GET /verify-email
├── HomeServlet.java                    # GET /home
├── InternshipsServlet.java             # GET /internships
├── JobDetailServlet.java               # GET /job/*
├── JobsServlet.java                    # GET /jobs
├── LoginServlet.java                   # GET, POST /login
├── LogoutServlet.java                  # POST /logout, /signout
├── PasswordResetServlet.java          # GET, POST /password-reset-request, /password-reset
├── RegistrationOptionsServlet.java    # GET /signup-options
├── ResendVerificationServlet.java     # POST /resend-verification
├── ReviewsServlet.java                 # GET /reviews
├── UserRegistrationServlet.java       # GET, POST /user-signup
├── admin/
│   ├── AdminCompaniesServlet.java     # GET, POST /admin/companies
│   ├── AdminCompanyApprovalServlet.java # POST /admin/companies/approve
│   ├── AdminJobsServlet.java          # GET, POST /admin/jobs
│   ├── AdminReportsServlet.java       # GET /admin/reports
│   ├── AdminSettingsServlet.java      # GET, POST /admin/settings
│   └── AdminUsersServlet.java         # GET, POST /admin/users
├── applicant/
│   ├── ApplyJobServlet.java          # GET, POST /applicant/apply
│   └── SaveJobServlet.java           # POST /applicant/save-job, /applicant/unsave-job
├── company/
│   ├── CompanyAnalyticsServlet.java   # GET /company/analytics
│   ├── CompanyApplicantsServlet.java  # GET, POST /company/applicants
│   ├── CompanyEditJobServlet.java     # GET, POST /company/edit-job
│   ├── CompanyListingsServlet.java    # GET, POST /company/listings
│   ├── CompanyListJobServlet.java     # GET, POST /company/list-job
│   ├── CompanyProfileServlet.java     # GET, POST /company/profile
│   └── CompanyReviewsServlet.java     # GET /company/reviews
└── dashboard/
    ├── AdminDashboardServlet.java     # GET /admin/dashboard
    ├── ApplicantDashboardServlet.java # GET /applicant/dashboard
    └── CompanyDashboardServlet.java   # GET /company/dashboard
```

---

## 4. Security & Authentication

### 4.1 Authentication Mechanism

**Form-Based Authentication with Jakarta Security**
- Users authenticate via email and password
- Passwords stored using bcrypt hashing
- Session-based authentication with HTTP sessions
- `@RolesAllowed` annotations for method-level security

### 4.2 Authorization Model

**Role-Based Access Control (RBAC)**

| Role | Permissions |
|------|-------------|
| `APPLICANT` | View jobs, apply, manage CV, view applications |
| `COMPANY_ADMIN` | Post jobs, view applicants, manage company profile |
| `SYSTEM_ADMIN` | Manage users, approve companies, system oversight |

### 4.3 Security Implementation

**Container Security (web.xml)**
```xml
<security-constraint>
    <web-resource-collection>
        <web-resource-name>Applicant Resources</web-resource-name>
        <url-pattern>/applicant/*</url-pattern>
    </web-resource-collection>
    <auth-constraint>
        <role-name>APPLICANT</role-name>
    </auth-constraint>
</security-constraint>
```

**Programmatic Security (Servlets)**
- `HttpServletRequest.isUserInRole(String role)` - Check user role
- `HttpServletRequest.getUserPrincipal()` - Get authenticated user
- `HttpServletRequest.getRemoteUser()` - Get user name

**Security Annotations**
- `@RolesAllowed({"APPLICANT", "COMPANY_ADMIN"})` - Restrict method access
- `@DenyAll` - Deny all access
- `@PermitAll` - Allow all access

### 4.4 Password Security

**Hashing Algorithm: BCrypt**
- Work factor: 10 (configurable)
- Automatic salt generation
- One-way hashing (no password retrieval)

**Password Requirements**
- Minimum 8 characters
- Enforced in `ApplicantProfileServlet` password change form

### 4.5 Email Verification

**Flow:**
1. User registers → verification token generated
2. Token stored in `User.verificationToken` field
3. Email sent with verification link
4. User clicks link → token validated → account activated

**Token Generation:**
- UUID-based tokens
- 24-hour expiration (configurable)

---

## 4. Session Management

### 4.1 Session Configuration

**Session Timeout:** 30 minutes (default)

**Session Attributes:**
| Attribute | Type | Purpose |
|-----------|------|---------|
| `userId` | String | Logged-in user identifier (email) |
| `userName` | String | Display name |
| `userRole` | String | User role for UI decisions |

### 4.2 Session Lifecycle

**Creation:**
- Created on successful authentication
- `AuthService.authenticate()` returns user data
- Stored via `req.getSession().setAttribute()`

**Invalidation:**
```java
// LogoutServlet
req.getSession().invalidate();
req.logout();
```

### 4.3 Session Security

- Secure flag set in production (HTTPS)
- HttpOnly flag prevents XSS attacks
- Session ID regeneration after login

---

## 5. Database Layer

### 5.1 Database Schema

**Entity Relationship Diagram (Conceptual)**

```
User ||--o{ Application : submits
User ||--o{ Review : writes
User ||--|| Company : administers
Company ||--o{ Job : posts
Job ||--o{ Application : receives
Company ||--o{ Review : receives
```

### 5.2 Entity Definitions

**User Entity**
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;              // UUID
    private String email;
    private String passwordHash;    // BCrypt
    private String name;
    private Role role;              // APPLICANT, COMPANY_ADMIN, SYSTEM_ADMIN
    private boolean emailVerified;
    private String verificationToken;
    private String phone;
    private String address;
    private LocalDateTime created_at;
    
    // Relationships
    @OneToMany(mappedBy = "applicant")
    private List<Application> applications;
    
    @OneToOne(mappedBy = "companyAdmin")
    private Company company;
}
```

**Company Entity**
```java
@Entity
@Table(name = "companies")
public class Company {
    @Id
    private String id;
    private String name;
    private String registrationNumber;
    private String website;
    private String description;
    private Status status;          // PENDING, APPROVED, REJECTED
    
    @OneToOne
    @JoinColumn(name = "admin_id")
    private User companyAdmin;
    
    @OneToMany(mappedBy = "company")
    private List<Job> jobs;
}
```

**Job Entity**
```java
@Entity
@Table(name = "jobs")
public class Job {
    @Id
    private String id;
    private String jobTitle;
    private String jobDescription;
    private String jobRequirements;
    private String physicalAddress;
    private String salaryRange;
    private String jobType;
    private LocalDateTime created_at;
    
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    
    @OneToMany(mappedBy = "job")
    private List<Application> applications;
}
```

**Application Entity**
```java
@Entity
@Table(name = "applications")
public class Application {
    @Id
    private String id;
    private LocalDateTime appliedDate;
    private Status status;          // PENDING, APPROVED, REJECTED
    
    @ManyToOne
    @JoinColumn(name = "applicant_id")
    private User applicant;
    
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
}
```

### 5.3 Database Operations (DAO Layer)

**CRUD Operations Pattern**
```java
@Stateless
public class UserDAO {
    @PersistenceContext
    private EntityManager em;
    
    // Create
    @Transactional
    public void create(User user) {
        em.persist(user);
    }
    
    // Read
    public User findById(String id) {
        return em.find(User.class, id);
    }
    
    // Update
    @Transactional
    public void update(User user) {
        em.merge(user);
    }
    
    // Delete
    @Transactional
    public void delete(String id) {
        User user = findById(id);
        if (user != null) {
            em.remove(user);
        }
    }
}
```

**Custom Queries**
```java
// Find by email
public Optional<User> findByEmail(String email) {
    TypedQuery<User> query = em.createQuery(
        "SELECT u FROM User u WHERE u.email = :email", User.class);
    query.setParameter("email", email);
    return query.getResultStream().findFirst();
}

// Find applications by job
public List<Application> findByJob(Job job) {
    return em.createQuery(
        "SELECT a FROM Application a WHERE a.job = :job", Application.class)
        .setParameter("job", job)
        .getResultList();
}
```

### 5.4 Transaction Management

**Declarative Transactions**
- `@Transactional` annotation on DAO methods
- Container-managed transactions
- Automatic rollback on unchecked exceptions

---

## 6. Business Logic Layer

### 6.1 Service Architecture

Services are implemented as CDI beans with `@ApplicationScoped`:

```java
@ApplicationScoped
public class JobService {
    @Inject
    private JobDAO jobDAO;
    
    @Inject
    private CompanyDAO companyDAO;
    
    // Business methods...
}
```

### 6.2 Key Services

**AuthService**
- `authenticate(email, password)` - User login
- `generateToken(user)` - Create auth token
- `extractRole(principal)` - Get role from principal

**EmailService**
- `sendVerificationEmail()` - Account verification
- `sendPasswordResetEmail()` - Password reset
- `sendApplicationSubmittedEmail()` - Application confirmation
- `sendNewApplicationNotificationEmail()` - Company notification

**JobService**
- `createJob()` - Post new job
- `getRecentJobsForCompany()` - Dashboard data
- `hasUserApplied()` - Check application status

**CompanyService**
- `getCompanyDashboardStats()` - Analytics for dashboard
- `getRecentActivitiesForCompany()` - Recent activity feed

### 6.3 Business Rules

**Application Submission Rules:**
1. User must be authenticated and have APPLICANT role
2. Email must be verified
3. Cannot apply to same job twice
4. Job must exist and be active

**Company Registration Rules:**
1. Unique email and registration number
2. Valid email domain (checked against blocklist)
3. Website URL validation
4. Admin approval required for activation

---

## 7. Presentation Layer

### 7.1 Servlet Architecture

**Base Pattern:**
```java
@WebServlet("/applicant/profile")
@RolesAllowed("APPLICANT")
public class ApplicantProfileServlet extends HttpServlet {
    
    private static final String SERVLET_NAME = "ApplicantProfileServlet";
    
    @Inject
    private UserDAO userDAO;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Authentication check
        String userId = getCurrentUserId(req);
        if (userId == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // Load data
        User user = userDAO.findById(userId);
        req.setAttribute("user", user);
        
        // Forward to view
        req.getRequestDispatcher("/views/applicant/profile.jsp")
           .forward(req, resp);
    }
}
```

### 7.2 View Layer (JSP)

**Location:** `src/main/webapp/WEB-INF/views/`

**Structure:**
```
views/
├── components/         # Reusable components
│   ├── signed-in-navbar.jsp
│   ├── company-navbar.jsp
│   ├── system-navbar.jsp
│   └── footer.jsp
├── applicant/          # Applicant views
│   ├── user-dashboard.jsp
│   ├── profile.jsp
│   ├── applications.jsp
│   └── apply-job.jsp
├── company/            # Company views
│   ├── company-dashboard.jsp
│   ├── company-profile.jsp
│   ├── listings.jsp
│   └── applicants.jsp
├── admin/              # Admin views
│   ├── admin-dashboard.jsp
│   ├── users.jsp
│   ├── companies.jsp
│   └── jobs.jsp
└── *.jsp               # General views
    ├── index.jsp
    ├── login.jsp
    ├── register.jsp
    └── error.jsp
```

### 7.3 CSS Architecture

**Files:**
- `index.css` - Main stylesheet with modern design
- `responsive.css` - Mobile-first responsive design
- **Framework:** Custom CSS with CSS Grid & Flexbox
- **Icons:** Font Awesome

---

## 8. Email System

### 8.1 Configuration

**SMTP Settings:**
```java
private static final String SMTP_HOST = "smtp.gmail.com";
private static final int SMTP_PORT = 587;
private static final String SMTP_USERNAME = System.getenv("SMTP_USERNAME");
private static final String SMTP_PASSWORD = System.getenv("SMTP_PASSWORD");
```

### 8.2 Email Types

| Email Type | Trigger | Recipients |
|------------|---------|------------|
| Verification | User registration | New user |
| Password Reset | User request | Requesting user |
| Application Submitted | Job application | Applicant |
| New Application | Job application | Company admin |
| Company Approved | Admin approval | Company admin |
| Company Rejected | Admin rejection | Company admin |

### 8.3 Email Templates

HTML templates with inline CSS for email client compatibility:
- Professional gradient headers
- Responsive design
- Call-to-action buttons
- Company branding

---

## 9. Logging & Monitoring

### 9.1 MainLogger Utility

**Centralized logging with categories:**
```java
// Authentication events
MainLogger.logLoginAttempt(email, clientIP, success);
MainLogger.logLogout(userId, email, clientIP);

// Security events
MainLogger.logSecurityEvent(component, event, details);
MainLogger.logAuthenticationError(component, operation, error, email);

// User actions
MainLogger.logUserAction(component, userId, action);

// Service operations
MainLogger.logServiceOperation(service, operation, success, details);

// General logging
MainLogger.logInfo(component, message);
MainLogger.logError(component, message);
MainLogger.logError(component, message, throwable);
```

### 9.2 Log Format

```
[2024-01-15 14:30:45] [AUTH] Login attempt: user@example.com from 192.168.1.1 - SUCCESS
[2024-01-15 14:31:12] [SECURITY] Role check: AdminCompaniesServlet - User: admin@example.com, Role: SYSTEM_ADMIN
[2024-01-15 14:32:08] [USER_ACTION] ApplyJobServlet: applicant@example.com - APPLY_JOB: job-123
[2024-01-15 14:32:09] [SERVICE] EmailService: sendApplicationSubmittedEmail - SUCCESS
```

---

## 10. API Documentation

### 10.1 OpenAPI Specification

**Location:** `src/main/resources/static/openapi.yaml`

Includes:
- Authentication endpoints
- Job management endpoints
- Application endpoints
- Company management endpoints
- Admin endpoints

### 10.2 Key Endpoints

| Endpoint | Method | Description | Roles |
|----------|--------|-------------|-------|
| `/login` | POST | User authentication | All |
| `/logout` | POST | User logout | All |
| `/applicant/jobs` | GET | List available jobs | APPLICANT |
| `/applicant/apply` | POST | Submit job application | APPLICANT |
| `/company/list-job` | POST | Create new job posting | COMPANY_ADMIN |
| `/company/applicants` | GET | View job applicants | COMPANY_ADMIN |
| `/admin/users` | GET/POST | User management | SYSTEM_ADMIN |
| `/admin/companies` | GET/POST | Company management | SYSTEM_ADMIN |

---

## 11. Deployment Guide

### 11.1 Prerequisites

- Java 17 or higher
- Maven 3.9+
- MySQL 8.0+
- GlassFish 8

### 11.2 Environment Variables

| Variable | Required | Description |
|----------|----------|-------------|
| `SMTP_USERNAME` | Yes | Email service username |
| `SMTP_PASSWORD` | Yes | Email service password |
| `DATABASE_URL` | Optional | JDBC connection URL |
| `DATABASE_USER` | Optional | Database username |
| `DATABASE_PASSWORD` | Optional | Database password |

### 11.3 Database Setup

1. Create MySQL database:
```sql
CREATE DATABASE jobfinder CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Configure `persistence.xml`:
```xml
<persistence-unit name="youth-job-finder">
    <jta-data-source>jdbc/youth_job_finder</jta-data-source>
    <properties>
        <property name="hibernate.dialect" value="org.hibernate.dialect.MySQLDialect"/>
        <property name="hibernate.hbm2ddl.auto" value="update"/>
    </properties>
</persistence-unit>
```

### 11.4 GlassFish Configuration

1. Create JDBC Connection Pool:
```bash
asadmin create-jdbc-connection-pool \
  --datasourceclassname com.mysql.cj.jdbc.MysqlDataSource \
  --restype javax.sql.DataSource \
  --property user=root:password=secret:url=jdbc:mysql://localhost:3306/jobfinder \
  jobfinder-pool
```

2. Create JDBC Resource:
```bash
asadmin create-jdbc-resource --connectionpoolid jobfinder-pool jdbc/youth_job_finder
```

### 11.5 Build & Deploy

```bash
# Build WAR file
mvn clean package

# Deploy to GlassFish
asadmin deploy target/jobfinder.war

# Or use deploy.ps1 script
.\deploy.ps1
```

### 11.6 Production Checklist

- [ ] Environment variables configured
- [ ] Database connection pool optimized
- [ ] SSL/TLS enabled
- [ ] Session timeout configured
- [ ] Logging level set to INFO or WARN
- [ ] Error pages configured
- [ ] Email service tested
- [ ] Backup strategy implemented

---

## Appendix A: Troubleshooting

### Common Issues

**Issue:** `ClassNotFoundException: com.mysql.cj.jdbc.Driver`
**Solution:** Add MySQL connector JAR to GlassFish lib folder

**Issue:** `PersistenceException: Unable to build EntityManagerFactory`
**Solution:** Check database connection and credentials in persistence.xml

**Issue:** Email not sending
**Solution:** Verify SMTP credentials and ensure less secure apps enabled (for Gmail)

---

## Appendix B: Glossary

| Term | Definition |
|------|------------|
| BCrypt | Password hashing algorithm |
| CDI | Contexts and Dependency Injection |
| DAO | Data Access Object |
| JPA | Java Persistence API |
| JSP | JavaServer Pages |
| ORM | Object-Relational Mapping |
| RBAC | Role-Based Access Control |
| SMTP | Simple Mail Transfer Protocol |
| WAR | Web Application Archive |

---

*Document Version: 1.0*
*Last Updated: April 2026*
*System Version: JobFinder v1.0*
