package com.jakartaee.jobfinder.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.Serializable;
import java.util.Properties;

/**
 * Service for sending email notifications throughout the JobFinder application.
 * Provides methods for sending various types of emails including verification,
 * password reset, application notifications, and company approval/rejection emails.
 */
@ApplicationScoped
public class EmailService implements Serializable {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    /**
     * SMTP username loaded from environment variable or fallback to default.
     */
    private static final String SMTP_USERNAME =
            System.getenv("SMTP_USERNAME") != null ? System.getenv("SMTP_USERNAME") : "aubreymapaile@gmail.com";

    /**
     * SMTP password loaded from environment variable or fallback to default.
     */
    private static final String SMTP_PASSWORD =
            System.getenv("SMTP_PASSWORD") != null ? System.getenv("SMTP_PASSWORD") : "gend iwex accw gopn";

    /**
     * From email address used for sending emails.
     */
    private static final String FROM_EMAIL = SMTP_USERNAME;

    /**
     * From name displayed in sent emails.
     */
    private static final String FROM_NAME = "JobFinder";

    /**
     * Sends a verification email to a user.
     *
     * @param toEmail           the recipient's email address
     * @param userName          the user's name
     * @param verificationToken the verification token
     * @param baseUrl           the base URL of the application
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendVerificationEmail(String toEmail, String userName, String verificationToken, String baseUrl) {
        try {
            Session session = createSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Verify Your JobFinder Account");

            String verificationUrl = baseUrl + "/verify-email?token=" + verificationToken;
            String emailContent = buildVerificationEmailContent(userName, verificationUrl);

            message.setContent(emailContent, "text/html");
            Transport.send(message);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sends a password reset email to a user.
     *
     * @param toEmail    the recipient's email address
     * @param userName   the user's name
     * @param resetToken the password reset token
     * @param baseUrl    the base URL of the application
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendPasswordResetEmail(String toEmail, String userName, String resetToken, String baseUrl) {
        try {
            Session session = createSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Reset Your JobFinder Password");

            String resetUrl = baseUrl + "/reset-password?token=" + resetToken;
            String emailContent = buildPasswordResetEmailContent(userName, resetUrl);

            message.setContent(emailContent, "text/html");
            Transport.send(message);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sends an email notification when an applicant is shortlisted.
     *
     * @param toEmail       the recipient's email address
     * @param applicantName the applicant's name
     * @param jobTitle      the job title
     * @param companyName   the company name
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendApplicationShortlistedEmail(String toEmail, String applicantName, String jobTitle, String companyName) {
        try {
            Session session = createSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Congratulations! You've Been Shortlisted - JobFinder");

            String emailContent = buildApplicationShortlistedEmailContent(applicantName, jobTitle, companyName);

            message.setContent(emailContent, "text/html");
            Transport.send(message);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendCompanyApprovalEmail(String toEmail, String userName, String companyName, String baseUrl) {
        try {
            Session session = createSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Your Company Has Been Approved - JobFinder");

            String loginUrl = baseUrl + "/login";
            String emailContent = buildCompanyApprovalEmailContent(userName, companyName, loginUrl);

            message.setContent(emailContent, "text/html");
            Transport.send(message);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendCompanyRejectionEmail(String toEmail, String userName, String companyName, String reason) {
        try {
            Session session = createSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Company Registration Update - JobFinder");

            String emailContent = buildCompanyRejectionEmailContent(userName, companyName, reason);

            message.setContent(emailContent, "text/html");
            Transport.send(message);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sends an email notification to an applicant confirming their job application submission.
     *
     * @param toEmail       the applicant's email address
     * @param applicantName the applicant's name
     * @param jobTitle      the job title applied for
     * @param companyName   the company name
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendApplicationSubmittedEmail(String toEmail, String applicantName, String jobTitle, String companyName) {
        try {
            Session session = createSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Application Submitted - " + jobTitle);

            String emailContent = buildApplicationSubmittedEmailContent(applicantName, jobTitle, companyName);

            message.setContent(emailContent, "text/html");
            Transport.send(message);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sends an email notification to the company admin about a new job application.
     *
     * @param toEmail       the company admin's email address
     * @param applicantName the applicant's name
     * @param jobTitle      the job title
     * @param companyName   the company name
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendNewApplicationNotificationEmail(String toEmail, String applicantName, String jobTitle, String companyName) {
        try {
            Session session = createSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("New Job Application - " + jobTitle);

            String emailContent = buildNewApplicationNotificationEmailContent(applicantName, jobTitle, companyName);

            message.setContent(emailContent, "text/html");
            Transport.send(message);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });
    }

    private String buildVerificationEmailContent(String userName, String verificationUrl) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Verify Your JobFinder Account</title>" +
                "<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}" +
                ".container{max-width:600px;margin:0 auto;padding:20px;}" +
                ".header{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:white;padding:30px;text-align:center;border-radius:10px 10px 0 0;}" +
                ".content{background:#f9f9f9;padding:30px;border-radius:0 0 10px 10px;}" +
                ".button{display:inline-block;background:#667eea;color:white;padding:15px 30px;text-decoration:none;border-radius:5px;margin:20px 0;}" +
                ".footer{text-align:center;color:#666;margin-top:30px;font-size:12px;}</style></head>" +
                "<body><div class='container'><div class='header'><h1>Welcome to JobFinder, " + userName + "!</h1>" +
                "<p>Your journey to amazing career opportunities starts here</p></div>" +
                "<div class='content'><h2>Verify Your Email Address</h2>" +
                "<p>Thank you for signing up! Please verify your email by clicking below:</p>" +
                "<a href='" + verificationUrl + "' class='button'>Verify Email Address</a>" +
                "<p><strong>Or copy and paste this link:</strong></p>" +
                "<p style='word-break:break-all;background:#e9ecef;padding:10px;border-radius:5px;'>" + verificationUrl + "</p>" +
                "<p><strong>This link expires in 24 hours.</strong></p>" +
                "</div><div class='footer'><p>&copy; 2026 JobFinder. All rights reserved.</p>" +
                "<p>This is an automated message. Do not reply.</p></div></div></body></html>";
    }

    private String buildPasswordResetEmailContent(String userName, String resetUrl) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Reset Your JobFinder Password</title>" +
                "<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}" +
                ".container{max-width:600px;margin:0 auto;padding:20px;}" +
                ".header{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:white;padding:30px;text-align:center;border-radius:10px 10px 0 0;}" +
                ".content{background:#f9f9f9;padding:30px;border-radius:0 0 10px 10px;}" +
                ".button{display:inline-block;background:#667eea;color:white;padding:15px 30px;text-decoration:none;border-radius:5px;margin:20px 0;}" +
                ".footer{text-align:center;color:#666;margin-top:30px;font-size:12px;}</style></head>" +
                "<body><div class='container'><div class='header'><h1>Password Reset Request</h1>" +
                "<p>JobFinder Account Security</p></div>" +
                "<div class='content'><h2>Reset Your Password</h2>" +
                "<p>Hi " + userName + ",</p>" +
                "<p>We received a request to reset your password. Click below to create a new password:</p>" +
                "<a href='" + resetUrl + "' class='button'>Reset Password</a>" +
                "<p><strong>Or copy and paste this link:</strong></p>" +
                "<p style='word-break:break-all;background:#e9ecef;padding:10px;border-radius:5px;'>" + resetUrl + "</p>" +
                "<p><strong>This link expires in 1 hour.</strong></p>" +
                "<p style='margin-top:20px;font-size:12px;color:#666;'>If you didn't request this password reset, you can safely ignore this email.</p>" +
                "</div><div class='footer'><p>&copy; 2026 JobFinder. All rights reserved.</p>" +
                "<p>This is an automated message. Do not reply.</p></div></div></body></html>";
    }

    private String buildCompanyApprovalEmailContent(String userName, String companyName, String loginUrl) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Company Approved - JobFinder</title>" +
                "<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}" +
                ".container{max-width:600px;margin:0 auto;padding:20px;}" +
                ".header{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:white;padding:30px;text-align:center;border-radius:10px 10px 0 0;}" +
                ".content{background:#f9f9f9;padding:30px;border-radius:0 0 10px 10px;}" +
                ".button{display:inline-block;background:#667eea;color:white;padding:15px 30px;text-decoration:none;border-radius:5px;margin:20px 0;}" +
                ".footer{text-align:center;color:#666;margin-top:30px;font-size:12px;}</style></head>" +
                "<body><div class='container'><div class='header'><h1>Congratulations, " + userName + "!</h1>" +
                "<p>Your Company Has Been Approved</p></div>" +
                "<div class='content'><h2>Welcome to JobFinder for Employers</h2>" +
                "<p>We're pleased to inform you that <strong>" + companyName + "</strong> has been approved and is now active on JobFinder.</p>" +
                "<p>You can now log in to your company dashboard to post jobs, manage applications, and connect with talented candidates.</p>" +
                "<a href='" + loginUrl + "' class='button'>Log In to Dashboard</a>" +
                "<p><strong>What's next?</strong></p>" +
                "<ul><li>Complete your company profile</li><li>Post your first job listing</li><li>Start receiving applications</li></ul>" +
                "</div><div class='footer'><p>&copy; 2026 JobFinder. All rights reserved.</p>" +
                "<p>This is an automated message. Do not reply.</p></div></div></body></html>";
    }

    private String buildApplicationShortlistedEmailContent(String applicantName, String jobTitle, String companyName) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Congratulations - JobFinder</title>" +
                "<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}" +
                ".container{max-width:600px;margin:0 auto;padding:20px;}" +
                ".header{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:white;padding:30px;text-align:center;border-radius:10px 10px 0 0;}" +
                ".content{background:#f9f9f9;padding:30px;border-radius:0 0 10px 10px;}" +
                ".button{display:inline-block;background:#667eea;color:white;padding:15px 30px;text-decoration:none;border-radius:5px;margin:20px 0;}" +
                ".footer{text-align:center;color:#666;margin-top:30px;font-size:12px;}</style></head>" +
                "<body><div class='container'><div class='header'><h1>Congratulations, " + applicantName + "!</h1>" +
                "<p>You've Been Shortlisted</p></div>" +
                "<div class='content'><h2>Great News!</h2>" +
                "<p>We're excited to inform you that you've been <strong>shortlisted</strong> for the position of <strong>" + jobTitle + "</strong> at <strong>" + companyName + "</strong>.</p>" +
                "<p>Your application has impressed the hiring team, and they would like to proceed with the next steps of the recruitment process.</p>" +
                "<div style='background:#e8f5e9;padding:20px;border-radius:5px;margin:20px 0;'>" +
                "<h3>What Happens Next?</h3>" +
                "<ul><li>The company will contact you directly regarding interview scheduling</li>" +
                "<li>Prepare for potential technical or behavioral interviews</li>" +
                "<li>Keep your profile and contact information up to date</li></ul></div>" +
                "<p><strong>Important:</strong> Please watch for communication from the company regarding interview details.</p>" +
                "<p>Best of luck with your interview!</p>" +
                "</div><div class='footer'><p>&copy; 2026 JobFinder. All rights reserved.</p>" +
                "<p>This is an automated message. Do not reply.</p></div></div></body></html>";
    }

    private String buildCompanyRejectionEmailContent(String userName, String companyName, String reason) {
        String reasonSection = (reason != null && !reason.isEmpty()) 
            ? "<p><strong>Reason:</strong> " + reason + "</p>" 
            : "<p>If you believe this is an error, please contact our support team for assistance.</p>";
        
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Company Registration Update - JobFinder</title>" +
                "<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}" +
                ".container{max-width:600px;margin:0 auto;padding:20px;}" +
                ".header{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:white;padding:30px;text-align:center;border-radius:10px 10px 0 0;}" +
                ".content{background:#f9f9f9;padding:30px;border-radius:0 0 10px 10px;}" +
                ".footer{text-align:center;color:#666;margin-top:30px;font-size:12px;}</style></head>" +
                "<body><div class='container'><div class='header'><h1>Registration Update</h1>" +
                "<p>JobFinder Company Registration</p></div>" +
                "<div class='content'><h2>Company Registration Status</h2>" +
                "<p>Hi " + userName + ",</p>" +
                "<p>We regret to inform you that your company registration for <strong>" + companyName + "</strong> has been declined.</p>" +
                reasonSection +
                "<p>If you have any questions or would like to submit a revised application, please don't hesitate to reach out to our support team.</p>" +
                "<p><strong>JobFinder Support Team</strong></p>" +
                "</div><div class='footer'><p>&copy; 2026 JobFinder. All rights reserved.</p>" +
                "<p>This is an automated message. Do not reply.</p></div></div></body></html>";
    }

    private String buildApplicationSubmittedEmailContent(String applicantName, String jobTitle, String companyName) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Application Submitted - JobFinder</title>" +
                "<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}" +
                ".container{max-width:600px;margin:0 auto;padding:20px;}" +
                ".header{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:white;padding:30px;text-align:center;border-radius:10px 10px 0 0;}" +
                ".content{background:#f9f9f9;padding:30px;border-radius:0 0 10px 10px;}" +
                ".button{display:inline-block;background:#667eea;color:white;padding:15px 30px;text-decoration:none;border-radius:5px;margin:20px 0;}" +
                ".footer{text-align:center;color:#666;margin-top:30px;font-size:12px;}</style></head>" +
                "<body><div class='container'><div class='header'><h1>Application Submitted!</h1>" +
                "<p>Your application has been received</p></div>" +
                "<div class='content'><h2>Application Details</h2>" +
                "<p>Hi " + applicantName + ",</p>" +
                "<p>Your application for the position of <strong>" + jobTitle + "</strong> at <strong>" + companyName + "</strong> has been successfully submitted.</p>" +
                "<p>The hiring team will review your application and contact you if you're shortlisted.</p>" +
                "<p><strong>What's next?</strong></p>" +
                "<ul><li>You can track your application status in your dashboard</li>" +
                "<li>Keep your profile updated</li>" +
                "<li>Watch for email notifications about your application status</li></ul>" +
                "<a href='https://jobfinder.com/applicant/applications' class='button'>View Applications</a>" +
                "</div><div class='footer'><p>&copy; 2026 JobFinder. All rights reserved.</p>" +
                "<p>This is an automated message. Do not reply.</p></div></div></body></html>";
    }

    private String buildNewApplicationNotificationEmailContent(String applicantName, String jobTitle, String companyName) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>New Job Application - JobFinder</title>" +
                "<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;}" +
                ".container{max-width:600px;margin:0 auto;padding:20px;}" +
                ".header{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:white;padding:30px;text-align:center;border-radius:10px 10px 0 0;}" +
                ".content{background:#f9f9f9;padding:30px;border-radius:0 0 10px 10px;}" +
                ".button{display:inline-block;background:#667eea;color:white;padding:15px 30px;text-decoration:none;border-radius:5px;margin:20px 0;}" +
                ".footer{text-align:center;color:#666;margin-top:30px;font-size:12px;}</style></head>" +
                "<body><div class='container'><div class='header'><h1>New Application Received!</h1>" +
                "<p>Someone applied for a position at " + companyName + "</p></div>" +
                "<div class='content'><h2>Application Details</h2>" +
                "<p><strong>Applicant:</strong> " + applicantName + "</p>" +
                "<p><strong>Position:</strong> " + jobTitle + "</p>" +
                "<p><strong>Company:</strong> " + companyName + "</p>" +
                "<p>A new application has been submitted for the position of <strong>" + jobTitle + "</strong>.</p>" +
                "<p>Please log in to your company dashboard to review the application.</p>" +
                "<a href='https://jobfinder.com/company/applicants' class='button'>Review Application</a>" +
                "</div><div class='footer'><p>&copy; 2026 JobFinder. All rights reserved.</p>" +
                "<p>This is an automated message. Do not reply.</p></div></div></body></html>";
    }
}