package com.placement.service;

import com.placement.entity.InterviewSchedule;
import com.placement.entity.JobApplication;
import com.placement.entity.JobPosting;
import com.placement.entity.StudentProfile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * StudentService - Abstraction contract for student recruitment actions and profile management.
 *
 * Demonstrates:
 * - OOP Abstraction: Encapsulates all student portal business requirements into an interface contract.
 */
public interface StudentService {

    StudentProfile getProfileByUserId(Long userId);

    StudentProfile updateProfile(Long userId, StudentProfile updatedProfile);

    String storeResume(Long userId, MultipartFile file);

    List<JobPosting> getAvailableJobPostings(Long userId, String keyword);

    JobPosting getJobPostingDetails(Long jobId);

    JobApplication applyForJob(Long userId, Long jobId, String coverNote);

    List<JobApplication> getStudentApplications(Long userId);

    List<InterviewSchedule> getStudentInterviews(Long userId);

    boolean isEligibleForJob(Long userId, Long jobId);

    boolean hasApplied(Long userId, Long jobId);
}
