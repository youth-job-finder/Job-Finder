package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.logging.MainLogger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/logout", "/signout"})
public class LogoutServlet extends HttpServlet {

    private static final String SERVLET_NAME = "LogoutServlet";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String userId = req.getUserPrincipal() != null
                    ? req.getUserPrincipal().getName()
                    : "unknown";

            MainLogger.logUserAction(SERVLET_NAME, userId, "LOGOUT_ATTEMPT");

            // Perform logout: clear authentication and invalidate session
            req.logout();
            req.getSession().invalidate();

            MainLogger.logLogout(userId, userId, "unknown");

            // Redirect back to login page with logout flag
            resp.sendRedirect(req.getContextPath() + "/login?logout=true");

        } catch (Exception e) {
            MainLogger.logAuthenticationError(SERVLET_NAME, "LOGOUT", e.getMessage(), "unknown");
            resp.sendRedirect(req.getContextPath() + "/login?error=logout_failed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        MainLogger.logUserAction(SERVLET_NAME, "anonymous", "LOGOUT_GET_REQUEST");
        doPost(req, resp);
    }
}
