package com.placement.service;

import com.placement.dto.AdminDashboardStats;
import com.placement.entity.CompanyProfile;
import com.placement.entity.JobApplication;
import com.placement.entity.JobPosting;
import com.placement.entity.PlacementDrive;
import com.placement.entity.StudentProfile;

import java.util.List;

/**
 * AdminService - Abstraction contract for placement administration operations.
 *
 * Demonstrates:
 * - OOP Abstraction: Declares methods for institutional oversight, corporate verification,
 *   placement reports, and drive scheduling.
 */
public interface AdminService {

    AdminDashboardStats getDashboardStatistics();

    List<CompanyProfile> getPendingCompanies();

    List<CompanyProfile> getAllCompanies(String keyword);

    CompanyProfile getCompanyById(Long companyId);

    void approveCompany(Long companyId);

    void rejectCompany(Long companyId, String reason);

    List<StudentProfile> getAllStudents(String keyword);

    StudentProfile getStudentById(Long studentId);

    List<JobPosting> getAllJobPostings();

    List<JobApplication> getAllApplications();

    List<PlacementDrive> getAllPlacementDrives();

    PlacementDrive createPlacementDrive(PlacementDrive drive);

    void toggleUserAccount(Long userId, boolean enabled);
}
