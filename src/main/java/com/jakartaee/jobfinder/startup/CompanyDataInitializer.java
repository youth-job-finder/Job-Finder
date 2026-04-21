package com.jakartaee.jobfinder.startup;

import com.jakartaee.jobfinder.dao.CompanyDAO;
import com.jakartaee.jobfinder.dao.JobDAO;
import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.entity.Company;
import com.jakartaee.jobfinder.entity.Job;
import com.jakartaee.jobfinder.entity.role.Role;
import com.jakartaee.jobfinder.entity.status.Status;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.security.utils.BCryptHashAlgorithm;
import com.jakartaee.jobfinder.services.CompanyRegistrationService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Startup
public class CompanyDataInitializer {

    private static final String INITIALIZER_NAME = "CompanyDataInitializer";

    @Inject
    private UserDAO userDAO;

    @Inject
    private CompanyDAO companyDAO;

    @Inject
    private JobDAO jobDAO;

    @Inject
    private BCryptHashAlgorithm passwordHasher;

    @Inject
    private CompanyRegistrationService companyRegistrationService;

    @Transactional
    public void init() {
        MainLogger.logInfo(INITIALIZER_NAME, "Starting default company data initialization");
        
        try {
            // Check if DAOs are injected properly
            if (userDAO == null) {
                MainLogger.logError(INITIALIZER_NAME, "UserDAO is null - dependency injection failed");
                return;
            }
            if (companyDAO == null) {
                MainLogger.logError(INITIALIZER_NAME, "CompanyDAO is null - dependency injection failed");
                return;
            }
            if (jobDAO == null) {
                MainLogger.logError(INITIALIZER_NAME, "JobDAO is null - dependency injection failed");
                return;
            }
            if (passwordHasher == null) {
                MainLogger.logError(INITIALIZER_NAME, "PasswordHasher is null - dependency injection failed");
                return;
            }
            if (companyRegistrationService == null) {
                MainLogger.logError(INITIALIZER_NAME, "CompanyRegistrationService is null - dependency injection failed");
                return;
            }

            MainLogger.logInfo(INITIALIZER_NAME, "All dependencies injected successfully");

            // Initialize Google
            initializeGoogle();

            // Initialize Amazon
            initializeAmazon();

            MainLogger.logInfo(INITIALIZER_NAME, "Default company data initialization completed successfully");
        } catch (Exception e) {
            MainLogger.logError(INITIALIZER_NAME, "Error during company data initialization", e);
            // Print stack trace to server logs
            e.printStackTrace();
        }
    }

    private void initializeGoogle() {
        String email = "admin@google.com";
        String companyName = "Google";

        // Check if company already exists
        if (companyDAO.findByName(companyName).isPresent()) {
            MainLogger.logInfo(INITIALIZER_NAME, "Google company already exists, skipping");
            return;
        }

        // Check if admin user exists, create if not
        User googleAdmin;
        var existingUser = userDAO.findByEmail(email);
        if (existingUser.isPresent()) {
            googleAdmin = existingUser.get();
            MainLogger.logInfo(INITIALIZER_NAME, "Using existing Google admin user: " + email);
        } else {
            // Create Google Admin User
            googleAdmin = new User();
            googleAdmin.setName("Google Admin");
            googleAdmin.setEmail(email);
            googleAdmin.setPasswordHash(passwordHasher.generate("google123".toCharArray()));
            googleAdmin.setRole(Role.COMPANY_ADMIN);
            googleAdmin.setEmailVerified(true);
            googleAdmin = userDAO.create(googleAdmin);
            MainLogger.logInfo(INITIALIZER_NAME, "Created Google admin user: " + email);
        }

        // Create Google Company
        Company google = new Company();
        google.setName("Google");
        google.setRegistrationNumber("GOOGL-001");
        google.setEmail("careers@google.com");
        google.setDescription("Google LLC is an American multinational technology company focusing on artificial intelligence, online advertising, search engine technology, cloud computing, computer software, quantum computing, e-commerce, and consumer electronics.");
        google.setIndustry("Technology");
        google.setUrl("https://www.google.com");
        google.setLocation("Mountain View, California, USA");
        google.setCompanyAdmin(googleAdmin);
        google.setStatus(Status.APPROVED);
        
        // Verify Google URL
        String googleUrl = "https://www.google.com";
        boolean googleUrlValid = companyRegistrationService.verifyCompanyUrl(googleUrl);
        google.setUrlVerified(googleUrlValid);
        if (googleUrlValid) {
            google.setUrlVerifiedAt(LocalDateTime.now());
            MainLogger.logInfo(INITIALIZER_NAME, "Google URL verified successfully: " + googleUrl);
        } else {
            MainLogger.logError(INITIALIZER_NAME, "Google URL verification failed: " + googleUrl);
        }
        
        google = companyDAO.create(google);

        MainLogger.logInfo(INITIALIZER_NAME, "Created Google company");

        // Create 10 Google Jobs
        createGoogleJobs(google);
    }

