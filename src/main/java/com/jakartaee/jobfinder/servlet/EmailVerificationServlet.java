package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.services.UserService;
import com.jakartaee.jobfinder.logging.MainLogger;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/verify-email")
public class EmailVerificationServlet extends HttpServlet {

    private static final String SERVLET_NAME = "EmailVerificationServlet";

    @Inject
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String token = req.getParameter("token");

        MainLogger.logUserAction(SERVLET_NAME, "anonymous", "EMAIL_VERIFICATION_ATTEMPT");

        boolean verificationSuccess = false;
        String errorMessage = null;

        if (token == null || token.trim().isEmpty()) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "VERIFY_EMAIL", "Missing verification token", "anonymous");
            errorMessage = "Invalid verification link.";
        } else {
            try {
                verificationSuccess = userService.verifyEmail(token.trim());

                if (verificationSuccess) {
                    MainLogger.logServiceOperation(SERVLET_NAME, "EMAIL_VERIFICATION", true,
                            "Token: " + token.substring(0, Math.min(8, token.length())) + "...");
                } else {
                    MainLogger.logAuthenticationError(SERVLET_NAME, "VERIFY_EMAIL", "Verification failed",
                            "Token: " + token.substring(0, Math.min(8, token.length())) + "...");
                    errorMessage = "Invalid or expired verification link. Please request a new verification email.";
                }
            } catch (Exception e) {
                MainLogger.logAuthenticationError(SERVLET_NAME, "VERIFY_EMAIL", e.getMessage(),
                        "Token: " + token.substring(0, Math.min(8, token.length())) + "...");
                errorMessage = "An error occurred during email verification. Please try again.";
            }
        }

        // Attributes expected by JSP
        req.setAttribute("verificationSuccess", verificationSuccess);
        req.setAttribute("errorMessage", errorMessage);

        // Forward to JSP (adjust path if your JSP is under /views/auth/)
        req.getRequestDispatcher("/views/verification-result.jsp").forward(req, resp);
    }
}
