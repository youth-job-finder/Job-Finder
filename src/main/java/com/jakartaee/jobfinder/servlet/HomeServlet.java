package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.logging.BusinessLogger;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    
    private static final String SERVLET_NAME = "HomeServlet";

    public void init() {
        BusinessLogger.logOperation(SERVLET_NAME, "INIT", "system", "HomeServlet initialized");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String user = request.getRemoteUser() != null ? request.getRemoteUser() : "anonymous";
        boolean isAuth = request.getRemoteUser() != null;
        
        BusinessLogger.logPageView(SERVLET_NAME, "index.jsp", user, isAuth);
        
        try {
            String userId = (String) request.getSession().getAttribute("userId");

            if (userId == null) {
                String remoteUser = request.getRemoteUser();
                if (remoteUser != null) {
                    userId = remoteUser;
                    request.getSession().setAttribute("userId", userId);

                    if (request.isUserInRole("APPLICANT")) {
                        request.getSession().setAttribute("role", "APPLICANT");
                    } else if (request.isUserInRole("COMPANY_ADMIN")) {
                        request.getSession().setAttribute("role", "COMPANY_ADMIN");
                    } else if (request.isUserInRole("SYSTEM_ADMIN")) {
                        request.getSession().setAttribute("role", "SYSTEM_ADMIN");
                    }
                }
            }

            boolean isAuthenticated = userId != null;
            request.setAttribute("isAuthenticated", isAuthenticated);

            if (isAuthenticated) {
                Object roleObj = request.getSession().getAttribute("role");
                String userRole = roleObj != null ? roleObj.toString() : "";
                request.setAttribute("userRole", userRole);
            }

            request.getRequestDispatcher("/views/index.jsp").forward(request, response);
        } catch (Exception e) {
            BusinessLogger.logError(SERVLET_NAME, "VIEW_HOME", 
                e.getMessage(), user);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load home page");
        }
    }
}
