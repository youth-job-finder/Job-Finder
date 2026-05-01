package com.jakartaee.jobfinder.servlet.company;

import com.jakartaee.jobfinder.dto.PaginationDTO;
import com.jakartaee.jobfinder.models.Company;
import com.jakartaee.jobfinder.models.Job;
import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.models.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.AuthService;
import com.jakartaee.jobfinder.services.CompanyService;
import com.jakartaee.jobfinder.services.JobService;
import com.jakartaee.jobfinder.utils.PaginationUtil;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet for displaying and managing company job listings.
 * Shows all jobs for the authenticated company and handles job deletion.
 */
@WebServlet("/company/listings")
public class CompanyListingsServlet extends HttpServlet {

    private static final String SERVLET_NAME = "CompanyListingsServlet";

    @Inject
    private AuthService authService;

    @Inject
    private JobService jobService;

    @Inject
    private CompanyService companyService;

    /**
     * Displays all job listings for the authenticated company.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response for rendering the listings view
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
        MainLogger.logUserAction(SERVLET_NAME, currentUserId, "VIEW_COMPANY_LISTINGS");

        try {
            Company company = companyService.getCompanyByUser(user);
            if (company != null) {
                List<Job> jobs = jobService.getJobsByCompany(company);
                int page = PaginationUtil.parsePageParameter(req.getParameter("page"));
                PaginationDTO<Job> pagination = PaginationUtil.paginate(jobs, page, 10);
                req.setAttribute("jobs", pagination.getItems());
                req.setAttribute("pagination", pagination);
                req.setAttribute("company", company);
            }

            req.setAttribute("isAuthenticated", true);
            req.setAttribute("role", Role.COMPANY_ADMIN.name());
            req.getRequestDispatcher("/views/company/listings.jsp").forward(req, resp);

        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VIEW_LISTINGS", e.getMessage(), currentUserId);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load listings");
        }
    }

    /**
     * Processes job deletion requests.
     * Verifies job ownership before deleting.
     *
     * @param req  the HTTP request containing the action and jobId parameters
     * @param resp the HTTP response for redirecting after deletion
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
        
        if ("delete".equals(action)) {
            String jobId = req.getParameter("jobId");
            if (jobId == null || jobId.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/company/listings?error=Invalid job ID");
                return;
            }
            
            try {
                // Verify job belongs to this company before deleting
                Company company = companyService.getCompanyByUser(user);
                Job job = jobService.getJobById(jobId);
                
                if (job == null || !job.getCompany().getId().equals(company.getId())) {
                    resp.sendRedirect(req.getContextPath() + "/company/listings?error=Job not found or unauthorized");
                    return;
                }
                
                jobService.deleteJob(jobId, user);
                MainLogger.logUserAction(SERVLET_NAME, currentUserId, "DELETE_JOB: " + jobId);
                resp.sendRedirect(req.getContextPath() + "/company/listings?success=Job deleted successfully");
                
            } catch (Exception e) {
                MainLogger.logAuthenticationError(SERVLET_NAME, "DELETE_JOB", e.getMessage(), currentUserId);
                resp.sendRedirect(req.getContextPath() + "/company/listings?error=Failed to delete job");
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/company/listings?error=Invalid action");
        }
    }
}
