package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.dao.CVDAO;
import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.entity.CV;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.ApplicationService;
import com.jakartaee.jobfinder.services.CompanyService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

/**
 * Servlet for viewing applicant CVs in a new browser tab.
 * Accessible by company admins who have received applications from the applicant.
 */
@WebServlet("/view-cv")
public class CVViewServlet extends HttpServlet {

    private static final String SERVLET_NAME = "CVViewServlet";

    @Inject
    private UserDAO userDAO;

    @Inject
    private CVDAO cvDAO;

    @Inject
    private CompanyService companyService;

    @Inject
    private ApplicationService applicationService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String currentUserId = (String) req.getSession().getAttribute("userId");
        if (currentUserId == null && req.getUserPrincipal() != null) {
            currentUserId = req.getUserPrincipal().getName();
        }

        if (currentUserId == null) {
            showErrorPage(resp, "Authentication Required", "Please log in to view CVs.");
            return;
        }

        String applicantId = req.getParameter("applicantId");

        try {
            // Get the current user
            User currentUser = userDAO.findById(currentUserId);
            if (currentUser == null) {
                showErrorPage(resp, "User Not Found", "Your user account could not be found.");
                return;
            }

            // Check user role and handle accordingly
            if (currentUser.getRole() == com.jakartaee.jobfinder.entity.role.Role.APPLICANT) {
                // Applicant viewing their own CV
                handleApplicantViewOwnCV(resp, currentUser);
            } else if (currentUser.getRole() == com.jakartaee.jobfinder.entity.role.Role.COMPANY_ADMIN) {
                // Company admin viewing an applicant's CV
                if (applicantId == null || applicantId.trim().isEmpty()) {
                    showErrorPage(resp, "Missing Information", "Applicant ID is required.");
                    return;
                }
                handleCompanyViewApplicantCV(req, resp, currentUser, applicantId);
            } else {
                showErrorPage(resp, "Access Denied", "You do not have permission to view CVs.");
            }

        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_CV", e.getMessage(), currentUserId);
            showErrorPage(resp, "Error", "An error occurred while trying to view the CV. Please try again.");
        }
    }

    private void handleApplicantViewOwnCV(HttpServletResponse resp, User applicant) throws IOException {
        // Get CV from database for the applicant
        Optional<CV> cvOpt = cvDAO.findByUser(applicant);

        if (cvOpt.isPresent()) {
            CV cv = cvOpt.get();
            String filePath = cv.getFilePath();
            java.io.File cvFile = new java.io.File(filePath);

            if (cvFile.exists()) {
                // Serve CV from file system
                resp.setContentType("application/pdf");
                resp.setHeader("Content-Disposition", "inline; filename=\"" + cv.getFileName() + "\"");

                try (java.io.FileInputStream fis = new java.io.FileInputStream(cvFile);
                     java.io.OutputStream os = resp.getOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
                MainLogger.logUserAction(SERVLET_NAME, applicant.getId(), "VIEW_OWN_CV");
                return;
            } else {
                // File not found on disk, but CV record exists
                MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_OWN_CV_FILE_NOT_FOUND",
                    "File not found: " + filePath, applicant.getId());
            }
        }

        // No CV found - show placeholder page
        showNoCVPage(resp, applicant);
        MainLogger.logUserAction(SERVLET_NAME, applicant.getId(), "VIEW_OWN_CV_NOT_FOUND");
    }

    private void handleCompanyViewApplicantCV(HttpServletRequest req, HttpServletResponse resp,
            User companyUser, String applicantId) throws IOException {

        // Get the applicant
        User applicant = userDAO.findById(applicantId);
        if (applicant == null) {
            showErrorPage(resp, "Applicant Not Found", "The applicant could not be found.");
            return;
        }

        // Verify the applicant has applied to a job at this company
        boolean hasApplication = applicationService.validateApplicantForCompany(applicant, companyUser);
        if (!hasApplication) {
            showErrorPage(resp, "Access Denied",
                "This applicant has not applied to any jobs at your company.");
            return;
        }

        // Get CV from database
        Optional<CV> cvOpt = cvDAO.findByUser(applicant);

        if (cvOpt.isPresent()) {
            CV cv = cvOpt.get();
            String filePath = cv.getFilePath();
            java.io.File cvFile = new java.io.File(filePath);

            if (cvFile.exists()) {
                // Serve CV from file system
                resp.setContentType("application/pdf");
                resp.setHeader("Content-Disposition", "inline; filename=\"" + cv.getFileName() + "\"");

                try (java.io.FileInputStream fis = new java.io.FileInputStream(cvFile);
                     java.io.OutputStream os = resp.getOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
                MainLogger.logUserAction(SERVLET_NAME, companyUser.getId(), "VIEW_CV: " + applicantId);
                return;
            } else {
                // File not found on disk, but CV record exists
                MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_CV_FILE_NOT_FOUND",
                    "File not found: " + filePath, companyUser.getId());
            }
        }

        // No CV found - show placeholder page with applicant info
        showNoCVPage(resp, applicant);
        MainLogger.logUserAction(SERVLET_NAME, companyUser.getId(), "VIEW_CV_NOT_FOUND: " + applicantId);
    }

    private void showNoCVPage(HttpServletResponse resp, User applicant) throws IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><head>");
        out.println("<title>CV Not Available - JobFinder</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; ");
        out.println("       background: #f5f7fa; margin: 0; padding: 2rem; }");
        out.println(".container { max-width: 800px; margin: 0 auto; background: white; ");
        out.println("            padding: 2rem; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }");
        out.println("h1 { color: #333; margin-bottom: 1rem; }");
        out.println(".applicant-info { background: #f8f9fa; padding: 1.5rem; border-radius: 8px; margin: 1.5rem 0; }");
        out.println(".applicant-info h3 { margin-top: 0; color: #667eea; }");
        out.println(".info-row { display: flex; margin: 0.75rem 0; }");
        out.println(".info-label { font-weight: 600; width: 120px; color: #555; }");
        out.println(".info-value { color: #333; }");
        out.println(".no-cv-notice { background: #fff3cd; border: 1px solid #ffc107; ");
        out.println("                padding: 1rem; border-radius: 8px; margin: 1.5rem 0; }");
        out.println(".no-cv-notice i { color: #ffc107; margin-right: 0.5rem; }");
        out.println("</style>");
        out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css'>");
        out.println("</head><body>");
        out.println("<div class='container'>");
        out.println("<h1><i class='fas fa-file-alt'></i> Applicant CV</h1>");
        out.println("<div class='no-cv-notice'>");
        out.println("<i class='fas fa-exclamation-triangle'></i>");
        out.println("<strong>No CV Available</strong><br>");
        out.println("This applicant has not uploaded a CV yet.");
        out.println("</div>");
        out.println("<div class='applicant-info'>");
        out.println("<h3>Applicant Information</h3>");
        out.println("<div class='info-row'><span class='info-label'>Name:</span><span class='info-value'>" + 
                   (applicant.getName() != null ? applicant.getName() : "N/A") + "</span></div>");
        out.println("<div class='info-row'><span class='info-label'>Email:</span><span class='info-value'>" + 
                   (applicant.getEmail() != null ? applicant.getEmail() : "N/A") + "</span></div>");
        out.println("<div class='info-row'><span class='info-label'>User ID:</span><span class='info-value'>" + 
                   applicant.getId() + "</span></div>");
        out.println("</div>");
        out.println("</div>");
        out.println("</body></html>");
    }

    private void showErrorPage(HttpServletResponse resp, String title, String message) throws IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><head>");
        out.println("<title>" + title + " - JobFinder</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; ");
        out.println("       background: #f5f7fa; margin: 0; padding: 2rem; }");
        out.println(".container { max-width: 600px; margin: 0 auto; background: white; ");
        out.println("            padding: 2rem; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); ");
        out.println("            text-align: center; }");
        out.println("h1 { color: #dc3545; }");
        out.println(".message { color: #666; margin: 1.5rem 0; }");
        out.println("</style>");
        out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css'>");
        out.println("</head><body>");
        out.println("<div class='container'>");
        out.println("<h1><i class='fas fa-exclamation-circle'></i> " + title + "</h1>");
        out.println("<p class='message'>" + message + "</p>");
        out.println("</div>");
        out.println("</body></html>");
    }
}
