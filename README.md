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
- **Modern Authentication & Authorization:** JWT-based security with bcrypt password hashing.
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
| API docs           | OpenAPI 3.0 (`src/main/resources/static/openapi.yaml`) |
| Deployment         | GlassFish 8 (see `deploy.ps1` for example) |
| Build              | Maven (`war` artifact `jobfinder.war`) |
| UI Framework        | Custom responsive design with CSS Grid & Flexbox |

---

## 5. Project Structure (this repository)

Maven standard layout with base package **`com.jakartaee.jobfinder`**.

```
src/main/java/com/jakartaee/jobfinder/
├── HomeServlet.java          # Welcome page with modern hero section
├── DatabaseTrigger.java      # DB lifecycle hook
├── entity/                   # JPA entities (User, Company, Job, Application, Review, …)
│   ├── role/                 # Role enum (APPLICANT, SYSTEM_ADMIN, COMPANY_ADMIN)
│   └── status/               # Status enum (PENDING, APPROVED, REJECTED)
├── dao/                      # Data access layer with JPA repositories
├── dto/                      # Data transfer objects for API responses
├── service/                  # Business logic layer (AuthService, application services)
├── security/                 # JWT authentication and authorization
│   ├── utils/                # JWT utilities and password hashing
│   └── JwtAuthenticationMechanism (disabled - using filter-based auth)
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
│   ├── index.css          # Main stylesheet with modern design
│   └── responsive.css    # Mobile-first responsive design
├── images/                   # Static images and assets
└── js/                       # Client-side scripts (optional; folder reserved)

src/main/resources/
├── META-INF/
│   ├── persistence.xml       # JPA database configuration
│   └── beans.xml           # CDI configuration
├── db/migrations/            # SQL migrations (Flyway/Liquibase-style naming)
└── static/                   # OpenAPI, Swagger HTML documentation
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
| `/reviews` | ReviewsServlet | GET | Company reviews page |
| `/internships` | InternshipsServlet | GET | Internship listings |
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
| `/company/reviews` | CompanyReviewsServlet | GET | Company reviews management |

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
| [Git-Workflow-Guide.md](docs/Git-Workflow-Guide.md) | Branches, `develop` vs `main`, PR workflow |
| [Project Contribution.md](Project%20Contribution.md) | Contributing and Git Flow |
| [DAO & DTO Team.md](DAO%20&%20DTO%20Team.md) | Handbook: DAO/DTO, entities, UI layer |
| [DataBase-Dev-Team.md](docs/DataBase-Dev-Team.md) | Database team, `resources/db` layout |
| [Business-Logic team.md](Business-Logic%20team.md) | Services layer (CDI beans) |
| [UI & Presentation Team.md](UI%20&%20Presentation%20Team.md) | Modern JSP/CSS design system |
| [Security Guide.md](Security%20Guide.md) | Security practices and JWT implementation |
| [MySQL Setup in GlassFish Guide.md](MySQL%20Setup%20in%20GlassFish%20Guide.md) | Datasource + persistence |
| [SRS.md](SRS.md) | Software requirements |

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
- JWT-based stateless authentication with refresh tokens.
- Role-based authorization with method-level security.
- Secure password storage with bcrypt hashing.
- Session management with automatic cleanup.

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
2. Set JWT_SECRET environment variable for token security
3. Deploy generated `target/jobfinder.war` to GlassFish 8
4. Use `deploy.ps1` script for automated deployment after path adjustment

---

## 11. Expected Outcomes

- A trusted, modern platform for youth employment and internships.
- Reduced risk of fraudulent job postings through company vetting.
- Transparent oversight with admin dashboards and analytics.
- Empowered students with accessible opportunities and AI-powered recommendations.
- Enhanced user experience through responsive, modern design.
- Scalable architecture supporting thousands of concurrent users.

---

## 12. Recent Enhancements (v2.0)

### UI/UX Modernization
- **Complete redesign** with modern gradient backgrounds and card-based layouts
- **Mobile-first responsive design** with hamburger menu and sidebar navigation
- **Enhanced hero section** with animated particles and floating elements
- **Advanced search and filtering** with real-time capabilities
- **Professional typography** and spacing system
- **Smooth animations** and micro-interactions throughout

### Technical Improvements
- **JWT authentication system** with refresh tokens and revocation
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