    private void initializeAmazon() {
        String email = "admin@amazon.com";
        String companyName = "Amazon";

        // Check if company already exists
        if (companyDAO.findByName(companyName).isPresent()) {
            MainLogger.logInfo(INITIALIZER_NAME, "Amazon company already exists, skipping");
            return;
        }

        // Check if admin user exists, create if not
        User amazonAdmin;
        var existingUser = userDAO.findByEmail(email);
        if (existingUser.isPresent()) {
            amazonAdmin = existingUser.get();
            MainLogger.logInfo(INITIALIZER_NAME, "Using existing Amazon admin user: " + email);
        } else {
            // Create Amazon Admin User
            amazonAdmin = new User();
            amazonAdmin.setName("Amazon Admin");
            amazonAdmin.setEmail(email);
            amazonAdmin.setPasswordHash(passwordHasher.generate("amazon123".toCharArray()));
            amazonAdmin.setRole(Role.COMPANY_ADMIN);
            amazonAdmin.setEmailVerified(true);
            amazonAdmin = userDAO.create(amazonAdmin);
            MainLogger.logInfo(INITIALIZER_NAME, "Created Amazon admin user: " + email);
        }

        // Create Amazon Company
        Company amazon = new Company();
        amazon.setName("Amazon");
        amazon.setRegistrationNumber("AMZN-001");
        amazon.setEmail("careers@amazon.com");
        amazon.setDescription("Amazon.com, Inc. is an American multinational technology company focusing on e-commerce, cloud computing, online advertising, digital streaming, and artificial intelligence.");
        amazon.setIndustry("E-commerce, Cloud Computing");
        amazon.setUrl("https://www.amazon.com");
        amazon.setLocation("Seattle, Washington, USA");
        amazon.setCompanyAdmin(amazonAdmin);
        amazon.setStatus(Status.APPROVED);
        
        // Verify Amazon URL
        String amazonUrl = "https://www.amazon.com";
        boolean amazonUrlValid = companyRegistrationService.verifyCompanyUrl(amazonUrl);
        amazon.setUrlVerified(amazonUrlValid);
        if (amazonUrlValid) {
            amazon.setUrlVerifiedAt(LocalDateTime.now());
            MainLogger.logInfo(INITIALIZER_NAME, "Amazon URL verified successfully: " + amazonUrl);
        } else {
            MainLogger.logError(INITIALIZER_NAME, "Amazon URL verification failed: " + amazonUrl);
        }
        
        amazon = companyDAO.create(amazon);

        MainLogger.logInfo(INITIALIZER_NAME, "Created Amazon company");

        // Create 10 Amazon Jobs
        createAmazonJobs(amazon);
    }

