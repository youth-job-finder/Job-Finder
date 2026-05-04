package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.models.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import com.jakartaee.jobfinder.services.AuthService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet for handling user login functionality.
 * Authenticates users and redirects them to their respective dashboards based on role.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String SERVLET_NAME = "LoginServlet";

    @Inject
    private AuthService authService;

    /**
     * Displays the login page.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        MainLogger.logUserAction(SERVLET_NAME, "anonymous", "VIEW_LOGIN_PAGE");
        req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
    }

    /**
     * Processes login form submission and authenticates the user.
     *
     * @param req  the HTTP request containing email and password
     * @param resp the HTTP response for redirecting after authentication
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        MainLogger.logUserAction(SERVLET_NAME, email, "LOGIN_ATTEMPT");

        AuthService.AuthResult result = authService.authenticate(email, password);

        if (result.isSuccess()) {
            User user = result.getUser();
            Role role = result.getRole();

            MainLogger.logUserAction(SERVLET_NAME, user.getEmail(), "LOGIN_SUCCESS");
            MainLogger.logUserAction(SERVLET_NAME, user.getEmail(), "LOGIN_FORWARD_TO_DASHBOARD");

            // Get session data from AuthService
            AuthService.UserSessionData sessionData = authService.getUserSessionData(user, role);

            // Set session attributes
            req.getSession().setAttribute("userId", sessionData.getUserId());
            req.getSession().setAttribute("role", sessionData.getRole());
            req.setAttribute("userId", sessionData.getUserId());

            // Set role-specific session attributes
            if (role == Role.APPLICANT) {
                req.getSession().setAttribute("userName", sessionData.getUserName());
                req.getSession().setAttribute("userEmail", sessionData.getUserEmail());
                req.getSession().setAttribute("emailVerified", sessionData.getEmailVerified());
            } else if (role == Role.COMPANY_ADMIN) {
                req.getSession().setAttribute("companyId", sessionData.getCompanyId());
                req.getSession().setAttribute("companyName", sessionData.getCompanyName());
                req.getSession().setAttribute("companyEmail", sessionData.getCompanyEmail());
                req.getSession().setAttribute("registrationNumber", sessionData.getRegistrationNumber());
            }

            String target = authService.getRedirectUrl(role);
            resp.sendRedirect(req.getContextPath() + target);

        } else if (result.isUnverified()) {
            MainLogger.logUserAction(SERVLET_NAME, email, "LOGIN_BLOCKED_UNVERIFIED_EMAIL");
            req.setAttribute("error", result.getMessage() + ". Check your inbox or request a new verification email.");
            req.setAttribute("unverifiedEmail", email);
            req.getRequestDispatcher("/views/login.jsp").forward(req, resp);

        } else {
            MainLogger.logUserAction(SERVLET_NAME, email, "LOGIN_FAILED");
            req.setAttribute("error", result.getMessage());
            req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
        }
    }
}