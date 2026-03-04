# 📦 Database Dev Team Overview

The **Database Dev Team** is responsible for designing, implementing, and maintaining the data layer of our Jakarta EE web application.  
We use **MySQL** as our relational database and **JPA (Java Persistence API)** for object‑relational mapping.

---

## 🔧 Responsibilities
- Define and evolve the **MySQL schema** to support application features.
- Create and maintain **JPA entity classes** that map Java objects to database tables.
- Manage **relationships** (e.g., `@OneToMany`, `@ManyToOne`, `@ManyToMany`) and **constraints**.
- Write and review **data migration scripts** and **initial seed data**.
- Ensure **data integrity**, **performance**, and **security** across environments.
- Collaborate with backend developers to align entity models with business logic.
- Document schema changes and entity updates for onboarding and future maintenance.
- Monitor database performance and optimize queries.
- Handle backup, restore, and disaster recovery strategies.

---

## 🧪 Technologies Used

| Layer         | Technology              |
|---------------|-------------------------|
| Database      | MySQL                   |
| ORM           | JPA (Jakarta Persistence) |
| Framework     | Jakarta EE              |
| Tools         | Hibernate, Flyway (optional), GitHub |
| Testing       | JUnit, Testcontainers   |

---

## 🔄 Workflow

1. **Create a feature branch**  
   Example: `feature/student-entity`

2. **Define JPA entity**
   ```java
   @Entity
   public class Student {
     @Id
     @GeneratedValue
     private Long id;

     private String name;
     private String email;

     @OneToMany(mappedBy = "student")
     private List<Application> applications;
   }