    private void createGoogleJobs(Company google) {
        List<Job> jobs = new ArrayList<>();

        jobs.add(createJob(google, "Software Engineer", 
            "Design, develop, test, deploy, maintain and improve software. Manage project priorities, deadlines and deliverables.",
            "Mountain View, CA", 
            "Bachelor's degree in Computer Science or related field. 3+ years of software development experience. Proficiency in Java, Python, or C++.",
            "$120,000 - $180,000",
            "Full-time"));

        jobs.add(createJob(google, "Senior UX Designer",
            "Lead design projects across the entire product lifecycle. Create user-centered designs by understanding business requirements and user feedback.",
            "San Francisco, CA",
            "5+ years of UX design experience. Portfolio demonstrating strong visual design skills. Proficiency in Figma, Sketch, or Adobe XD.",
            "$130,000 - $190,000",
            "Full-time"));

        jobs.add(createJob(google, "Product Manager - Cloud",
            "Define product vision, strategy and roadmap for Google Cloud products. Work with engineering, design, and marketing teams to deliver world-class products.",
            "Sunnyvale, CA",
            "Bachelor's degree in technical field. 4+ years of product management experience. Strong technical background in cloud computing.",
            "$140,000 - $200,000",
            "Full-time"));

        jobs.add(createJob(google, "Data Scientist",
            "Work with large, complex data sets. Solve difficult, non-routine analysis problems. Apply advanced analytical methods as needed.",
            "New York, NY",
            "Master's degree in Statistics, Mathematics, Computer Science, or related field. 3+ years of data science experience. Proficiency in SQL, Python, R.",
            "$125,000 - $185,000",
            "Full-time"));

        jobs.add(createJob(google, "DevOps Engineer",
            "Build and maintain CI/CD pipelines. Manage cloud infrastructure. Implement monitoring and alerting solutions.",
            "Austin, TX",
            "Bachelor's degree in Computer Science. 3+ years of DevOps experience. Experience with Kubernetes, Docker, Terraform.",
            "$115,000 - $170,000",
            "Full-time"));

        jobs.add(createJob(google, "Mobile Developer - Android",
            "Design and build advanced applications for the Android platform. Collaborate with cross-functional teams to define and ship new features.",
            "Mountain View, CA",
            "Bachelor's degree in Computer Science. 3+ years of Android development experience. Strong knowledge of Java and Kotlin.",
            "$120,000 - $175,000",
            "Full-time"));

        jobs.add(createJob(google, "Technical Program Manager",
            "Lead complex, multi-disciplinary engineering projects. Plan requirements with internal customers and usher projects through the entire project lifecycle.",
            "Seattle, WA",
            "Bachelor's degree in technical field. 5+ years of technical program management experience. PMP certification preferred.",
            "$135,000 - $195,000",
            "Full-time"));

        jobs.add(createJob(google, "Machine Learning Engineer",
            "Design and implement machine learning models for various Google products. Work with large-scale data processing systems.",
            "Palo Alto, CA",
            "Master's degree in Computer Science or related field. 3+ years of ML engineering experience. Experience with TensorFlow, PyTorch.",
            "$140,000 - $210,000",
            "Full-time"));

        jobs.add(createJob(google, "Site Reliability Engineer",
            "Ensure the reliability and performance of Google's services. Build automation to prevent and address system failures.",
            "Los Angeles, CA",
            "Bachelor's degree in Computer Science. 3+ years of SRE or systems engineering experience. Strong programming skills in Python or Go.",
            "$125,000 - $180,000",
            "Full-time"));

        jobs.add(createJob(google, "Marketing Manager - Digital",
            "Develop and execute marketing campaigns for Google's products. Analyze campaign performance and optimize for better results.",
            "Chicago, IL",
            "Bachelor's degree in Marketing or related field. 4+ years of digital marketing experience. Experience with Google Ads and Analytics.",
            "$95,000 - $145,000",
            "Full-time"));

        for (Job job : jobs) {
            jobDAO.create(job);
        }

        MainLogger.logInfo(INITIALIZER_NAME, "Created " + jobs.size() + " jobs for Google");
    }

