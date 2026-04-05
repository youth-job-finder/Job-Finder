package com.jakartaee.jobfinder.servlet.company;

import com.jakartaee.jobfinder.dao.CVDAO;
import com.jakartaee.jobfinder.entity.Application;
import com.jakartaee.jobfinder.entity.CV;
import com.jakartaee.jobfinder.entity.Company;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.entity.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.ApplicationService;
import com.jakartaee.jobfinder.services.AuthService;
import com.jakartaee.jobfinder.services.CompanyService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

/**
 * Servlet for managing company applicants.
 * Displays applicants for company jobs and handles applicant status updates.
 */
@WebServlet("/company/applicants")
public class CompanyApplicantsServlet extends HttpServlet {

    private static final String SERVLET_NAME = "CompanyApplicantsServlet";

    @Inject
    private AuthService authService;

    @Inject
    private CVDAO cvDAO;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private CompanyService companyService;

    /**
     * Displays applicants for the company's jobs.
     * Supports filtering by job ID or showing all applicants.
     *
     * @param req  the HTTP request, may contain jobId parameter for filtering
     * @param resp the HTTP response for rendering the applicants view
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = authService.requireCompanyAdmin(req, resp, SERVLET_NAME);
        if (user == null) {
            return;
        }

        String currentUserId = user.getId();
        MainLogger.logUserAction(SERVLET_NAME, currentUserId, "VIEW_COMPANY_APPLICANTS");

        try {
            Company company = companyService.getCompanyByUser(user);
            if (company != null) {
                String jobId = req.getParameter("jobId");
                List<Application> applications;
                
                if (jobId != null && !jobId.trim().isEmpty()) {
                    // Get applicants for specific job
                    applications = applicationService.getApplicationsByJobId(jobId);
                    req.setAttribute("selectedJobId", jobId);
                    MainLogger.logUserAction(SERVLET_NAME, currentUserId, "VIEW_APPLICANTS_FOR_JOB: " + jobId);
                } else {
                    // Get all applicants for company
                    applications = applicationService.getAllApplicationsForCompany(user);
                    MainLogger.logUserAction(SERVLET_NAME, currentUserId, "VIEW_ALL_APPLICANTS");
                }
                
                req.setAttribute("applications", applications);
                req.setAttribute("company", company);
            }

            req.setAttribute("isAuthenticated", true);
            req.setAttribute("role", Role.COMPANY_ADMIN.name());
            req.getRequestDispatcher("/views/company/applicants.jsp").forward(req, resp);

        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_APPLICANTS", e.getMessage(), currentUserId);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load applicants");
        }
    }

    /**
     * Processes applicant management actions.
     * Supports actions: updateStatus, downloadCV, deleteApplication.
     *
     * @param req  the HTTP request containing action and applicant data
     * @param resp the HTTP response for redirecting or returning data
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = authService.requireCompanyAdmin(req, resp, SERVLET_NAME);
        if (user == null) {
            return;
        }

        String currentUserId = user.getId();

        String action = req.getParameter("action");
        
        if ("updateStatus".equals(action)) {
            handleStatusUpdate(req, resp, user);
        } else if ("downloadCV".equals(action)) {
            handleCVDownload(req, resp, user);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    private void handleStatusUpdate(HttpServletRequest req, HttpServletResponse resp, User companyUser)
            throws IOException {
        
        String applicationId = req.getParameter("applicationId");
        String newStatus = req.getParameter("status");
        String currentUserId = companyUser.getId();
        
        if (applicationId == null || newStatus == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters");
            return;
        }
        
        try {
            applicationService.updateApplicationStatus(applicationId, newStatus, companyUser);
            
            MainLogger.logUserAction(SERVLET_NAME, currentUserId, 
                "UPDATE_APPLICATION_STATUS: " + applicationId + " to " + newStatus);
            
            // Redirect back to applicants page with success message
            resp.sendRedirect(req.getContextPath() + "/company/applicants?success=Status+updated+successfully");
            
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid status value");
        } catch (IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "UPDATE_STATUS", e.getMessage(), currentUserId);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update status");
        }
    }

    private void handleCVDownload(HttpServletRequest req, HttpServletResponse resp, User companyUser)
            throws IOException {
        
        String currentUserId = companyUser.getId();
        String applicantId = req.getParameter("applicantId");
        
        if (applicantId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing applicant ID");
            return;
        }
        
        try {
            // Verify the applicant has applied to a job from this company
            Company company = companyService.getCompanyByUser(companyUser);
            
            if (company == null) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "No company associated with user");
                return;
            }
            
            // Get applicant from application service
            User applicant = applicationService.getApplicantById(applicantId);
            
            if (applicant == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Applicant not found");
                return;
            }
            
            // Check if applicant has any applications for this company's jobs
            boolean hasApplicationWithCompany = applicationService.validateApplicantForCompany(applicant, companyUser);
            
            if (!hasApplicationWithCompany) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, 
                    "Applicant has not applied to any jobs at your company");
                return;
            }
            
            // Get CV from database using CVDAO
            Optional<CV> cvOpt = cvDAO.findByUser(applicant);
            
            if (cvOpt.isEmpty()) {
                resp.setContentType("text/html");
                PrintWriter out = resp.getWriter();
                out.println("<html><body>");
                out.println("<h2>CV Not Available</h2>");
                out.println("<p>The applicant has not uploaded a CV yet.</p>");
                out.println("<a href='" + req.getContextPath() + "/company/applicants'>Back to Applicants</a>");
                out.println("</body></html>");
                return;
            }
            
            CV cv = cvOpt.get();
            java.io.File cvFile = new java.io.File(cv.getFilePath());
            
            if (!cvFile.exists()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "CV file not found on server");
                return;
            }
            
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + cv.getFileName() + "\"");
            
            try (java.io.FileInputStream fis = new java.io.FileInputStream(cvFile);
                 java.io.OutputStream os = resp.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
            
            MainLogger.logUserAction(SERVLET_NAME, currentUserId, "DOWNLOAD_CV: " + applicantId);
            
        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "DOWNLOAD_CV", e.getMessage(), currentUserId);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to download CV");
        }
    }
}
