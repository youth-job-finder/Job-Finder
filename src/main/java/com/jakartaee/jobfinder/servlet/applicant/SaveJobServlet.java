package com.jakartaee.jobfinder.servlet.applicant;

import com.jakartaee.jobfinder.dao.JobDAO;
import com.jakartaee.jobfinder.dao.SavedJobDAO;
import com.jakartaee.jobfinder.models.Job;
import com.jakartaee.jobfinder.models.SavedJob;
import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.AuthService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet for saving and unsaving jobs by applicants.
 * Handles POST requests to save or unsave jobs.
 */
@WebServlet(urlPatterns = {"/applicant/save-job", "/applicant/unsave-job"})
public class SaveJobServlet extends HttpServlet {

    private static final String SERVLET_NAME = "SaveJobServlet";

    @Inject
    private AuthService authService;

    @Inject
    private JobDAO jobDAO;

    @Inject
    private SavedJobDAO savedJobDAO;

    /**
     * Processes save/unsave job requests.
     * Validates the job exists and performs the requested action.
     *
     * @param req  the HTTP request containing the jobId parameter
     * @param resp the HTTP response for sending JSON result
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = authService.requireApplicant(req, resp, SERVLET_NAME);
        if (user == null) {
            return;
        }

        String currentUserId = user.getId();
        String jobId = req.getParameter("jobId");
        if (jobId == null || jobId.trim().isEmpty()) {
            MainLogger.logError(SERVLET_NAME, "Save/unsave job request without jobId by user: " + currentUserId);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Job ID is required");
            return;
        }

        String path = req.getServletPath();
        boolean isSave = path.equals("/applicant/save-job");

        MainLogger.logInfo(SERVLET_NAME, (isSave ? "Save" : "Unsave") + " job request - user: " + currentUserId + ", job: " + jobId);

        try {
            Job job = jobDAO.findById(jobId);
            if (job == null) {
                MainLogger.logError(SERVLET_NAME, "Job not found for jobId: " + jobId + ", requested by user: " + currentUserId);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Job not found");
                return;
            }

            if (isSave) {
                // Check if already saved
                if (savedJobDAO.isJobSavedByApplicant(user, job)) {
                    // Job already saved, return success (idempotent)
                    MainLogger.logInfo(SERVLET_NAME, "Job already saved - user: " + currentUserId + ", job: " + jobId);
                    sendJsonResponse(resp, true, "Job is already saved");
                    return;
                }

                // Save the job
                SavedJob savedJob = new SavedJob(user, job);
                savedJobDAO.create(savedJob);
                MainLogger.logUserAction(SERVLET_NAME, currentUserId, "SAVE_JOB: " + jobId);
                MainLogger.logInfo(SERVLET_NAME, "Job saved successfully - user: " + currentUserId + ", job: " + jobId + ", company: " + (job.getCompany() != null ? job.getCompany().getName() : "unknown"));
                sendJsonResponse(resp, true, "Job saved successfully");
            } else {
                // Unsave the job
                savedJobDAO.deleteByApplicantAndJob(user, job);
                MainLogger.logUserAction(SERVLET_NAME, currentUserId, "UNSAVE_JOB: " + jobId);
                MainLogger.logInfo(SERVLET_NAME, "Job unsaved - user: " + currentUserId + ", job: " + jobId);
                sendJsonResponse(resp, true, "Job removed from saved");
            }

        } catch (Exception e) {
            MainLogger.logError(SERVLET_NAME, "Failed to " + (isSave ? "save" : "unsave") + " job - user: " + currentUserId + ", job: " + jobId, e);
            sendJsonResponse(resp, false, "Failed to process request. Please try again.");
        }
    }

    /**
     * Sends a JSON response to the client.
     *
     * @param resp    the HTTP response
     * @param success whether the operation succeeded
     * @param message the response message
     * @throws IOException if an I/O error occurs
     */
    private void sendJsonResponse(HttpServletResponse resp, boolean success, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.print("{\"success\": " + success + ", \"message\": \"" + message.replace("\"", "\\\"") + "\"}");
        out.flush();
    }
}
