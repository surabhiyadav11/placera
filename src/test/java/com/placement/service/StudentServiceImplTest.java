package com.placement.service;

import com.placement.entity.*;
import com.placement.exception.DuplicateResourceException;
import com.placement.exception.ResourceNotFoundException;
import com.placement.exception.UnauthorizedAccessException;
import com.placement.repository.InterviewScheduleRepository;
import com.placement.repository.JobApplicationRepository;
import com.placement.repository.JobPostingRepository;
import com.placement.repository.StudentProfileRepository;
import com.placement.service.impl.StudentServiceImpl;
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
@DisplayName("StudentService Business Logic Unit Tests")
class StudentServiceImplTest {

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private InterviewScheduleRepository interviewScheduleRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    private StudentProfile student;
    private JobPosting job;
    private CompanyProfile company;

    @BeforeEach
    void setUp() {
        student = new StudentProfile();
        student.setId(10L);
        student.setFullName("Aman Verma");
        student.setRollNumber("22CSE045");
        student.setBranch("CSE");
        student.setCgpa(8.50);
        student.setGraduationYear(2026);

        company = new CompanyProfile();
        company.setId(1L);
        company.setCompanyName("Tech Corp");
        company.setApprovalStatus(CompanyProfile.ApprovalStatus.APPROVED);

        job = new JobPosting();
        job.setId(100L);
        job.setTitle("Software Development Engineer");
        job.setCompany(company);
        job.setActive(true);
        job.setApplicationDeadline(LocalDate.now().plusDays(10));
        job.setMinCgpa(7.50);
        job.setEligibleBranches("CSE,IT");
        job.setEligibleGraduationYear(2026);
    }

    @Test
    @DisplayName("getProfileByUserId returns student profile when user exists")
    void getProfileByUserId_Success() {
        when(studentProfileRepository.findByUserAccountId(1L)).thenReturn(Optional.of(student));

        StudentProfile found = studentService.getProfileByUserId(1L);

        assertNotNull(found);
        assertEquals("Aman Verma", found.getFullName());
        assertEquals("22CSE045", found.getRollNumber());
        verify(studentProfileRepository, times(1)).findByUserAccountId(1L);
    }

    @Test
    @DisplayName("getProfileByUserId throws ResourceNotFoundException when student does not exist")
    void getProfileByUserId_NotFound() {
        when(studentProfileRepository.findByUserAccountId(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.getProfileByUserId(999L));
    }

    @Test
    @DisplayName("applyForJob succeeds when student is eligible and hasn't applied yet")
    void applyForJob_Success() {
        when(studentProfileRepository.findByUserAccountId(1L)).thenReturn(Optional.of(student));
        when(jobPostingRepository.findById(100L)).thenReturn(Optional.of(job));
        when(jobApplicationRepository.existsByStudentIdAndJobPostingId(10L, 100L)).thenReturn(false);
        when(jobApplicationRepository.save(any(JobApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobApplication application = studentService.applyForJob(1L, 100L, "I have strong Java skills");

        assertNotNull(application);
        assertEquals(student, application.getStudent());
        assertEquals(job, application.getJobPosting());
        assertEquals(JobApplication.ApplicationStatus.APPLIED, application.getStatus());
        verify(jobApplicationRepository, times(1)).save(any(JobApplication.class));
    }

    @Test
    @DisplayName("applyForJob rejects duplicate application with DuplicateResourceException")
    void applyForJob_DuplicateApplication_ThrowsException() {
        when(studentProfileRepository.findByUserAccountId(1L)).thenReturn(Optional.of(student));
        when(jobPostingRepository.findById(100L)).thenReturn(Optional.of(job));
        when(jobApplicationRepository.existsByStudentIdAndJobPostingId(10L, 100L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                studentService.applyForJob(1L, 100L, "Duplicate application"));
        verify(jobApplicationRepository, never()).save(any(JobApplication.class));
    }

    @Test
    @DisplayName("applyForJob throws UnauthorizedAccessException when student does not meet CGPA cutoff")
    void applyForJob_LowCgpa_ThrowsException() {
        student.setCgpa(6.50); // Cutoff is 7.50
        when(studentProfileRepository.findByUserAccountId(1L)).thenReturn(Optional.of(student));
        when(jobPostingRepository.findById(100L)).thenReturn(Optional.of(job));
        when(jobApplicationRepository.existsByStudentIdAndJobPostingId(10L, 100L)).thenReturn(false);

        assertThrows(UnauthorizedAccessException.class, () ->
                studentService.applyForJob(1L, 100L, "Low CGPA"));
        verify(jobApplicationRepository, never()).save(any(JobApplication.class));
    }

    @Test
    @DisplayName("applyForJob throws UnauthorizedAccessException when job is closed")
    void applyForJob_JobClosed_ThrowsException() {
        job.setActive(false);
        when(studentProfileRepository.findByUserAccountId(1L)).thenReturn(Optional.of(student));
        when(jobPostingRepository.findById(100L)).thenReturn(Optional.of(job));

        assertThrows(UnauthorizedAccessException.class, () ->
                studentService.applyForJob(1L, 100L, "Job is closed"));
        verify(jobApplicationRepository, never()).save(any(JobApplication.class));
    }
}
