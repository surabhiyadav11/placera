package com.placement.service.impl;

import com.placement.entity.*;
import com.placement.exception.DuplicateResourceException;
import com.placement.exception.ResourceNotFoundException;
import com.placement.exception.UnauthorizedAccessException;
import com.placement.repository.InterviewScheduleRepository;
import com.placement.repository.JobApplicationRepository;
import com.placement.repository.JobPostingRepository;
import com.placement.repository.StudentProfileRepository;
import com.placement.service.StudentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * StudentServiceImpl - Implements StudentService interface.
 *
 * Demonstrates:
 * - OOP Constructor Injection: Injects all four collaborating repositories cleanly.
 * - Business Rule Enforcement:
 *   1. Blocks unapproved company postings from student visibility.
 *   2. Validates student eligibility (CGPA, branch, graduation year) prior to applying.
 *   3. Enforces single application rule per student per job.
 *   4. Safely manages PDF resume file storage.
 */
@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentProfileRepository studentProfileRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final InterviewScheduleRepository interviewScheduleRepository;

    @Value("${placera.upload.dir:uploads/resumes}")
    private String uploadDir;

    // Constructor Injection
    public StudentServiceImpl(StudentProfileRepository studentProfileRepository,
                              JobPostingRepository jobPostingRepository,
                              JobApplicationRepository jobApplicationRepository,
                              InterviewScheduleRepository interviewScheduleRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.interviewScheduleRepository = interviewScheduleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfile getProfileByUserId(Long userId) {
        return studentProfileRepository.findByUserAccountId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userAccountId", userId));
    }

    @Override
    public StudentProfile updateProfile(Long userId, StudentProfile updatedData) {
        StudentProfile existing = getProfileByUserId(userId);
        existing.setFullName(updatedData.getFullName());
        existing.setBranch(updatedData.getBranch());
        existing.setCgpa(updatedData.getCgpa());
        existing.setGraduationYear(updatedData.getGraduationYear());
        existing.setPhone(updatedData.getPhone());
        existing.setSkills(updatedData.getSkills());
        return studentProfileRepository.save(existing);
    }

    @Override
    public String storeResume(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded resume file cannot be empty.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF resumes (.pdf) are supported.");
        }

        try {
            Path targetDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(targetDirectory);

            String uniqueFilename = "resume_" + userId + "_" + UUID.randomUUID().toString().substring(0, 8) + ".pdf";
            Path targetFile = targetDirectory.resolve(uniqueFilename);

            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

            StudentProfile student = getProfileByUserId(userId);
            student.setResumePath(uniqueFilename);
            studentProfileRepository.save(student);

            return uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Could not store resume file: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> getAvailableJobPostings(Long userId, String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return jobPostingRepository.searchActiveJobs(keyword.trim());
        }
        return jobPostingRepository.findByActiveTrueAndCompany_ApprovalStatusOrderByCreatedAtDesc(
                CompanyProfile.ApprovalStatus.APPROVED);
    }

    @Override
    @Transactional(readOnly = true)
    public JobPosting getJobPostingDetails(Long jobId) {
        return jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobPosting", "id", jobId));
    }

    @Override
    public JobApplication applyForJob(Long userId, Long jobId, String coverNote) {
        StudentProfile student = getProfileByUserId(userId);
        JobPosting job = getJobPostingDetails(jobId);

        // 1. Verify job is currently open and published by an approved company
        if (!job.isOpenForApplication()) {
            throw new UnauthorizedAccessException("APPLY", "This job is closed or the company is not currently approved.");
        }

        // 2. Prevent duplicate application at application layer (in addition to DB unique constraint)
        if (jobApplicationRepository.existsByStudentIdAndJobPostingId(student.getId(), jobId)) {
            throw new DuplicateResourceException("JobApplication", "jobId", jobId);
        }

        // 3. Verify student eligibility constraints
        if (!job.isStudentEligible(student)) {
            throw new UnauthorizedAccessException("APPLY", "You do not meet the minimum eligibility requirements for this position.");
        }

        // 4. Create and persist application
        JobApplication application = new JobApplication(student, job, coverNote);
        return jobApplicationRepository.save(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplication> getStudentApplications(Long userId) {
        StudentProfile student = getProfileByUserId(userId);
        return jobApplicationRepository.findByStudentIdOrderByAppliedAtDesc(student.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewSchedule> getStudentInterviews(Long userId) {
        StudentProfile student = getProfileByUserId(userId);
        return interviewScheduleRepository.findByApplicationStudentIdOrderByScheduledDateTimeAsc(student.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEligibleForJob(Long userId, Long jobId) {
        StudentProfile student = getProfileByUserId(userId);
        JobPosting job = getJobPostingDetails(jobId);
        return job.isStudentEligible(student);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasApplied(Long userId, Long jobId) {
        StudentProfile student = getProfileByUserId(userId);
        return jobApplicationRepository.existsByStudentIdAndJobPostingId(student.getId(), jobId);
    }
}
