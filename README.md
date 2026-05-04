# Youth Job & Internship Finder Web Application

## 1. Project Overview

The **Youth Job & Internship Finder** is a modern, responsive web platform designed to connect students with legitimate companies offering jobs and internships. The system emphasizes **trust, transparency, and accessibility** through company vetting workflows, student reviews, and admin oversight, now featuring a **sleek, modern UI design**.

### Objectives
- Provide students with a secure, user-friendly portal to discover opportunities, manage applications, and track progress.
- Enable companies to post verified job listings, manage applicants, and gain insights through analytics.
- Empower administrators to oversee the ecosystem, ensuring legitimacy and fairness via semi-automated vetting and approval workflows.

---

## 2. User Roles & Permissions

### Students
- Register with a valid student/personal email.
- Upload and update CVs with modern file management.
- Apply for jobs/internships and track application status with real-time updates.
- Review companies and flag suspicious behavior.
- Personalized dashboard with AI-powered recommendations.

### Companies
- Register with company details (registration number, email, URL).
- Undergo semi-automated vetting + admin approval for platform trust.
- Manage job listings and applications via intuitive dashboard.
- Access comprehensive analytics on job postings and applicant engagement.

### Admins
- Approve/reject company registrations with workflow management.
- Monitor flagged companies and suspicious activity with advanced reporting.
- Access system-wide analytics and platform health metrics.
- Manage platform integrity and user governance.

---

## 3. Core Features

- **Role-Based Access Control (RBAC):** Distinct permissions for students, companies, and admins.
- **Modern Authentication & Authorization:** Jakarta Security with database identity store and bcrypt password hashing.
- **Advanced Job Search & Filtering:** Filter by category, location, duration, and requirements with real-time search.
- **Company Vetting Workflow:** Automated checks (email domain, URL validation) + admin approval.
- **Analytics Dashboards:**
    - Students: application history, success rates, personalized insights.
    - Companies: applicant statistics, job performance metrics.
    - Admins: system-wide metrics and user engagement analytics.
- **Responsive Modern UI:** Mobile-first design with gradient backgrounds, card layouts, and smooth animations.

---

## 4. Technology Stack

| Layer              | Technology |
|--------------------|------------|
| Backend            | Jakarta EE 10 (Servlets, CDI, JSP, JPA) |
| Frontend           | Modern JSP + CSS3 + Font Awesome Icons |
| Database           | MySQL with JPA/Hibernate ORM |
| Deployment         | GlassFish 8 (see `deploy.ps1` for Windows deployment) |
| UI Framework        | Custom responsive design with CSS Grid & Flexbox |

---

## 5. Project Structure (this repository)

Maven standard layout with base package **`com.jakartaee.jobfinder`**.

```
src/main/java/com/jakartaee/jobfinder/
├── HomeServlet.java          # Welcome page with modern hero section
├── DatabaseTrigger.java      # DB lifecycle hook
├── models/                   # JPA entities (User, Company, Job, Application, Review, …)
│   ├── role/                 # Role enum (APPLICANT, SYSTEM_ADMIN, COMPANY_ADMIN)
│   └── status/               # Status enum (PENDING, APPROVED, REJECTED)
├── dao/                      # Data access layer with JPA repositories
├── dto/                      # Data transfer objects for API responses
├── services/                 # Business logic layer (AuthService, EmailService, etc.)
├── security/                 # Security configuration and utilities
│   ├── config/               # Security configuration
│   ├── session/              # Session management
│   └── utils/                # Password hashing utilities
└── servlet/                  # @WebServlet controllers (login, jobs, companies, …)

src/main/webapp/
├── WEB-INF/
│   ├── web.xml
│   └── views/                # JSP views (not directly URL-accessible)
│       ├── components/       # Shared UI components (navbar, footer, …)
│       ├── index.jsp
│       ├── login.jsp
│       └── …
├── css/                      # Modern responsive stylesheets
│   └── index.css             # Main stylesheet with modern design
├── images/                   # Static images and assets
└── js/                       # Client-side scripts (optional; folder reserved)

src/main/resources/
├── META-INF/
│   ├── persistence.xml       # JPA database configuration
│   └── beans.xml             # CDI configuration
├── db/migrations/            # SQL migrations (Flyway/Liquibase-style naming)
└── static/                   # Static resources
```

