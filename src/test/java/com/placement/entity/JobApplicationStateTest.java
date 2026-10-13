package com.placement.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Job Application & Posting State Machine Tests")
class JobApplicationStateTest {

    private JobPosting job;
    private CompanyProfile company;
    private StudentProfile student;
    private JobApplication application;

    @BeforeEach
    void setUp() {
        company = new CompanyProfile();
        company.setCompanyName("Google");
        company.setApprovalStatus(CompanyProfile.ApprovalStatus.APPROVED);

        job = new JobPosting();
        job.setTitle("Software Engineer");
        job.setCompany(company);
        job.setActive(true);
        job.setApplicationDeadline(LocalDate.now().plusDays(10));
        job.setSalaryPackage(24.0);
        job.setMinCgpa(8.0);
        job.setEligibleBranches("CSE");
        job.setEligibleGraduationYear(2026);

        student = new StudentProfile();
        student.setFullName("Ananya Verma");
        student.setRollNumber("22CSE099");
        student.setCgpa(8.8);
        student.setBranch("CSE");
        student.setGraduationYear(2026);

        application = new JobApplication(student, job);
    }

    @Test
    @DisplayName("Approved company with active posting and future deadline should be open for application")
    void testJobOpenForApplication() {
        assertTrue(job.isOpenForApplication());
        assertTrue(job.isStudentEligible(student));
    }

    @Test
    @DisplayName("Job posting with passed deadline should not accept applications")
    void testJobDeadlinePassed() {
        job.setApplicationDeadline(LocalDate.now().minusDays(1));
        assertTrue(job.isDeadlinePassed());
        assertFalse(job.isOpenForApplication());
        assertFalse(job.isStudentEligible(student));
    }

    @Test
    @DisplayName("Unapproved company jobs should not be open for applications")
    void testUnapprovedCompanyJob() {
        company.setApprovalStatus(CompanyProfile.ApprovalStatus.PENDING);
        assertFalse(job.isOpenForApplication());
    }

    @Test
    @DisplayName("Application lifecycle queries should accurately report current status")
    void testApplicationStatusTransitions() {
        assertEquals(JobApplication.ApplicationStatus.APPLIED, application.getStatus());
        assertFalse(application.isSelected());
        assertFalse(application.isRejected());

        application.setStatus(JobApplication.ApplicationStatus.SHORTLISTED);
        assertTrue(application.isShortlisted());

        application.setStatus(JobApplication.ApplicationStatus.INTERVIEW_SCHEDULED);
        assertTrue(application.isInterviewScheduled());

        application.setStatus(JobApplication.ApplicationStatus.SELECTED);
        assertTrue(application.isSelected());

        application.setStatus(JobApplication.ApplicationStatus.REJECTED);
        assertTrue(application.isRejected());
    }
}
