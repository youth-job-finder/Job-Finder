package com.jakartaee.jobfinder.servlet;

import com.jakartaee.jobfinder.dao.CVDAO;
import com.jakartaee.jobfinder.dao.UserDAO;
import com.jakartaee.jobfinder.models.CV;
import com.jakartaee.jobfinder.models.User;
import com.jakartaee.jobfinder.models.role.Role;
import com.jakartaee.jobfinder.logging.MainLogger;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/applicant/cv/download")
public class CVDownloadServlet extends HttpServlet {

    private static final String SERVLET_NAME = "CVDownloadServlet";

    @Inject
    private UserDAO userDAO;

    @Inject
    private CVDAO cvDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Enforce authentication - no redirects, return 401 if not authenticated
        String userId = (String) req.getSession().getAttribute("userId");
        if (userId == null && req.getUserPrincipal() != null) {
            userId = req.getUserPrincipal().getName();
            req.getSession().setAttribute("userId", userId);
        }

        if (userId == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        // Check authorization - must have APPLICANT role (from database)
        User user = userDAO.findById(userId);
        if (user == null || user.getRole() != Role.APPLICANT) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied - Applicant role required");
            return;
        }

        // Get the user's CV
        CV cv = cvDAO.findByUser(user).orElse(null);
        if (cv == null || cv.getFilePath() == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No CV found for this user");
            return;
        }

        File file = new File(cv.getFilePath());
        if (!file.exists()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "CV file not found on server");
            return;
        }

        // Log the download action
        MainLogger.logUserAction(SERVLET_NAME, userId, "DOWNLOAD_CV");

        // Set response headers for file download
        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + cv.getFileName() + "\"");
        resp.setContentLengthLong(file.length());

        // Stream the file to response
        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = resp.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }

        MainLogger.logServiceOperation(SERVLET_NAME, "DOWNLOAD_CV", true, "File: " + cv.getFileName());
    }
}
