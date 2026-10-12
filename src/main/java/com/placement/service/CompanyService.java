package com.placement.service;

import com.placement.dto.InterviewScheduleDto;
import com.placement.dto.JobPostingDto;
import com.placement.entity.CompanyProfile;
import com.placement.entity.InterviewSchedule;
import com.placement.entity.JobApplication;
import com.placement.entity.JobPosting;

import java.util.List;

/**
 * CompanyService - Abstraction contract for company portal operations.
 *
 * Demonstrates:
 * - OOP Abstraction: Declares corporate job posting, applicant evaluation, and interview scheduling workflows.
 */
public interface CompanyService {

    CompanyProfile getCompanyProfileByUserId(Long userId);

    CompanyProfile updateProfile(Long userId, CompanyProfile updatedData);

    JobPosting createJobPosting(Long userId, JobPostingDto dto);

    JobPosting updateJobPosting(Long userId, Long jobId, JobPostingDto dto);

    void toggleJobStatus(Long userId, Long jobId, boolean active);

    List<JobPosting> getCompanyJobPostings(Long userId);

    JobPosting getJobPostingById(Long userId, Long jobId);

    List<JobApplication> getApplicantsForJob(Long userId, Long jobId);

    List<JobApplication> getAllApplicantsForCompany(Long userId);

    JobApplication updateApplicationStatus(Long userId, Long applicationId,
                                           JobApplication.ApplicationStatus status, String remarks);

    InterviewSchedule scheduleInterview(Long userId, InterviewScheduleDto dto);

    List<InterviewSchedule> getCompanyInterviews(Long userId);

    long getApplicantCount(Long userId);

    long getShortlistedCount(Long userId);
}