Servlets forward to **`/WEB-INF/views/...`** so pages live under `WEB-INF` as recommended in Jakarta EE best practices.

---

## 6. All Endpoints & Servlets

### Public Endpoints (No Authentication Required)

| Endpoint | Servlet | Methods | Description |
|----------|---------|---------|-------------|
| `/home` | HomeServlet | GET | Home/landing page |
| `/login` | LoginServlet | GET, POST | User authentication |
| `/logout` | LogoutServlet | POST | User logout (also `/signout`) |
| `/jobs` | JobsServlet | GET | Job listings page |
| `/job/*` | JobDetailServlet | GET | Job details page (e.g., /job/123) |
| `/companies` | CompaniesServlet | GET | Company listings page |
| `/signup-options` | RegistrationOptionsServlet | GET | Registration type selection |
| `/user-signup` | UserRegistrationServlet | GET, POST | Applicant registration |
| `/company-register` | CompanyRegistrationServlet | GET, POST | Company registration |
| `/verify-email` | EmailVerificationServlet | GET | Email verification |
| `/resend-verification` | ResendVerificationServlet | POST | Resend verification email |
| `/password-reset-request` | PasswordResetServlet | GET, POST | Request password reset |
| `/password-reset` | PasswordResetServlet | GET, POST | Reset password with token |

### Applicant Endpoints (Requires APPLICANT Role)

| Endpoint | Servlet | Methods | Description |
|----------|---------|---------|-------------|
| `/applicant/dashboard` | ApplicantDashboardServlet | GET | Applicant dashboard |
| `/applicant/profile` | ApplicantProfileServlet | GET, POST | Manage profile |
| `/applicant/cv` | ApplicantCVServlet | GET, POST | CV upload/management |
| `/applicant/applications` | ApplicantApplicationsServlet | GET | View applications |
| `/applicant/apply` | ApplyJobServlet | GET, POST | Apply for a job |
| `/applicant/save-job` | SaveJobServlet | POST | Save a job |
| `/applicant/unsave-job` | SaveJobServlet | POST | Unsave a job |

### Company Endpoints (Requires COMPANY_ADMIN Role)

| Endpoint | Servlet | Methods | Description |
|----------|---------|---------|-------------|
| `/company/dashboard` | CompanyDashboardServlet | GET | Company dashboard |
| `/company/profile` | CompanyProfileServlet | GET, POST | Company profile management |
| `/company/listings` | CompanyListingsServlet | GET, POST | Manage job listings |
| `/company/list-job` | CompanyListJobServlet | GET, POST | Create new job posting |
| `/company/edit-job` | CompanyEditJobServlet | GET, POST | Edit existing job |
| `/company/applicants` | CompanyApplicantsServlet | GET, POST | View/manage applicants |
| `/company/analytics` | CompanyAnalyticsServlet | GET | Company analytics |

### Admin Endpoints (Requires SYSTEM_ADMIN Role)

| Endpoint | Servlet | Methods | Description |
|----------|---------|---------|-------------|
| `/admin/dashboard` | AdminDashboardServlet | GET | Admin dashboard |
| `/admin/users` | AdminUsersServlet | GET, POST | User management |
| `/admin/companies` | AdminCompaniesServlet | GET, POST | Company management |
| `/admin/companies/approve` | AdminCompanyApprovalServlet | POST | Approve/reject companies |
| `/admin/jobs` | AdminJobsServlet | GET, POST | Job management |
| `/admin/reports` | AdminReportsServlet | GET | System reports |
| `/admin/settings` | AdminSettingsServlet | GET, POST | System settings |

### CV Endpoints

| Endpoint | Servlet | Methods | Description |
|----------|---------|---------|-------------|
| `/cv/download` | CVDownloadServlet | GET | Download CV file |
| `/cv/view` | CVViewServlet | GET | View CV file |

