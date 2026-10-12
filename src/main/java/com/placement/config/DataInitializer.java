package com.placement.config;

import com.placement.entity.*;
import com.placement.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DataInitializer - Seeds mandatory administrator account and realistic demo data on initial boot.
 *
 * Demonstrates:
 * - Secure Admin Bootstrapping: Admin accounts cannot be created via public registration.
 * - Initial Data Seeding: Populates approved and pending companies, students, jobs, and placement drives.
 * - OOP Constructor Injection: Injects all needed persistence interfaces.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final InterviewScheduleRepository interviewScheduleRepository;
    private final PlacementDriveRepository placementDriveRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${placera.admin.default-email:admin@placera.edu}")
    private String adminEmail;

    @Value("${placera.admin.default-password:Admin@1234}")
    private String adminPassword;

    public DataInitializer(UserAccountRepository userAccountRepository,
                           StudentProfileRepository studentProfileRepository,
                           CompanyProfileRepository companyProfileRepository,
                           JobPostingRepository jobPostingRepository,
                           JobApplicationRepository jobApplicationRepository,
                           InterviewScheduleRepository interviewScheduleRepository,
                           PlacementDriveRepository placementDriveRepository,
                           PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.interviewScheduleRepository = interviewScheduleRepository;
        this.placementDriveRepository = placementDriveRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedAdminAccount();
        seedDemoDataIfEmpty();
    }

    private void seedAdminAccount() {
        if (!userAccountRepository.existsByEmail(adminEmail.toLowerCase())) {
            UserAccount admin = new UserAccount();
            admin.setEmail(adminEmail.toLowerCase());
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setEnabled(true);
            userAccountRepository.save(admin);
        }
    }

    private void seedDemoDataIfEmpty() {
        if (studentProfileRepository.count() > 0 || companyProfileRepository.count() > 0) {
            return; // Data already present, prevent re-seeding
        }

        // 1. Seed Placement Drive Season
        PlacementDrive drive = new PlacementDrive();
        drive.setDriveName("Campus Placement Season 2025-2026");
        drive.setAcademicYear("2025-2026");
        drive.setStartDate(LocalDate.now().minusMonths(1));
        drive.setEndDate(LocalDate.now().plusMonths(5));
        drive.setCoordinator("Prof. R. K. Sharma (Head of Placement)");
        drive.setDescription("Institutional placement campaign for B.Tech / M.Tech batches.");
        drive.setStatus(PlacementDrive.DriveStatus.ONGOING);
        placementDriveRepository.save(drive);

        // 2. Seed Approved Company: Google India
        UserAccount googleUser = new UserAccount("recruitment@google.com", passwordEncoder.encode("Company@123"), Role.ROLE_COMPANY);
        googleUser.setEnabled(true);
        userAccountRepository.save(googleUser);

        CompanyProfile googleProfile = new CompanyProfile();
        googleProfile.setUserAccount(googleUser);
        googleProfile.setCompanyName("Google India");
        googleProfile.setContactPerson("Pooja Mehta");
        googleProfile.setContactEmail("pooja@google.com");
        googleProfile.setPhone("+91 98765 43210");
        googleProfile.setWebsite("https://careers.google.com");
        googleProfile.setIndustry("Technology & Cloud");
        googleProfile.setLocation("Bengaluru, Karnataka");
        googleProfile.setDescription("Global technology leader in search, cloud systems, and software engineering.");
        googleProfile.setApprovalStatus(CompanyProfile.ApprovalStatus.APPROVED);
        companyProfileRepository.save(googleProfile);

        // Seed Google Job Postings
        JobPosting googleJob1 = new JobPosting();
        googleJob1.setCompany(googleProfile);
        googleJob1.setTitle("Software Development Engineer (Backend)");
        googleJob1.setJobRole("Software Engineer");
        googleJob1.setJobType("Full-Time");
        googleJob1.setLocation("Bengaluru");
        googleJob1.setSalaryPackage(28.5);
        googleJob1.setMinCgpa(8.0);
        googleJob1.setEligibleBranches("Computer Science, Information Technology, Electronics");
        googleJob1.setEligibleGraduationYear(2026);
        googleJob1.setRequiredSkills("Java, Data Structures, Algorithms, Distributed Systems, MySQL");
        googleJob1.setDescription("Develop high-throughput cloud microservices and distributed storage engines.");
        googleJob1.setApplicationDeadline(LocalDate.now().plusDays(25));
        googleJob1.setActive(true);
        jobPostingRepository.save(googleJob1);

        JobPosting googleJob2 = new JobPosting();
        googleJob2.setCompany(googleProfile);
        googleJob2.setTitle("Cloud Associate Engineer");
        googleJob2.setJobRole("Cloud Specialist");
        googleJob2.setJobType("Full-Time");
        googleJob2.setLocation("Hyderabad");
        googleJob2.setSalaryPackage(18.0);
        googleJob2.setMinCgpa(7.5);
        googleJob2.setEligibleBranches("ALL");
        googleJob2.setEligibleGraduationYear(2026);
        googleJob2.setRequiredSkills("Linux, Networking, Java, Cloud Computing, Docker");
        googleJob2.setDescription("Manage large-scale enterprise cloud infrastructure and deployment pipelines.");
        googleJob2.setApplicationDeadline(LocalDate.now().plusDays(30));
        googleJob2.setActive(true);
        jobPostingRepository.save(googleJob2);

        // 3. Seed Approved Company: Microsoft India
        UserAccount msUser = new UserAccount("careers@microsoft.com", passwordEncoder.encode("Company@123"), Role.ROLE_COMPANY);
        msUser.setEnabled(true);
        userAccountRepository.save(msUser);

        CompanyProfile msProfile = new CompanyProfile();
        msProfile.setUserAccount(msUser);
        msProfile.setCompanyName("Microsoft India");
        msProfile.setContactPerson("Aditya Nambiar");
        msProfile.setContactEmail("aditya@microsoft.com");
        msProfile.setPhone("+91 98111 22334");
        msProfile.setWebsite("https://careers.microsoft.com");
        msProfile.setIndustry("Software Engineering");
        msProfile.setLocation("Hyderabad, Telangana");
        msProfile.setDescription("Empowering individuals and organizations through cutting-edge enterprise cloud platforms.");
        msProfile.setApprovalStatus(CompanyProfile.ApprovalStatus.APPROVED);
        companyProfileRepository.save(msProfile);

        JobPosting msJob = new JobPosting();
        msJob.setCompany(msProfile);
        msJob.setTitle("Associate Software Development Engineer");
        msJob.setJobRole("Software Developer");
        msJob.setJobType("Full-Time");
        msJob.setLocation("Hyderabad");
        msJob.setSalaryPackage(24.0);
        msJob.setMinCgpa(8.0);
        msJob.setEligibleBranches("Computer Science, Information Technology");
        msJob.setEligibleGraduationYear(2026);
        msJob.setRequiredSkills("Java, C++, Object Oriented Programming, System Design, SQL");
        msJob.setDescription("Architect next-generation productivity and intelligence tools.");
        msJob.setApplicationDeadline(LocalDate.now().plusDays(20));
        msJob.setActive(true);
        jobPostingRepository.save(msJob);

        // 4. Seed Pending Company (for Admin Approval Queue demonstration)
        UserAccount pendingUser = new UserAccount("hr@fintechpulse.io", passwordEncoder.encode("Company@123"), Role.ROLE_COMPANY);
        pendingUser.setEnabled(true);
        userAccountRepository.save(pendingUser);

        CompanyProfile pendingProfile = new CompanyProfile();
        pendingProfile.setUserAccount(pendingUser);
        pendingProfile.setCompanyName("FinTech Pulse Solutions");
        pendingProfile.setContactPerson("Vikram Singhania");
        pendingProfile.setContactEmail("vikram@fintechpulse.io");
        pendingProfile.setPhone("+91 97654 32190");
        pendingProfile.setWebsite("https://fintechpulse.io");
        pendingProfile.setIndustry("Financial Technology");
        pendingProfile.setLocation("Mumbai, Maharashtra");
        pendingProfile.setDescription("Fast-growing payments orchestration and algorithmic risk modeling platform.");
        pendingProfile.setApprovalStatus(CompanyProfile.ApprovalStatus.PENDING);
        companyProfileRepository.save(pendingProfile);

        // 5. Seed Students
        // Student 1: Aarav Sharma
        UserAccount s1User = new UserAccount("aarav.sharma@college.edu", passwordEncoder.encode("Student@123"), Role.ROLE_STUDENT);
        s1User.setEnabled(true);
        userAccountRepository.save(s1User);

        StudentProfile s1 = new StudentProfile();
        s1.setUserAccount(s1User);
        s1.setFullName("Aarav Sharma");
        s1.setRollNumber("23CSE011");
        s1.setBranch("Computer Science");
        s1.setCgpa(8.85);
        s1.setGraduationYear(2026);
        s1.setPhone("+91 91234 56780");
        s1.setSkills("Java, Spring Boot, MySQL, Hibernate, OOP, REST APIs, Git");
        studentProfileRepository.save(s1);

        // Student 2: Surabhi Yadav
        UserAccount s2User = new UserAccount("surabhi.yadav@college.edu", passwordEncoder.encode("Student@123"), Role.ROLE_STUDENT);
        s2User.setEnabled(true);
        userAccountRepository.save(s2User);

        StudentProfile s2 = new StudentProfile();
        s2.setUserAccount(s2User);
        s2.setFullName("Surabhi Yadav");
        s2.setRollNumber("23CSE042");
        s2.setBranch("Computer Science");
        s2.setCgpa(9.30);
        s2.setGraduationYear(2026);
        s2.setPhone("+91 98220 12345");
        s2.setSkills("Java, Spring Security, Microservices, Python, React, Data Structures");
        studentProfileRepository.save(s2);

        // Student 3: Rohan Verma
        UserAccount s3User = new UserAccount("rohan.verma@college.edu", passwordEncoder.encode("Student@123"), Role.ROLE_STUDENT);
        s3User.setEnabled(true);
        userAccountRepository.save(s3User);

        StudentProfile s3 = new StudentProfile();
        s3.setUserAccount(s3User);
        s3.setFullName("Rohan Verma");
        s3.setRollNumber("23IT023");
        s3.setBranch("Information Technology");
        s3.setCgpa(7.90);
        s3.setGraduationYear(2026);
        s3.setPhone("+91 94567 89012");
        s3.setSkills("Java, Web Development, SQL, JavaScript, HTML, CSS");
        studentProfileRepository.save(s3);

        // 6. Seed Applications & Interview Schedules
        // Aarav applied to Google Backend -> INTERVIEW_SCHEDULED
        JobApplication app1 = new JobApplication(s1, googleJob1, "Eager to contribute to scalable cloud backend services.");
        app1.setStatus(JobApplication.ApplicationStatus.INTERVIEW_SCHEDULED);
        jobApplicationRepository.save(app1);

        InterviewSchedule interview1 = new InterviewSchedule(
                app1,
                "Round 1 - Technical Coding Interview",
                LocalDateTime.now().plusDays(3).withHour(11).withMinute(0),
                "Google Meet: https://meet.google.com/abc-defg-hij"
        );
        interview1.setInterviewerName("Siddharth Roy (Senior Engineer)");
        interviewScheduleRepository.save(interview1);

        // Surabhi applied to Microsoft -> SELECTED (Demonstrating placement % statistic)
        JobApplication app2 = new JobApplication(s2, msJob, "Excited about distributed intelligence and platform engineering.");
        app2.setStatus(JobApplication.ApplicationStatus.SELECTED);
        app2.setRemarks("Selected for Software Development Engineer role at Microsoft Hyderabad.");
        jobApplicationRepository.save(app2);

        // Rohan applied to Google Cloud -> APPLIED
        JobApplication app3 = new JobApplication(s3, googleJob2, "Passionate about Linux systems and containerized architectures.");
        app3.setStatus(JobApplication.ApplicationStatus.APPLIED);
        jobApplicationRepository.save(app3);
    }
}
