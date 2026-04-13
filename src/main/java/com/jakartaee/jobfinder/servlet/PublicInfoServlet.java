package com.jakartaee.jobfinder.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/about", "/contact", "/faq", "/privacy", "/terms"})
public class PublicInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = (String) req.getSession().getAttribute("userId");
        if (userId == null) {
            String remoteUser = req.getRemoteUser();
            if (remoteUser != null) {
                userId = remoteUser;
                req.getSession().setAttribute("userId", userId);

                if (req.isUserInRole("APPLICANT")) {
                    req.getSession().setAttribute("role", "APPLICANT");
                } else if (req.isUserInRole("COMPANY_ADMIN")) {
                    req.getSession().setAttribute("role", "COMPANY_ADMIN");
                } else if (req.isUserInRole("SYSTEM_ADMIN")) {
                    req.getSession().setAttribute("role", "SYSTEM_ADMIN");
                }
            }
        }

        boolean isAuthenticated = userId != null;
        req.setAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            Object roleObj = req.getSession().getAttribute("role");
            req.setAttribute("userRole", roleObj != null ? roleObj.toString() : "");
        }

        String path = req.getServletPath();

        switch (path) {
            case "/about" -> {
                req.setAttribute("pageTitle", "About JobFinder");
                req.setAttribute("pageSubtitle", "Connecting students and graduates with verified jobs and internships.");
                req.setAttribute("pageIcon", "fa-compass");
                req.setAttribute("pageLabel", "Our Mission");
                req.setAttribute("sections", List.of(
                        new InfoSection("What We Do", "JobFinder helps young professionals discover trusted opportunities, explore companies, and manage applications in one place.", "fa-briefcase"),
                        new InfoSection("Why It Matters", "We focus on legitimacy, accessibility, and a smoother path from learning to employment.", "fa-shield-heart"),
                        new InfoSection("Who We Serve", "Applicants, employers, and platform administrators each get tools tailored to their part in the hiring journey.", "fa-users")
                ));
            }
            case "/contact" -> {
                req.setAttribute("pageTitle", "Contact Us");
                req.setAttribute("pageSubtitle", "Reach the JobFinder team for support, partnerships, or general questions.");
                req.setAttribute("pageIcon", "fa-envelope-open-text");
                req.setAttribute("pageLabel", "Support Channels");
                req.setAttribute("sections", List.of(
                        new InfoSection("Email", "info@jobfinder.com", "fa-envelope"),
                        new InfoSection("Phone", "+27 (72) 623-4567", "fa-phone"),
                        new InfoSection("Office", "Cnr R40 and D725 Roads, Mbombela, 1200", "fa-location-dot")
                ));
            }
            case "/faq" -> {
                req.setAttribute("pageTitle", "Frequently Asked Questions");
                req.setAttribute("pageSubtitle", "Quick answers to common questions from applicants and employers.");
                req.setAttribute("pageIcon", "fa-circle-question");
                req.setAttribute("pageLabel", "Quick Answers");
                req.setAttribute("sections", List.of(
                        new InfoSection("How do I apply for jobs?", "Create an account, complete your profile, upload your CV, and use the jobs page to apply.", "fa-file-signature"),
                        new InfoSection("Do companies get verified?", "Yes. Company registrations go through a vetting and approval process before they can post opportunities.", "fa-badge-check"),
                        new InfoSection("Can I save jobs for later?", "Yes. Signed-in applicants can bookmark jobs and revisit them from the saved jobs filter.", "fa-bookmark")
                ));
            }
            case "/privacy" -> {
                req.setAttribute("pageTitle", "Privacy Policy");
                req.setAttribute("pageSubtitle", "How we handle your account, profile, and application information.");
                req.setAttribute("pageIcon", "fa-user-shield");
                req.setAttribute("pageLabel", "Data & Privacy");
                req.setAttribute("sections", List.of(
                        new InfoSection("Data We Collect", "We store account details, profile information, application activity, and platform usage needed to provide the service.", "fa-database"),
                        new InfoSection("How We Use It", "Your data is used to authenticate you, personalize your experience, and support applications and employer workflows.", "fa-gears"),
                        new InfoSection("Your Control", "You can review and update your profile information from your account area.", "fa-sliders")
                ));
            }
            case "/terms" -> {
                req.setAttribute("pageTitle", "Terms of Service");
                req.setAttribute("pageSubtitle", "The basic rules for using JobFinder responsibly and safely.");
                req.setAttribute("pageIcon", "fa-scale-balanced");
                req.setAttribute("pageLabel", "Platform Rules");
                req.setAttribute("sections", List.of(
                        new InfoSection("Acceptable Use", "Users must provide accurate information and may not use the platform for fraud, harassment, or misleading content.", "fa-handshake-angle"),
                        new InfoSection("Employer Responsibilities", "Employers are expected to post legitimate opportunities and communicate fairly with applicants.", "fa-building-shield"),
                        new InfoSection("Platform Rights", "JobFinder may moderate content, suspend abusive accounts, and improve the service over time.", "fa-gavel")
                ));
            }
            default -> {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        req.getRequestDispatcher("/views/public-info.jsp").forward(req, resp);
    }

    public static class InfoSection {
        private final String heading;
        private final String body;
        private final String icon;

        public InfoSection(String heading, String body, String icon) {
            this.heading = heading;
            this.body = body;
            this.icon = icon;
        }

        public String getHeading() {
            return heading;
        }

        public String getBody() {
            return body;
        }

        public String getIcon() {
            return icon;
        }
    }
}