---

## 7. Documentation in this repo

| Document | Purpose |
|----------|---------|
| **[jobfinder-v1.md](jobfinder-v1.md)** | **Comprehensive system documentation** - Detailed technical reference |
| **[README.md](README.md)** | This file - Setup and usage guide |

---

## 8. System Architecture (Logic Flow)

### Registration & Vetting
- **Students:** Register → immediate email verification → dashboard access.
- **Companies:** Register → automated vetting → admin approval → dashboard access.

### Modern User Dashboards
- **Students:** CV management with drag-drop upload, job search with advanced filters, application tracking, personalized recommendations, company reviews.
- **Companies:** Job posting with rich text editor, applicant management with status tracking, analytics dashboard with engagement metrics.
- **Admins:** User/company management with workflow automation, flagged content monitoring, system analytics with real-time reporting.

### Security Implementation
- Jakarta Security with database identity store and form-based authentication.
- Role-based authorization with security annotations.
- Secure password storage with bcrypt hashing.
- Session-based authentication with proper session management.

---

## 9. Modern UI/UX Features

### Design System
- **Modern Color Scheme:** Professional gradients with primary brand colors.
- **Typography:** Clean, readable fonts with proper hierarchy.
- **Component Library:** Reusable UI components (cards, buttons, forms).
- **Responsive Design:** Mobile-first approach with CSS Grid and Flexbox.

### Interactive Elements
- **Hero Section:** Animated backgrounds with floating cards and statistics.
- **Advanced Search:** Real-time filtering with category buttons.
- **Card-Based Layouts:** Job and company cards with hover effects.
- **Micro-interactions:** Smooth transitions and loading states.

### Accessibility
- **WCAG 2.1 Compliance:** Proper color contrast, keyboard navigation.
- **Screen Reader Support:** Semantic HTML5 with ARIA labels.
- **Focus Management:** Clear focus indicators for keyboard users.

---

## 10. Build and Deployment

### Development
```bash
# Clean build
mvn clean package

# Run with GlassFish (development)
mvn glassfish:run
```

### Production Deployment
1. Configure database connection in `src/main/resources/META-INF/persistence.xml`
2. Configure SMTP environment variables (see Section 11)
3. Deploy generated `target/jobfinder.war` to GlassFish 8
4. Use `deploy.ps1` script for automated deployment after path adjustment

---

## 11. SMTP Configuration (Required for Email Features)

> ⚠️ **IMPORTANT:** You must configure your own SMTP server before running the application. Email features (verification, password reset, notifications) will not work without valid SMTP credentials.

### Setup Instructions

1. **Copy the example environment file:**
   ```bash
   cp .env .env
   ```

2. **Edit `.env` with your SMTP server details:**
   ```bash
   # Required SMTP Settings
   SMTP_HOST=smtp.gmail.com          # Your SMTP server host
   SMTP_PORT=587                     # Your SMTP server port
   SMTP_USERNAME=your-email@gmail.com # Your SMTP username/email
   SMTP_PASSWORD=your-app-password   # Your SMTP password or app password

   # Optional: Display name for sent emails
   SMTP_FROM_NAME=JobFinder
   ```

3. **Load environment variables** before starting the application:
   - **Windows (PowerShell):**
     ```powershell
     $env:SMTP_HOST="smtp.gmail.com"
     $env:SMTP_PORT="587"
     $env:SMTP_USERNAME="your-email@gmail.com"
     $env:SMTP_PASSWORD="your-app-password"
     ```
   - **Linux/macOS:**
     ```bash
     export SMTP_HOST=smtp.gmail.com
     export SMTP_PORT=587
     export SMTP_USERNAME=your-email@gmail.com
     export SMTP_PASSWORD=your-app-password
     ```

