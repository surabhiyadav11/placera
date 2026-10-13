package com.placement.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Student Profile Domain Logic Tests")
class StudentEligibilityTest {

    private StudentProfile student;

    @BeforeEach
    void setUp() {
        student = new StudentProfile();
        student.setFullName("Aditya Sharma");
        student.setRollNumber("22CSE042");
        student.setBranch("CSE");
        student.setCgpa(8.50);
        student.setGraduationYear(2026);
    }

    @Test
    @DisplayName("Candidate with qualifying CGPA and branch should be eligible")
    void testEligibleCandidate() {
        boolean eligible = student.isEligible(7.50, "CSE,IT,ECE", 2026);
        assertTrue(eligible, "Student with 8.5 CGPA in CSE graduating in 2026 should be eligible.");
    }

    @Test
    @DisplayName("Candidate below cutoff CGPA should be rejected")
    void testBelowCgpaCutoff() {
        boolean eligible = student.isEligible(9.00, "CSE,IT", 2026);
        assertFalse(eligible, "Student with 8.5 CGPA should not meet a 9.0 cutoff.");
    }

    @Test
    @DisplayName("Candidate with non-qualifying branch should be rejected")
    void testIneligibleBranch() {
        boolean eligible = student.isEligible(7.00, "MECH,CIVIL", 2026);
        assertFalse(eligible, "CSE student should not be eligible for MECH/CIVIL openings.");
    }

    @Test
    @DisplayName("Wildcard ALL branches should accept any student branch")
    void testWildcardBranches() {
        boolean eligible = student.isEligible(7.00, "ALL", 2026);
        assertTrue(eligible, "ALL branches criteria should accept CSE student.");
    }

    @Test
    @DisplayName("Candidate graduating in different year should be rejected")
    void testGraduationYearMismatch() {
        boolean eligible = student.isEligible(7.00, "ALL", 2025);
        assertFalse(eligible, "2026 graduate should not be eligible for 2025 drive.");
    }

    @Test
    @DisplayName("Resume upload status helper should accurately reflect path presence")
    void testResumePresenceHelper() {
        assertFalse(student.hasUploadedResume());
        student.setResumePath("22CSE042_resume.pdf");
        assertTrue(student.hasUploadedResume());
        assertEquals("22CSE042_resume.pdf", student.getResumeFilePath());
    }
}