    private void createAmazonJobs(Company amazon) {
        List<Job> jobs = new ArrayList<>();

        jobs.add(createJob(amazon, "Software Development Engineer",
            "Design, develop, and maintain software solutions for Amazon's e-commerce platform. Work on scalable distributed systems.",
            "Seattle, WA",
            "Bachelor's degree in Computer Science. 2+ years of software development experience. Proficiency in Java, C++, or C#.",
            "$110,000 - $170,000",
            "Full-time"));

        jobs.add(createJob(amazon, "Solutions Architect - AWS",
            "Help customers architect scalable, secure, and cost-effective solutions on AWS. Provide technical guidance and best practices.",
            "Remote",
            "Bachelor's degree in technical field. 5+ years of IT experience. AWS certification preferred. Strong communication skills.",
            "$130,000 - $190,000",
            "Remote"));

        jobs.add(createJob(amazon, "Operations Manager",
            "Manage day-to-day operations of fulfillment centers. Lead and develop a team of associates to meet operational goals.",
            "Phoenix, AZ",
            "Bachelor's degree in Business, Operations, or related field. 3+ years of operations management experience. Strong leadership skills.",
            "$80,000 - $120,000",
            "Full-time"));

        jobs.add(createJob(amazon, "Data Engineer",
            "Design and build large-scale data processing systems. Work with big data technologies to enable data-driven decisions.",
            "Boston, MA",
            "Bachelor's degree in Computer Science. 3+ years of data engineering experience. Experience with Hadoop, Spark, AWS data services.",
            "$120,000 - $175,000",
            "Full-time"));

        jobs.add(createJob(amazon, "Product Marketing Manager",
            "Develop go-to-market strategies for Amazon products. Create compelling messaging and positioning for target audiences.",
            "New York, NY",
            "Bachelor's degree in Marketing or related field. 4+ years of product marketing experience. Experience in tech industry preferred.",
            "$100,000 - $150,000",
            "Full-time"));

        jobs.add(createJob(amazon, "Supply Chain Analyst",
            "Analyze supply chain data to optimize inventory levels and delivery times. Develop forecasting models and performance metrics.",
            "Nashville, TN",
            "Bachelor's degree in Supply Chain, Analytics, or related field. 2+ years of supply chain experience. Proficiency in SQL and Excel.",
            "$70,000 - $100,000",
            "Full-time"));

        jobs.add(createJob(amazon, "Frontend Engineer - React",
            "Build responsive and performant user interfaces for Amazon's web applications. Collaborate with UX designers and backend engineers.",
            "San Francisco, CA",
            "Bachelor's degree in Computer Science. 3+ years of frontend development experience. Expert in React, JavaScript, HTML, CSS.",
            "$125,000 - $185,000",
            "Full-time"));

        jobs.add(createJob(amazon, "Quality Assurance Engineer",
            "Develop and execute test plans for Amazon's software products. Automate testing processes to ensure high-quality releases.",
            "Austin, TX",
            "Bachelor's degree in Computer Science or related field. 3+ years of QA experience. Experience with automation frameworks.",
            "$95,000 - $140,000",
            "Full-time"));

        jobs.add(createJob(amazon, "Business Intelligence Engineer",
            "Build reporting systems and dashboards to support business decisions. Work with stakeholders to understand analytical needs.",
            "Denver, CO",
            "Bachelor's degree in Computer Science, Engineering, or related field. 3+ years of BI experience. Proficiency in SQL, Tableau, or similar tools.",
            "$100,000 - $150,000",
            "Full-time"));

        jobs.add(createJob(amazon, "Warehouse Associate Team Lead",
            "Supervise warehouse operations and lead a team of associates. Ensure safety protocols and productivity targets are met.",
            "Indianapolis, IN",
            "High school diploma or equivalent. 2+ years of warehouse experience with 1+ year in a leadership role. Ability to lift up to 49 lbs.",
            "$45,000 - $60,000",
            "Full-time"));

        for (Job job : jobs) {
            jobDAO.create(job);
        }

        MainLogger.logInfo(INITIALIZER_NAME, "Created " + jobs.size() + " jobs for Amazon");
    }

    private Job createJob(Company company, String title, String description, String location, String requirements, String salaryRange, String jobType) {
        Job job = new Job();
        job.setCompany(company);
        job.setJobTitle(title);
        job.setJobDescription(description);
        job.setPhysicalAddress(location);
        job.setJobRequirements(requirements);
        job.setSalaryRange(salaryRange);
        job.setJobType(jobType);
        return job;
    }
}
