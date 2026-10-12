package com.placement.service.impl;

import com.placement.dto.AdminDashboardStats;
import com.placement.entity.*;
import com.placement.exception.ResourceNotFoundException;
import com.placement.repository.*;
import com.placement.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AdminServiceImpl - Implements AdminService interface.
 *
 * Demonstrates:
 * - OOP Constructor Injection: Injects persistence repositories for aggregated KPI reporting.
 * - Business Approval Workflow: Transitions company status (PENDING -> APPROVED / REJECTED).
 * - User Moderation: Manages active account states.
 */
@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private final StudentProfileRepository studentProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final InterviewScheduleRepository interviewScheduleRepository;
    private final PlacementDriveRepository placementDriveRepository;
    private final UserAccountRepository userAccountRepository;

    // Constructor Injection
    public AdminServiceImpl(StudentProfileRepository studentProfileRepository,
                            CompanyProfileRepository companyProfileRepository,
                            JobPostingRepository jobPostingRepository,
                            JobApplicationRepository jobApplicationRepository,
                            InterviewScheduleRepository interviewScheduleRepository,
                            PlacementDriveRepository placementDriveRepository,
                            UserAccountRepository userAccountRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.interviewScheduleRepository = interviewScheduleRepository;
        this.placementDriveRepository = placementDriveRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardStats getDashboardStatistics() {
        long totalStudents = studentProfileRepository.count();
        long placedStudents = jobApplicationRepository.countDistinctPlacedStudents();
        double placementPercentage = (totalStudents > 0)
                ? ((double) placedStudents / totalStudents) * 100.0
                : 0.0;

        long totalCompanies = companyProfileRepository.count();
        long pendingCompanies = companyProfileRepository.countByApprovalStatus(CompanyProfile.ApprovalStatus.PENDING);
        long activeJobOpenings = jobPostingRepository.countByActiveTrue();
        long totalApplications = jobApplicationRepository.count();
        long totalInterviews = interviewScheduleRepository.count();

        return new AdminDashboardStats(
                totalStudents,
                placedStudents,
                placementPercentage,
                totalCompanies,
                pendingCompanies,
                activeJobOpenings,
                totalApplications,
                totalInterviews
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyProfile> getPendingCompanies() {
        return companyProfileRepository.findByApprovalStatusOrderByCreatedAtDesc(CompanyProfile.ApprovalStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyProfile> getAllCompanies(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return companyProfileRepository.searchCompanies(keyword.trim());
        }
        return companyProfileRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyProfile getCompanyById(Long companyId) {
        return companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("CompanyProfile", "id", companyId));
    }

    @Override
    public void approveCompany(Long companyId) {
        CompanyProfile company = getCompanyById(companyId);
        company.setApprovalStatus(CompanyProfile.ApprovalStatus.APPROVED);
        company.setRejectionReason(null);
        companyProfileRepository.save(company);
    }

    @Override
    public void rejectCompany(Long companyId, String reason) {
        CompanyProfile company = getCompanyById(companyId);
        company.setApprovalStatus(CompanyProfile.ApprovalStatus.REJECTED);
        company.setRejectionReason(reason != null ? reason.trim() : "Application rejected by Placement Cell");
        companyProfileRepository.save(company);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentProfile> getAllStudents(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return studentProfileRepository.searchStudents(keyword.trim());
        }
        return studentProfileRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfile getStudentById(Long studentId) {
        return studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "id", studentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> getAllJobPostings() {
        return jobPostingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplication> getAllApplications() {
        return jobApplicationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlacementDrive> getAllPlacementDrives() {
        return placementDriveRepository.findAllByOrderByStartDateDesc();
    }

    @Override
    public PlacementDrive createPlacementDrive(PlacementDrive drive) {
        return placementDriveRepository.save(drive);
    }

    @Override
    public void toggleUserAccount(Long userId, boolean enabled) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserAccount", "id", userId));
        user.setEnabled(enabled);
        userAccountRepository.save(user);
    }
}
