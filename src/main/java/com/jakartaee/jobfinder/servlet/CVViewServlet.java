package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.dao.CVDAO;
import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.models.Application;
import com.jakartaee.jobfinder.models.CV;
import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.ApplicationService;
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
    private ApplicationService applicationService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String currentUserId = (String) req.getSession().getAttribute("userId");
        if (currentUserId == null && req.getUserPrincipal() != null) {
            currentUserId = req.getUserPrincipal().getName();
        }

        if (currentUserId == null) {
            showErrorPage(req, resp, "Authentication Required", "Please log in to view CVs.");
            return;
        }

        String applicantId = req.getParameter("applicantId");
        String applicationId = req.getParameter("applicationId");

        try {
            // Get the current user
            User currentUser = userDAO.findById(currentUserId);
            if (currentUser == null) {
                showErrorPage(req, resp, "User Not Found", "Your user account could not be found.");
                return;
            }

            // Check user role and handle accordingly
            if (currentUser.getRole() == com.jakartaee.jobfinder.models.role.Role.APPLICANT) {
                // Applicant viewing their own CV
                handleApplicantViewOwnCV(req, resp, currentUser);
            } else if (currentUser.getRole() == com.jakartaee.jobfinder.models.role.Role.COMPANY_ADMIN) {
                if (applicationId != null && !applicationId.trim().isEmpty()) {
                    handleCompanyViewApplicationCV(req, resp, currentUser, applicationId.trim());
                } else if (applicantId != null && !applicantId.trim().isEmpty()) {
                    handleCompanyViewApplicantCV(req, resp, currentUser, applicantId);
                } else {
                    showErrorPage(req, resp, "Missing Information", "Application ID is required.");
                    return;
                }
            } else {
                showErrorPage(req, resp, "Access Denied", "You do not have permission to view CVs.");
            }

        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_CV", e.getMessage(), currentUserId);
            showErrorPage(req, resp, "Error", "An error occurred while trying to view the CV. Please try again.");
        }
    }

    private void handleApplicantViewOwnCV(HttpServletRequest req, HttpServletResponse resp, User applicant)
            throws IOException {
        Optional<CV> cvOpt = cvDAO.findByUser(applicant);

        if (cvOpt.isPresent()) {
            CV cv = cvOpt.get();
            String filePath = cv.getFilePath();
            java.io.File cvFile = new java.io.File(filePath);

            if (cvFile.exists()) {
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
            }

            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_OWN_CV_FILE_NOT_FOUND",
                    "File not found: " + filePath, applicant.getId());
        }

        showNoCVPage(req, resp, applicant);
        MainLogger.logUserAction(SERVLET_NAME, applicant.getId(), "VIEW_OWN_CV_NOT_FOUND");
    }

    private void handleCompanyViewApplicationCV(HttpServletRequest req, HttpServletResponse resp,
                                                User companyUser, String applicationId) throws IOException {
        Application application = applicationService.getApplicationForCompany(applicationId, companyUser);
        User applicant = application.getApplicant();

        if (application.getCvFilePath() == null || application.getCvFileName() == null) {
            showNoCVPage(req, resp, applicant);
            MainLogger.logUserAction(SERVLET_NAME, companyUser.getId(), "VIEW_CV_NOT_FOUND: " + applicationId);
            return;
        }

        java.io.File cvFile = new java.io.File(application.getCvFilePath());
        if (!cvFile.exists()) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_CV_FILE_NOT_FOUND",
                    "File not found: " + application.getCvFilePath(), companyUser.getId());
            showNoCVPage(req, resp, applicant);
            return;
        }

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "inline; filename=\"" + application.getCvFileName() + "\"");

        try (java.io.FileInputStream fis = new java.io.FileInputStream(cvFile);
             java.io.OutputStream os = resp.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
        MainLogger.logUserAction(SERVLET_NAME, companyUser.getId(), "VIEW_CV: " + applicationId);
    }

    private void handleCompanyViewApplicantCV(HttpServletRequest req, HttpServletResponse resp,
                                              User companyUser, String applicantId) throws IOException {
        User applicant = userDAO.findById(applicantId);
        if (applicant == null || !applicationService.validateApplicantForCompany(applicant, companyUser)) {
            showErrorPage(req, resp, "Access Denied", "This applicant has not applied to any jobs at your company.");
            return;
        }

        showErrorPage(req, resp, "Unsupported Link",
                "Open the CV from the specific application row so you see the CV attached to that application.");
    }

    private void showNoCVPage(HttpServletRequest req, HttpServletResponse resp, User applicant) throws IOException {
        resp.setContentType("text/html");
        String backUrl = resolveBackUrl(req);
        String homeUrl = resolveHomeUrl(req);
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><head>");
        out.println("<title>CV Not Available - JobFinder</title>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css'>");
        out.println("<style>");
        out.println(":root { --bg-deep: #020617; --bg-mid: #0f172a; --panel: rgba(15, 23, 42, 0.88); --panel-border: rgba(148, 163, 184, 0.18); --text-main: #e2e8f0; --text-soft: #94a3b8; --accent-gold: #d4af37; --accent-blue: #38bdf8; --warning-bg: rgba(212, 175, 55, 0.14); --warning-border: rgba(212, 175, 55, 0.35); --warning-text: #f8fafc; --shadow: 0 24px 48px rgba(2, 6, 23, 0.45); }");
        out.println("* { box-sizing: border-box; }");
        out.println("body { margin: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: var(--text-main); background: radial-gradient(circle at top, rgba(56, 189, 248, 0.16), transparent 30%), linear-gradient(160deg, var(--bg-deep) 0%, var(--bg-mid) 55%, #111827 100%); min-height: 100vh; }");
        out.println(".shell { max-width: 960px; margin: 0 auto; padding: 48px 20px; min-height: 100vh; display: flex; align-items: center; }");
        out.println(".card { width: 100%; background: var(--panel); border-radius: 24px; box-shadow: var(--shadow); padding: 32px; border: 1px solid var(--panel-border); backdrop-filter: blur(12px); }");
        out.println(".eyebrow { display: inline-flex; align-items: center; gap: 8px; padding: 8px 14px; border-radius: 999px; background: rgba(56, 189, 248, 0.12); color: var(--accent-blue); font-size: 0.85rem; font-weight: 700; letter-spacing: 0.04em; text-transform: uppercase; }");
        out.println(".hero { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; }");
        out.println(".hero-icon { width: 72px; height: 72px; border-radius: 22px; display: grid; place-items: center; background: linear-gradient(135deg, rgba(212, 175, 55, 0.24), rgba(56, 189, 248, 0.14)); border: 1px solid rgba(212, 175, 55, 0.22); color: var(--accent-gold); font-size: 30px; }");
        out.println("h1 { margin: 0 0 6px; font-size: 2rem; color: #f8fafc; }");
        out.println(".subtitle { margin: 0; color: var(--text-soft); line-height: 1.7; }");
        out.println(".notice { margin: 24px 0; padding: 18px 20px; border-radius: 18px; background: var(--warning-bg); border: 1px solid var(--warning-border); color: var(--warning-text); display: flex; gap: 12px; align-items: flex-start; }");
        out.println(".notice i { margin-top: 2px; }");
        out.println(".info-panel { margin-top: 24px; padding: 24px; border-radius: 20px; background: rgba(15, 23, 42, 0.55); border: 1px solid rgba(148,163,184,0.18); }");
        out.println(".info-panel h3 { margin: 0 0 18px; color: var(--accent-gold); }");
        out.println(".info-row { display: flex; flex-wrap: wrap; gap: 8px; padding: 10px 0; border-bottom: 1px solid rgba(148,163,184,0.16); }");
        out.println(".info-row:last-child { border-bottom: none; }");
        out.println(".info-label { min-width: 120px; font-weight: 700; color: #f8fafc; }");
        out.println(".info-value { color: var(--text-soft); word-break: break-word; }");
        out.println(".actions { margin-top: 28px; display: flex; gap: 12px; flex-wrap: wrap; }");
        out.println(".btn { display: inline-flex; align-items: center; gap: 10px; text-decoration: none; border-radius: 999px; padding: 12px 18px; font-weight: 700; transition: transform .15s ease, box-shadow .15s ease; }");
        out.println(".btn:hover { transform: translateY(-1px); }");
        out.println(".btn-primary { background: linear-gradient(135deg, #1d4ed8, #38bdf8); color: white; box-shadow: 0 16px 30px rgba(29,78,216,0.28); }");
        out.println(".btn-secondary { background: rgba(15, 23, 42, 0.65); color: var(--text-main); border: 1px solid rgba(148, 163, 184, 0.2); }");
        out.println("@media (max-width: 640px) { .card { padding: 24px; } .hero { align-items: flex-start; } h1 { font-size: 1.65rem; } .info-label { min-width: 100%; } }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<div class='shell'>");
        out.println("<div class='card'>");
        out.println("<div class='eyebrow'><i class='fas fa-briefcase'></i> Company Applications</div>");
        out.println("<div class='hero'>");
        out.println("<div class='hero-icon'><i class='fas fa-file-alt'></i></div>");
        out.println("<div>");
        out.println("<h1>CV Not Available</h1>");
        out.println("<p class='subtitle'>This application does not currently have a CV snapshot available to view.</p>");
        out.println("</div>");
        out.println("</div>");
        out.println("<div class='notice'>");
        out.println("<i class='fas fa-exclamation-triangle'></i>");
        out.println("<div><strong>No CV Snapshot Found</strong><br>This can happen when the application was created before CV snapshot support was added, or if no CV was attached at submission time.</div>");
        out.println("</div>");
        out.println("<div class='info-panel'>");
        out.println("<h3>Applicant Information</h3>");
        out.println("<div class='info-row'><span class='info-label'>Name:</span><span class='info-value'>" + 
                   (applicant.getName() != null ? applicant.getName() : "N/A") + "</span></div>");
        out.println("<div class='info-row'><span class='info-label'>Email:</span><span class='info-value'>" + 
                   (applicant.getEmail() != null ? applicant.getEmail() : "N/A") + "</span></div>");
        out.println("<div class='info-row'><span class='info-label'>User ID:</span><span class='info-value'>" + 
                   applicant.getId() + "</span></div>");
        out.println("</div>");
        out.println("<div class='actions'>");
        out.println("<a class='btn btn-primary' href='" + backUrl + "'><i class='fas fa-arrow-left'></i> Back</a>");
        out.println("<a class='btn btn-secondary' href='" + homeUrl + "'><i class='fas fa-house'></i> Home</a>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        out.println("</body></html>");
    }

    private void showErrorPage(HttpServletRequest req, HttpServletResponse resp, String title, String message)
            throws IOException {
        resp.setContentType("text/html");
        String backUrl = resolveBackUrl(req);
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><head>");
        out.println("<title>" + title + " - JobFinder</title>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css'>");
        out.println("<style>");
        out.println(":root { --bg-deep: #020617; --bg-mid: #0f172a; --card: rgba(15, 23, 42, 0.88); --danger: #f87171; --danger-soft: rgba(248, 113, 113, 0.14); --text: #e2e8f0; --muted: #94a3b8; --shadow: 0 24px 48px rgba(2,6,23,0.45); --border: rgba(148, 163, 184, 0.18); }");
        out.println("* { box-sizing: border-box; }");
        out.println("body { margin: 0; min-height: 100vh; display: grid; place-items: center; padding: 24px; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: radial-gradient(circle at top, rgba(56, 189, 248, 0.16), transparent 30%), linear-gradient(160deg, var(--bg-deep) 0%, var(--bg-mid) 55%, #111827 100%); color: var(--text); }");
        out.println(".container { width: min(100%, 640px); background: var(--card); padding: 32px; border-radius: 24px; box-shadow: var(--shadow); text-align: center; border: 1px solid var(--border); backdrop-filter: blur(12px); }");
        out.println(".icon { width: 72px; height: 72px; margin: 0 auto 18px; border-radius: 22px; display: grid; place-items: center; background: var(--danger-soft); color: var(--danger); font-size: 30px; }");
        out.println("h1 { margin: 0 0 12px; color: #f8fafc; font-size: 2rem; }");
        out.println(".message { color: var(--muted); margin: 0 auto 24px; max-width: 42ch; line-height: 1.7; }");
        out.println(".btn { display: inline-flex; align-items: center; gap: 10px; text-decoration: none; padding: 12px 18px; border-radius: 999px; background: linear-gradient(135deg, #1d4ed8, #38bdf8); color: #fff; font-weight: 700; box-shadow: 0 16px 30px rgba(29,78,216,0.28); }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<div class='container'>");
        out.println("<div class='icon'><i class='fas fa-exclamation-circle'></i></div>");
        out.println("<h1>" + title + "</h1>");
        out.println("<p class='message'>" + message + "</p>");
        out.println("<a class='btn' href='" + backUrl + "'><i class='fas fa-arrow-left'></i> Go Back</a>");
        out.println("</div>");
        out.println("</body></html>");
    }

    private String resolveBackUrl(HttpServletRequest req) {
        String referer = req.getHeader("Referer");
        String contextPath = req.getContextPath();

        if (referer != null && !referer.isBlank() && referer.contains(contextPath)) {
            return referer;
        }

        Object role = req.getSession().getAttribute("role");
        if (role != null && "COMPANY_ADMIN".equals(String.valueOf(role))) {
            return contextPath + "/company/applicants";
        }
        if (role != null && "APPLICANT".equals(String.valueOf(role))) {
            return contextPath + "/applicant/cv";
        }

        return resolveHomeUrl(req);
    }

    private String resolveHomeUrl(HttpServletRequest req) {
        String contextPath = req.getContextPath();
        Object role = req.getSession().getAttribute("role");
        if (role != null && "COMPANY_ADMIN".equals(String.valueOf(role))) {
            return contextPath + "/company/dashboard";
        }
        if (role != null && "APPLICANT".equals(String.valueOf(role))) {
            return contextPath + "/applicant/dashboard";
        }
        return (contextPath == null || contextPath.isBlank()) ? "/" : contextPath + "/";
    }
}
