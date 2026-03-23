🚀 MySQL Setup in GlassFish (Jakarta EE 9+)
1. Create Database and User in MySQL Workbench
   Run the following SQL commands:

```sql
-- Create the database
CREATE DATABASE youth_job_finder;

-- Create a dedicated user with password
CREATE USER 'user_name'@'localhost' IDENTIFIED BY 'your_password';

-- Grant privileges to the user on the database
GRANT ALL PRIVILEGES ON youth_job_finder.* TO 'youth_user'@'localhost';

-- Apply changes
FLUSH PRIVILEGES;
```
⚠️ Note: The database name (youth_job_finder) must match the persistence-unit name in your persistence.xml.

2. Place the MySQL Connector JAR(Download the latest version)
   Copy mysql-connector-java-x.x.x.jar into:

text
glassfish/domains/domain1/lib/
Restart GlassFish after placing the JAR.

3. Configure JDBC Connection Pool
   In the GlassFish Admin Console:
```text
Navigate: Resources → JDBC → Connection Pools → New

Settings:

Resource Type: javax.sql.DataSource

Database Vendor: MySQL

Driver Classname: com.mysql.cj.jdbc.Driver

Properties:


user=youth_user
password=youth_pass
URL=jdbc:mysql://localhost:3306/youth_job_finder?useSSL=false&allowPublicKeyRetrieval=true&verifyServerCertificate=false
Save and Ping the pool to test connection.

4. Configure JDBC Resource
   Navigate: Resources → JDBC → JDBC Resources → New

JNDI Name: jdbc/youth_job_finder

Pool Name: the one you created above.

5. Persistence.xml (Jakarta EE 9+)
   Place this inside src/main/resources/META-INF/persistence.xml (or WEB-INF/classes/META-INF/persistence.xml):
```
```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence
                                 https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">

    <persistence-unit name="youth-job-finder" transaction-type="JTA">
        <!-- Reference the JDBC resource you created in the server -->
        <jta-data-source>jdbc/MySQLPool</jta-data-source>

        <!-- List your entity classes (or rely on automatic scanning if your provider supports it) -->
        <class>com.jakartaee.jobfinder.entity.Company</class>
        <class>com.jakartaee.jobfinder.entity.User</class>
        <class>com.jakartaee.jobfinder.entity.Job</class>
        <class>com.jakartaee.jobfinder.entity.Application</class>
        <class>com.jakartaee.jobfinder.entity.Review</class>

        <properties>
            <!-- CRITICAL: Tell JPA to create/drop tables -->
            <property name="jakarta.persistence.schema-generation.database.action" value="drop-and-create"/>

            <!-- Optional: Log the generated SQL to verify -->
            <property name="jakarta.persistence.show_sql" value="true"/>
            <property name="jakarta.persistence.format_sql" value="true"/>

            <!-- If you want to specify the database dialect (helps with schema generation) -->
            <!-- For Hibernate (if it's your provider) -->
            <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL8Dialect"/>
            <!-- For EclipseLink (default in many servers) -->
            <!-- <property name="eclipselink.target-database" value="MySQL"/> -->
        </properties>
    </persistence-unit>
</persistence>
```
🔍 Key Changes from the Old Version:
Namespace: Updated to https://jakarta.ee/xml/ns/persistence with version 3.0.

Properties: All javax.persistence properties changed to jakarta.persistence.

JTA Data Source: Kept as <jta-data-source> (same).

Schema generation: Set to none as required – no automatic table creation.

6. Security & Environment Variables (Optional but Recommended)
   Instead of hard‑coding credentials in persistence.xml, use GlassFish system properties or environment variables. Example:

xml
<property name="jakarta.persistence.jdbc.user" value="${ENV=DB_USER}"/>
<property name="jakarta.persistence.jdbc.password" value="${ENV=DB_PASSWORD}"/>
Then set DB_USER and DB_PASSWORD in your server's environment or domain.xml.

✅ Best Practices
Match names: Ensure persistence-unit name, database name, and JNDI reference are consistent.

Dedicated user: Never use root in production; create a specific DB user with minimal privileges.

Schema management: For production, use none and manage schema via migration tools (Flyway, Liquibase) or manual DDL.

SSL: In production, enable SSL and remove useSSL=false. The current settings are only for local development.

Restart GlassFish after adding JARs or changing pool settings.

⚠️ Common Errors & Solutions
Error	Cause	Solution
No suitable driver found	JAR not in correct directory	Place connector JAR in domain1/lib and restart
Communications link failure	Wrong URL or port	Verify jdbc:mysql://localhost:3306/dbname
Ping failed in JDBC pool	Driver not loaded or credentials wrong	Check driver class com.mysql.cj.jdbc.Driver and credentials
Unknown database	Database name mismatch	Ensure DB name in URL matches the created database
Access denied	Wrong user/password	Verify credentials and privileges
Table not found	Schema not generated	Either set schema-generation to create (dev only) or run DDL scripts
📂 Visual Directory Structure (GlassFish Domain)
text
glassfish/
└── domains/
└── domain1/
├── lib/                         <-- Place MySQL connector JAR here
│   └── mysql-connector-java-8.0.33.jar
├── config/
│   └── domain.xml                <-- Data source definitions stored here
└── applications/
└── your-app.war
└── WEB-INF/
└── classes/
└── META-INF/
└── persistence.xml   <-- Your persistence config
✅ Onboarding Checklist
Database created in MySQL (youth_job_finder)

Dedicated user created (youth_user/youth_pass)

MySQL Connector JAR placed in domain1/lib

GlassFish restarted

JDBC Connection Pool configured and ping successful

JDBC Resource created with JNDI name jdbc/youth_job_finder

persistence.xml updated with Jakarta namespace and properties

persistence.xml contains <jta-data-source> pointing to the correct JNDI name

No schema generation (or set to none) – tables to be created manually

Application deployed and tested