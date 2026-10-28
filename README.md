# Placera — Campus Placement Management System

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.3.4](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Build Status](https://img.shields.io/badge/Build-Passing-success.svg)]()
[![Tests](https://img.shields.io/badge/Tests-19%20Passing%20(100%25)-green.svg)]()
[![License](https://img.shields.io/badge/License-Academic-blue.svg)]()

**Placera** is an enterprise-grade, full-stack Campus Placement Management System engineered with **Spring Boot 3**, **Java 21**, **Spring Data JPA / Hibernate**, **Spring Security**, **Thymeleaf**, and **MySQL**. It digitizes and streamlines university campus recruitment campaigns across students, corporate recruiters, and institutional placement administrators.

---

## 🏛️ Core Object-Oriented Programming (OOP) Principles

Placera is designed as a model implementation of Object-Oriented Programming principles:

1. **Encapsulation & Domain State Machines**:
   - Entities (`JobApplication`, `JobPosting`, `StudentProfile`, `CompanyProfile`) maintain private state and expose high-level domain behaviors (e.g., `application.shortlist()`, `application.select()`, `job.isStudentEligible(student)`).
   - Strict horizontal security prevents unauthorized state transitions across companies or candidate profiles.

2. **Interface Abstraction & Separation of Concerns**:
   - Strict 3-tier layering separating Controllers, Business Service Interfaces, Data Access Repositories, and Entity Data Models:
     - `StudentService` &rarr; `StudentServiceImpl`
     - `CompanyService` &rarr; `CompanyServiceImpl`
     - `AdminService` &rarr; `AdminServiceImpl`
     - `UserService` &rarr; `UserServiceImpl`

3. **Polymorphism & Spring Security Integration**:
   - Unified `UserAccount` entity implements Spring Security credentials, bridged dynamically via `CustomUserDetailsService` to polymorphic role-based access authorities (`ROLE_STUDENT`, `ROLE_COMPANY`, `ROLE_ADMIN`).
   - Dynamic post-login routing via `CustomAuthenticationSuccessHandler` seamlessly navigates each role to their dedicated portal.

4. **Dependency Inversion & SOLID Architecture**:
   - 100% Constructor-based Dependency Injection across all components (no field `@Autowired`), allowing fast, isolated Mockito unit testing without heavy container overhead.

---

## 🚀 Key Portals & Features

### 🎓 1. Student Portal (`/student/*`)
- **Executive Dashboard**: Real-time counter of submitted applications, active interviews, eligibility radar, and application milestones.
- **Academic Profile & Resume**: Manage CGPA, department/branch, graduation year, contact details, skills, and PDF resume upload.
- **Campus Job Explorer**: Filter and search approved corporate openings with instant eligibility validation flags (CGPA cutoffs, eligible branches, application deadlines).
- **Application Tracker**: Detailed progress tracker showing stage progression (`APPLIED` &rarr; `SHORTLISTED` &rarr; `INTERVIEW_SCHEDULED` &rarr; `SELECTED` / `REJECTED`).
- **Interview Timeline**: View upcoming technical and HR interview rounds with datetime schedules, interviewer details, and Google Meet/venue links.

### 🏢 2. Corporate Recruiter Portal (`/company/*`)
- **Accreditation Lifecycle**: Account creation with institutional administrative approval gate (`PENDING` &rarr; `APPROVED` / `REJECTED`).
- **Job Posting Management**: Create, edit, and toggle active status on openings with custom CTC (LPA), cutoff CGPA, and branch requirements.
- **Candidate Screening**: Inspect student applicant rosters, preview submitted PDF resumes directly in-browser, and transition candidate status.
- **Interview Scheduling**: Schedule interview rounds with meeting links, physical locations, and custom preparation notes sent directly to candidate timelines.

### 🛡️ 3. Institutional Administration Portal (`/admin/*`)
- **Analytics & KPIs**: Real-time institutional overview: Total Students, Approved Recruiters, Active Postings, Overall Placement Rate, Average CTC, and Highest Package.
- **Recruiter Approval Queue**: Accredit or reject newly registered employer accounts before they can post jobs to students.
- **Student Moderation**: Institutional student directory with CGPA oversight, account enable/disable moderation, and resume inspection.
- **Placement Drives Campaign**: Manage seasonal recruitment drives (`PlacementDrive`), assign faculty coordinators, and track ongoing placement calendar.
- **Institutional Reports & Funnel**: Comprehensive recruitment funnel analytics with package distributions and placement statistics.

---

## 🔑 Pre-Seeded Demo Accounts

The application automatically bootstraps sample accounts on first launch via `DataInitializer`:

| Role | Email | Password | Portal Entry |
|---|---|---|---|
| **Administrator** | `admin@placera.edu` | `Admin@1234` | `/admin/dashboard` |
| **Recruiter (Google)** | `recruitment@google.com` | `Company@123` | `/company/dashboard` |
| **Recruiter (Microsoft)** | `careers@microsoft.com` | `Company@123` | `/company/dashboard` |
| **Recruiter (FinTech)** | `hr@fintechpulse.io` | `Company@123` | `/company/dashboard` |
| **Student (Aarav)** | `aarav.sharma@college.edu` | `Student@123` | `/student/dashboard` |
| **Student (Surabhi)** | `surabhi.yadav@college.edu` | `Student@123` | `/student/dashboard` |
| **Student (Rohan)** | `rohan.verma@college.edu` | `Student@123` | `/student/dashboard` |

---

## 🛠️ Technology Stack

- **Language**: Java 21 (LTS)
- **Framework**: Spring Boot 3.3.4
- **Security**: Spring Security 6 (BCrypt Password Hashing, CSRF Protection, Role-based Portal Isolation)
- **Persistence**: Spring Data JPA, Hibernate 6.5, HikariCP
- **Database**: MySQL 8.0+
- **Frontend / Templating**: Thymeleaf 3, HTML5 Semantic Elements
- **Styling**: Vanilla Modern CSS Design System (Custom Color Tokens, Responsive Layouts, Glassmorphic Headers, Micro-interactions)
- **Testing**: JUnit 5 Jupiter, Mockito, Spring Security Test

---

## 📦 Getting Started

### 1. Prerequisites
- **JDK 21** or higher
- **Maven 3.8+**
- **MySQL 8.0+** running locally on port `3306`

### 2. Database Configuration
Edit [`src/main/resources/application.properties`](src/main/resources/application.properties) or set environment variables:
```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/placera_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

### 3. Run Unit & Service Tests
```bash
mvn test
```
All 19 automated domain and service unit tests will run and report:
```
Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### 4. Start the Application
```bash
mvn spring-boot:run
```
Once booted, access the web platform at:
```
http://localhost:8080/
```

---

## 🗂️ Project Architecture & Directory Structure

```
com.placement/
├── PlaceraApplication.java               # Main Spring Boot Entry Point
├── config/
│   ├── DataInitializer.java              # Bootstraps Admin & Demo Data
│   └── SecurityConfig.java               # RBAC & Route Access Firewall
├── controller/
│   ├── HomeController.java               # Public Landing & About Pages
│   ├── AuthController.java               # Sign-In & Role Registration
│   ├── StudentController.java            # Student Candidate Workspace
│   ├── CompanyController.java            # Recruiter Management Workspace
│   ├── AdminController.java              # Placement Cell Administration
│   └── ResumeDownloadController.java     # Secure PDF Resume Streaming
├── dto/
│   ├── StudentRegisterDto.java           # Student Registration Validator
│   ├── CompanyRegisterDto.java           # Recruiter Registration Validator
│   ├── JobPostingDto.java                # Job Creation/Edit Validator
│   ├── InterviewScheduleDto.java         # Interview Schedule Validator
│   └── AdminDashboardStats.java          # Analytics Aggregate DTO
├── entity/
│   ├── UserAccount.java                  # Auth & Role Entity
│   ├── StudentProfile.java               # Academic Candidate Profile
│   ├── CompanyProfile.java               # Recruiter Company Entity
│   ├── JobPosting.java                   # Campus Job Vacancy Model
│   ├── JobApplication.java               # Application & Selection State Machine
│   ├── InterviewSchedule.java            # Interview Round Schedule Model
│   └── PlacementDrive.java               # Institutional Season Campaign
├── exception/
│   ├── ResourceNotFoundException.java    # 404 Domain Exception
│   ├── DuplicateResourceException.java   # 409 Conflict Exception
│   ├── UnauthorizedAccessException.java  # 403 Forbidden Exception
│   └── GlobalExceptionHandler.java       # Central Controller Advice
├── repository/                           # Spring Data JPA Interfaces
├── security/
│   ├── CustomUserDetailsService.java     # Authentication Provider Bridge
│   └── CustomAuthenticationSuccessHandler.java # Dynamic Role Redirector
└── service/                              # OOP Interfaces & Implementations
    ├── UserService.java
    ├── StudentService.java
    ├── CompanyService.java
    └── AdminService.java
```

---

## 📄 License
Academic Coursework & Production Campus Placement Management System. Developed with modern Object-Oriented Programming standards.
