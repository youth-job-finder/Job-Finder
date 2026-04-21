package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.entity.Application;
import com.jakartaee.jobfinder.entity.User;
import com.jakartaee.jobfinder.services.ApplicationService;
import com.jakartaee.jobfinder.services.AuthService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet for displaying applicant job applications.
 * Shows all applications submitted by the authenticated applicant.
 */
@WebServlet("/applicant/applications")
public class ApplicantApplicationsServlet extends HttpServlet {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private AuthService authService;

    /**
     * Displays all job applications for the authenticated applicant.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response for rendering the applications view
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = authService.requireApplicant(req, resp, "ApplicantApplicationsServlet");
        if (user == null) {
            return;
        }

        // Get user's applications using ApplicationService
        List<Application> applications = applicationService.getApplicationsByApplicant(user);
        req.setAttribute("applications", applications);

        // Set authentication attributes for navbar
        req.setAttribute("isAuthenticated", true);
        req.setAttribute("userRole", "APPLICANT");

        req.getRequestDispatcher("/views/applicant/applications.jsp").forward(req, resp);
    }
}