4. **For GlassFish deployment:** Configure environment variables in GlassFish:

   **Option A: Using GlassFish Admin Console (GUI)**
   1. Open `http://localhost:4848` in browser
   2. Navigate to **Configurations** → **server-config** → **JVM Settings**
   3. Click **JVM Options** tab
   4. Click **Add JVM Option**
   5. Add each SMTP variable (e.g., `-DSMTP_HOST=smtp.gmail.com`)
   6. Click **Save**, then restart GlassFish

   **Option B: Using asadmin CLI:**
   ```bash
   # Navigate to GlassFish bin directory
   cd C:\glassfish8\glassfish\bin

   # Add SMTP configuration
   asadmin create-jvm-options -DSMTP_HOST=smtp.gmail.com
   asadmin create-jvm-options -DSMTP_PORT=587
   asadmin create-jvm-options -DSMTP_USERNAME=your-email@gmail.com
   asadmin create-jvm-options -DSMTP_PASSWORD=your-app-password
   asadmin create-jvm-options -DSMTP_FROM_NAME=JobFinder

   # Restart GlassFish
   asadmin stop-domain
   asadmin start-domain
   ```

   **Option C: Using domain.xml (Advanced)**
   Edit `glassfish/domains/domain1/config/domain.xml` and add to `<java-config>`:
   ```xml
   <jvm-options>-DSMTP_HOST=smtp.gmail.com</jvm-options>
   <jvm-options>-DSMTP_PORT=587</jvm-options>
   <jvm-options>-DSMTP_USERNAME=your-email@gmail.com</jvm-options>
   <jvm-options>-DSMTP_PASSWORD=your-app-password</jvm-options>
   ```

   **Option D: Using asadmin System Properties (Recommended)**
   ```bash
   # Configure SMTP as system properties (persisted across restarts)
   asadmin create-system-properties SMTP_HOST=smtp.gmail.com
   asadmin create-system-properties SMTP_PORT=587
   asadmin create-system-properties SMTP_USERNAME=your-email@gmail.com
   asadmin create-system-properties SMTP_PASSWORD=your-app-password
   asadmin create-system-properties SMTP_FROM_NAME=JobFinder

   # Restart GlassFish to apply changes
   asadmin restart-domain domain1
   ```

5. **Configuration Priority:** The application checks for SMTP settings in this order:
   1. GlassFish JVM/System Properties (`System.getProperty()`)
   2. System Environment Variables (`System.getenv()`)
   3. `.env` file (development fallback)

6. **Verify configuration:** The application will fail to start with a clear error message if required SMTP variables are missing.

### SMTP Provider Examples

| Provider | SMTP_HOST | SMTP_PORT | Notes |
|----------|-----------|-----------|-------|
| Gmail | smtp.gmail.com | 587 | Use App Password (16 chars, no spaces/hyphens) |
| Outlook | smtp.office365.com | 587 | Use App Password |
| SendGrid | smtp.sendgrid.net | 587 | Use API key as password |
| Mailgun | smtp.mailgun.org | 587 | Use SMTP credentials |

---

## 12. Expected Outcomes

- A trusted, modern platform for youth employment and internships.
- Reduced risk of fraudulent job postings through company vetting.
- Transparent oversight with admin dashboards and analytics.
- Empowered students with accessible opportunities and AI-powered recommendations.
- Enhanced user experience through responsive, modern design.
- Scalable architecture supporting thousands of concurrent users.

---

## 13. Recent Enhancements (v2.0)

### UI/UX Modernization
- **Complete redesign** with modern gradient backgrounds and card-based layouts
- **Mobile-first responsive design** with hamburger menu and sidebar navigation
- **Enhanced hero section** with animated particles and floating elements
- **Advanced search and filtering** with real-time capabilities
- **Professional typography** and spacing system
- **Smooth animations** and micro-interactions throughout

### Technical Improvements
- **Jakarta Security integration** with database identity store and form authentication
- **Enhanced security** with proper error handling and validation
- **Optimized database layer** with JPA entities and relationships
- **Responsive CSS Grid** layouts for all screen sizes
- **Font Awesome integration** for consistent iconography

### Feature Additions
- **Company verification badges** and status indicators
- **Job statistics and analytics** for data-driven decisions
- **Tag-based categorization** for better content organization
- **Loading states and error handling** for better UX
- **Accessibility improvements** with WCAG 2.1 compliance

---
