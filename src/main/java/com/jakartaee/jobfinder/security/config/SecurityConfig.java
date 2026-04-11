package com.jakartaee.jobfinder.security.config;

import com.jakartaee.jobfinder.security.utils.BCryptHashAlgorithm;
import jakarta.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.LoginToContinue;
import jakarta.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;

@FormAuthenticationMechanismDefinition(
        loginToContinue = @LoginToContinue(
                loginPage = "/login.jsp",
                errorPage = "/login.jsp?error=true"
        )
)
@DatabaseIdentityStoreDefinition(
        dataSourceLookup = "jdbc/youth_job_finder", // JNDI name of your MySQL datasource
        callerQuery = "SELECT password_hash FROM users WHERE email = ?",
        groupsQuery = "SELECT role FROM users WHERE email = ?",
        hashAlgorithm = BCryptHashAlgorithm.class,
        priority = 10
)
public class SecurityConfig {
}
