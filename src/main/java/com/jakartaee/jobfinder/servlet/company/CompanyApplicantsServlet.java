package com.jakartaee.jobfinder.servlet.company;

import com.jakartaee.jobfinder.entity.Application;
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
        String applicationId = req.getParameter("applicationId");

        if (applicationId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing application ID");
            return;
        }
        
        try {
            Application application = applicationService.getApplicationForCompany(applicationId, companyUser);

            if (application.getCvFilePath() == null || application.getCvFileName() == null) {
                renderStyledCvMessage(
                        req,
                        resp,
                        "CV Not Available",
                        "No CV snapshot is attached to this application.",
                        "This usually means the application was submitted before CV snapshots were added, or the applicant did not have a CV attached when applying.",
                        "fa-file-circle-xmark"
                );
                return;
            }

            java.io.File cvFile = new java.io.File(application.getCvFilePath());
            
            if (!cvFile.exists()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "CV file not found on server");
                return;
            }
            
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + application.getCvFileName() + "\"");
            
            try (java.io.FileInputStream fis = new java.io.FileInputStream(cvFile);
                 java.io.OutputStream os = resp.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
            
            MainLogger.logUserAction(SERVLET_NAME, currentUserId, "DOWNLOAD_CV: " + applicationId);
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "DOWNLOAD_CV", e.getMessage(), currentUserId);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to download CV");
        }
    }

    private void renderStyledCvMessage(HttpServletRequest req, HttpServletResponse resp,
                                       String title, String message, String detail, String iconClass)
            throws IOException {
        resp.setContentType("text/html;charset=UTF-8");

        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><head>");
        out.println("<title>" + title + " - JobFinder</title>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css'>");
        out.println("<style>");
        out.println(":root { --bg-deep: #020617; --bg-mid: #0f172a; --panel: rgba(15, 23, 42, 0.88); --panel-border: rgba(148, 163, 184, 0.18); --text-main: #e2e8f0; --text-soft: #94a3b8; --accent-gold: #d4af37; --accent-blue: #38bdf8; --warning-bg: rgba(212, 175, 55, 0.14); --warning-border: rgba(212, 175, 55, 0.35); --shadow: 0 24px 48px rgba(2, 6, 23, 0.45); }");
        out.println("* { box-sizing: border-box; }");
        out.println("body { margin: 0; min-height: 100vh; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: var(--text-main); background: radial-gradient(circle at top, rgba(56, 189, 248, 0.16), transparent 30%), linear-gradient(160deg, var(--bg-deep) 0%, var(--bg-mid) 55%, #111827 100%); }");
        out.println(".page-shell { min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 40px 18px; }");
        out.println(".message-card { width: min(100%, 760px); background: var(--panel); border: 1px solid var(--panel-border); border-radius: 24px; box-shadow: var(--shadow); padding: 32px; backdrop-filter: blur(12px); }");
        out.println(".eyebrow { display: inline-flex; align-items: center; gap: 8px; padding: 8px 14px; border-radius: 999px; background: rgba(56, 189, 248, 0.12); color: var(--accent-blue); font-size: 0.85rem; font-weight: 700; letter-spacing: 0.04em; text-transform: uppercase; }");
        out.println(".hero { display: flex; gap: 18px; align-items: flex-start; margin: 22px 0 24px; }");
        out.println(".hero-icon { width: 72px; height: 72px; flex: 0 0 72px; border-radius: 22px; display: grid; place-items: center; background: linear-gradient(135deg, rgba(212, 175, 55, 0.24), rgba(56, 189, 248, 0.14)); border: 1px solid rgba(212, 175, 55, 0.22); color: var(--accent-gold); font-size: 1.9rem; }");
        out.println("h1 { margin: 0 0 10px; font-size: clamp(1.9rem, 3vw, 2.5rem); line-height: 1.1; color: #f8fafc; }");
        out.println(".lead { margin: 0; color: var(--text-soft); font-size: 1.02rem; line-height: 1.75; }");
        out.println(".notice { margin: 24px 0; padding: 18px 20px; border-radius: 18px; background: var(--warning-bg); border: 1px solid var(--warning-border); color: #f8fafc; }");
        out.println(".notice strong { display: block; color: var(--accent-gold); margin-bottom: 6px; }");
        out.println(".actions { display: flex; flex-wrap: wrap; gap: 12px; margin-top: 28px; }");
        out.println(".btn { display: inline-flex; align-items: center; gap: 10px; text-decoration: none; border-radius: 999px; padding: 12px 20px; font-weight: 700; transition: transform .15s ease, box-shadow .15s ease, border-color .15s ease; }");
        out.println(".btn:hover { transform: translateY(-2px); }");
        out.println(".btn-primary { background: linear-gradient(135deg, #1d4ed8, #38bdf8); color: #fff; box-shadow: 0 16px 30px rgba(29, 78, 216, 0.28); }");
        out.println(".btn-secondary { background: rgba(15, 23, 42, 0.65); color: var(--text-main); border: 1px solid rgba(148, 163, 184, 0.2); }");
        out.println("@media (max-width: 640px) { .message-card { padding: 24px; } .hero { flex-direction: column; } .hero-icon { width: 62px; height: 62px; flex-basis: 62px; } }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<div class='page-shell'>");
        out.println("<section class='message-card'>");
        out.println("<div class='eyebrow'><i class='fas fa-briefcase'></i> Company Applications</div>");
        out.println("<div class='hero'>");
        out.println("<div class='hero-icon'><i class='fas " + iconClass + "'></i></div>");
        out.println("<div>");
        out.println("<h1>" + title + "</h1>");
        out.println("<p class='lead'>" + message + "</p>");
        out.println("</div>");
        out.println("</div>");
        out.println("<div class='notice'><strong>Why this is happening</strong>" + detail + "</div>");
        out.println("<div class='actions'>");
        out.println("<a class='btn btn-primary' href='" + req.getContextPath() + "/company/applicants'><i class='fas fa-arrow-left'></i> Back to Applicants</a>");
        out.println("<a class='btn btn-secondary' href='javascript:history.back()'><i class='fas fa-clock-rotate-left'></i> Previous Page</a>");
        out.println("</div>");
        out.println("</section>");
        out.println("</div>");
        out.println("</body></html>");
    }
}
