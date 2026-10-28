package com.placement.service;

import com.placement.dto.JobPostingDto;
import com.placement.entity.*;
import com.placement.exception.UnauthorizedAccessException;
import com.placement.repository.CompanyProfileRepository;
import com.placement.repository.InterviewScheduleRepository;
import com.placement.repository.JobApplicationRepository;
import com.placement.repository.JobPostingRepository;
import com.placement.service.impl.CompanyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompanyService Business Logic Unit Tests")
class CompanyServiceImplTest {

    @Mock
    private CompanyProfileRepository companyProfileRepository;

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private InterviewScheduleRepository interviewScheduleRepository;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private CompanyProfile approvedCompany;
    private CompanyProfile pendingCompany;

    @BeforeEach
    void setUp() {
        approvedCompany = new CompanyProfile();
        approvedCompany.setId(10L);
        approvedCompany.setCompanyName("Infosys Labs");
        approvedCompany.setApprovalStatus(CompanyProfile.ApprovalStatus.APPROVED);

        pendingCompany = new CompanyProfile();
        pendingCompany.setId(20L);
        pendingCompany.setCompanyName("Startup Inc");
        pendingCompany.setApprovalStatus(CompanyProfile.ApprovalStatus.PENDING);
    }

    @Test
    @DisplayName("createJobPosting succeeds when company is approved by admin")
    void createJobPosting_ApprovedCompany_Success() {
        when(companyProfileRepository.findByUserAccountId(1L)).thenReturn(Optional.of(approvedCompany));
        when(jobPostingRepository.save(any(JobPosting.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobPostingDto dto = new JobPostingDto();
        dto.setTitle("Associate Software Engineer");
        dto.setJobRole("Developer");
        dto.setJobType("Full-Time");
        dto.setLocation("Bengaluru");
        dto.setSalaryPackage(9.50);
        dto.setMinCgpa(7.00);
        dto.setEligibleBranches("CSE,ECE");
        dto.setEligibleGraduationYear(2026);
        dto.setRequiredSkills("Java, Spring Boot");
        dto.setDescription("Great career opportunity");
        dto.setApplicationDeadline(LocalDate.now().plusMonths(1));
        dto.setActive(true);

        JobPosting created = companyService.createJobPosting(1L, dto);

        assertNotNull(created);
        assertEquals("Associate Software Engineer", created.getTitle());
        assertEquals(approvedCompany, created.getCompany());
        assertTrue(created.isActive());
        verify(jobPostingRepository, times(1)).save(any(JobPosting.class));
    }

    @Test
    @DisplayName("createJobPosting throws UnauthorizedAccessException when company is still pending approval")
    void createJobPosting_PendingCompany_ThrowsException() {
        when(companyProfileRepository.findByUserAccountId(2L)).thenReturn(Optional.of(pendingCompany));

        JobPostingDto dto = new JobPostingDto();
        dto.setTitle("Backend Engineer");
        dto.setJobRole("Engineer");

        assertThrows(UnauthorizedAccessException.class, () ->
                companyService.createJobPosting(2L, dto));
        verify(jobPostingRepository, never()).save(any(JobPosting.class));
    }

    @Test
    @DisplayName("updateApplicationStatus updates candidate status when company owns the job posting")
    void updateApplicationStatus_Success() {
        when(companyProfileRepository.findByUserAccountId(1L)).thenReturn(Optional.of(approvedCompany));

        JobPosting job = new JobPosting();
        job.setCompany(approvedCompany);

        JobApplication application = new JobApplication();
        application.setId(50L);
        application.setJobPosting(job);
        application.setStatus(JobApplication.ApplicationStatus.APPLIED);

        when(jobApplicationRepository.findById(50L)).thenReturn(Optional.of(application));
        when(jobApplicationRepository.save(any(JobApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobApplication updated = companyService.updateApplicationStatus(1L, 50L,
                JobApplication.ApplicationStatus.SHORTLISTED, "Profile matches requirements");

        assertNotNull(updated);
        assertEquals(JobApplication.ApplicationStatus.SHORTLISTED, updated.getStatus());
        assertEquals("Profile matches requirements", updated.getRemarks());
        verify(jobApplicationRepository, times(1)).save(application);
    }
}
