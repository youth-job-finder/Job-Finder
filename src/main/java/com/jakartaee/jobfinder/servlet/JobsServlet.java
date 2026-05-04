package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.dao.SavedJobDAO;
import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.models.Job;
import com.jakartaee.jobfinder.models.SavedJob;
import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.dto.PaginationDTO;
import com.jakartaee.jobfinder.logging.BusinessLogger;
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
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet("/jobs")
public class JobsServlet extends HttpServlet {

    private static final String SERVLET_NAME = "JobsServlet";

    @Inject
    private JobService jobService;

    @Inject
    private UserDAO userDAO;

    @Inject
    private SavedJobDAO savedJobDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String remoteUser = req.getRemoteUser();
        String username = remoteUser != null ? remoteUser : "anonymous";
        boolean isAuthenticated = remoteUser != null;

        BusinessLogger.logPageView(SERVLET_NAME, "jobs.jsp", username, isAuthenticated);

        try {
            // Check authentication status for navbar/footer selection
            String userId = (String) req.getSession().getAttribute("userId");

            // Also check container authentication (for users who logged in via form auth)
            if (userId == null) {
                remoteUser = req.getRemoteUser();
                if (remoteUser != null) {
                    userId = remoteUser;
                    req.getSession().setAttribute("userId", userId);
                    // Try to get role from user principal
                    if (req.isUserInRole("APPLICANT")) {
                        req.getSession().setAttribute("role", "APPLICANT");
                    } else if (req.isUserInRole("COMPANY_ADMIN")) {
                        req.getSession().setAttribute("role", "COMPANY_ADMIN");
                    } else if (req.isUserInRole("SYSTEM_ADMIN")) {
                        req.getSession().setAttribute("role", "SYSTEM_ADMIN");
                    }
                }
            }

            isAuthenticated = userId != null;
            req.setAttribute("isAuthenticated", isAuthenticated);

            String userRole = "";
            if (isAuthenticated) {
                Object roleObj = req.getSession().getAttribute("role");
                userRole = roleObj != null ? roleObj.toString() : "";
                req.setAttribute("userRole", userRole);
            }

            // Handle filter + optional search (q)
            String filter = req.getParameter("filter");
            String searchQuery = req.getParameter("q");
            if (searchQuery != null) {
                searchQuery = searchQuery.trim();
            }
            boolean hasSearch = searchQuery != null && !searchQuery.isEmpty();
            req.setAttribute("searchQuery", hasSearch ? searchQuery : "");

            List<Job> jobs;

            if ("saved".equals(filter) && isAuthenticated && "APPLICANT".equals(userRole)) {
                User user = userDAO.findById(userId);
                if (user != null) {
                    List<SavedJob> savedJobs = savedJobDAO.findByApplicant(user);
                    jobs = savedJobs.stream()
                            .map(SavedJob::getJob)
                            .collect(Collectors.toList());
                    if (hasSearch) {
                        String needle = searchQuery.toLowerCase(Locale.ROOT);
                        jobs = jobs.stream()
                                .filter(j -> matchesJobSearch(j, needle))
                                .collect(Collectors.toList());
                    }
                } else {
                    jobs = List.of();
                }
            } else if ("remote".equals(filter)) {
                jobs = hasSearch ? jobService.searchRemoteJobs(searchQuery) : jobService.getRemoteJobs();
            } else {
                jobs = hasSearch ? jobService.searchJobs(searchQuery) : jobService.getAllJobs();
            }

            // Pagination
            int page = PaginationUtil.parsePageParameter(req.getParameter("page"));
            PaginationDTO<Job> pagination = PaginationUtil.paginate(jobs, page, 9);
            
            BusinessLogger.logDataAccess(SERVLET_NAME, "Job", pagination.getTotalItems(), username);

            req.setAttribute("jobs", pagination.getItems());
            req.setAttribute("pagination", pagination);

            // If applicant is logged in, get their saved job IDs for the save/unsave buttons
            if (isAuthenticated && "APPLICANT".equals(userRole)) {
                User user = userDAO.findById(userId);
                if (user != null) {
                    List<SavedJob> savedJobs = savedJobDAO.findByApplicant(user);
                    Set<String> savedJobIds = savedJobs.stream()
                            .map(savedJob -> savedJob.getJob().getId())
                            .collect(Collectors.toSet());
                    req.setAttribute("savedJobIds", savedJobIds);
                }
            }

            req.getRequestDispatcher("/views/jobs.jsp").forward(req, resp);

        } catch (Exception e) {
            BusinessLogger.logError(SERVLET_NAME, "LOAD_JOBS",
                    e.getMessage(), username);

            req.setAttribute("jobs", List.of());
            req.setAttribute("searchQuery", "");
            req.setAttribute("error", "Unable to load jobs at this time. Please try again later.");
            req.getRequestDispatcher("/views/jobs.jsp").forward(req, resp);
        }
    }

    private static boolean matchesJobSearch(Job job, String needle) {
        if (job.getJobTitle() != null && job.getJobTitle().toLowerCase(Locale.ROOT).contains(needle)) {
            return true;
        }
        if (job.getJobDescription() != null && job.getJobDescription().toLowerCase(Locale.ROOT).contains(needle)) {
            return true;
        }
        if (job.getJobRequirements() != null && job.getJobRequirements().toLowerCase(Locale.ROOT).contains(needle)) {
            return true;
        }
        if (job.getPhysicalAddress() != null && job.getPhysicalAddress().toLowerCase(Locale.ROOT).contains(needle)) {
            return true;
        }
        if (job.getSalaryRange() != null && job.getSalaryRange().toLowerCase(Locale.ROOT).contains(needle)) {
            return true;
        }
        if (job.getJobType() != null && job.getJobType().toLowerCase(Locale.ROOT).contains(needle)) {
            return true;
        }
        return job.getCompany() != null
                && job.getCompany().getName() != null
                && job.getCompany().getName().toLowerCase(Locale.ROOT).contains(needle);
    }
}
