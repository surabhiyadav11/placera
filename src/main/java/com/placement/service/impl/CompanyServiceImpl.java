package com.placement.service.impl;

import com.placement.dto.InterviewScheduleDto;
import com.placement.dto.JobPostingDto;
import com.placement.entity.CompanyProfile;
import com.placement.entity.InterviewSchedule;
import com.placement.entity.JobApplication;
import com.placement.entity.JobPosting;
import com.placement.exception.ResourceNotFoundException;
import com.placement.exception.UnauthorizedAccessException;
import com.placement.repository.CompanyProfileRepository;
import com.placement.repository.InterviewScheduleRepository;
import com.placement.repository.JobApplicationRepository;
import com.placement.repository.JobPostingRepository;
import com.placement.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * CompanyServiceImpl - Implements CompanyService interface.
 *
 * Demonstrates:
 * - OOP Constructor Injection: Injects all four collaborating repositories cleanly.
 * - Horizontal Ownership Security: Enforces that companies only manipulate their own job postings,
 *   candidates, and interview schedules.
 * - Approval Policy Enforcement: Prevents unapproved companies from creating active job postings.
 */
@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyProfileRepository companyProfileRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final InterviewScheduleRepository interviewScheduleRepository;

    // Constructor Injection
    public CompanyServiceImpl(CompanyProfileRepository companyProfileRepository,
                              JobPostingRepository jobPostingRepository,
                              JobApplicationRepository jobApplicationRepository,
                              InterviewScheduleRepository interviewScheduleRepository) {
        this.companyProfileRepository = companyProfileRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.interviewScheduleRepository = interviewScheduleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyProfile getCompanyProfileByUserId(Long userId) {
        return companyProfileRepository.findByUserAccountId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("CompanyProfile", "userAccountId", userId));
    }

    @Override
    public CompanyProfile updateProfile(Long userId, CompanyProfile updatedData) {
        CompanyProfile existing = getCompanyProfileByUserId(userId);
        existing.setCompanyName(updatedData.getCompanyName());
        existing.setContactPerson(updatedData.getContactPerson());
        existing.setContactEmail(updatedData.getContactEmail());
        existing.setPhone(updatedData.getPhone());
        existing.setWebsite(updatedData.getWebsite());
        existing.setIndustry(updatedData.getIndustry());
        existing.setLocation(updatedData.getLocation());
        existing.setDescription(updatedData.getDescription());
        return companyProfileRepository.save(existing);
    }

    @Override
    public JobPosting createJobPosting(Long userId, JobPostingDto dto) {
        CompanyProfile company = getCompanyProfileByUserId(userId);

        // Security check: Unapproved companies cannot publish active job postings
        if (!company.isApproved()) {
            throw new UnauthorizedAccessException("CREATE_JOB",
                    "Your company profile is pending administrator approval. Job postings cannot be created yet.");
        }

        JobPosting job = new JobPosting();
        job.setCompany(company);
        job.setTitle(dto.getTitle().trim());
        job.setJobRole(dto.getJobRole().trim());
        job.setJobType(dto.getJobType());
        job.setLocation(dto.getLocation().trim());
        job.setSalaryPackage(dto.getSalaryPackage());
        job.setMinCgpa(dto.getMinCgpa());
        job.setEligibleBranches(dto.getEligibleBranches());
        job.setEligibleGraduationYear(dto.getEligibleGraduationYear());
        job.setRequiredSkills(dto.getRequiredSkills());
        job.setDescription(dto.getDescription());
        job.setApplicationDeadline(dto.getApplicationDeadline());
        job.setActive(dto.isActive());

        return jobPostingRepository.save(job);
    }

    @Override
    public JobPosting updateJobPosting(Long userId, Long jobId, JobPostingDto dto) {
        CompanyProfile company = getCompanyProfileByUserId(userId);
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobPosting", "id", jobId));

        // Ownership check: Prevent modifying jobs belonging to other companies
        if (!Objects.equals(job.getCompany().getId(), company.getId())) {
            throw new UnauthorizedAccessException("UPDATE_JOB", "You do not own this job posting.");
        }

        job.setTitle(dto.getTitle().trim());
        job.setJobRole(dto.getJobRole().trim());
        job.setJobType(dto.getJobType());
        job.setLocation(dto.getLocation().trim());
        job.setSalaryPackage(dto.getSalaryPackage());
        job.setMinCgpa(dto.getMinCgpa());
        job.setEligibleBranches(dto.getEligibleBranches());
        job.setEligibleGraduationYear(dto.getEligibleGraduationYear());
        job.setRequiredSkills(dto.getRequiredSkills());
        job.setDescription(dto.getDescription());
        job.setApplicationDeadline(dto.getApplicationDeadline());
        job.setActive(dto.isActive());

        return jobPostingRepository.save(job);
    }

    @Override
    public void toggleJobStatus(Long userId, Long jobId, boolean active) {
        CompanyProfile company = getCompanyProfileByUserId(userId);
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobPosting", "id", jobId));

        if (!Objects.equals(job.getCompany().getId(), company.getId())) {
            throw new UnauthorizedAccessException("TOGGLE_JOB", "You do not own this job posting.");
        }

        job.setActive(active);
        jobPostingRepository.save(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> getCompanyJobPostings(Long userId) {
        CompanyProfile company = getCompanyProfileByUserId(userId);
        return jobPostingRepository.findByCompanyIdOrderByCreatedAtDesc(company.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public JobPosting getJobPostingById(Long userId, Long jobId) {
        CompanyProfile company = getCompanyProfileByUserId(userId);
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobPosting", "id", jobId));

        if (!Objects.equals(job.getCompany().getId(), company.getId())) {
            throw new UnauthorizedAccessException("VIEW_JOB", "You do not own this job posting.");
        }

        return job;
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplication> getApplicantsForJob(Long userId, Long jobId) {
        CompanyProfile company = getCompanyProfileByUserId(userId);
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobPosting", "id", jobId));

        if (!Objects.equals(job.getCompany().getId(), company.getId())) {
            throw new UnauthorizedAccessException("VIEW_APPLICANTS", "You do not own this job posting.");
        }

        return jobApplicationRepository.findByJobPostingIdOrderByAppliedAtDesc(jobId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplication> getAllApplicantsForCompany(Long userId) {
        CompanyProfile company = getCompanyProfileByUserId(userId);
        return jobApplicationRepository.findByJobPostingCompanyIdOrderByAppliedAtDesc(company.getId());
    }

    @Override
    public JobApplication updateApplicationStatus(Long userId, Long applicationId,
                                                  JobApplication.ApplicationStatus status, String remarks) {
        CompanyProfile company = getCompanyProfileByUserId(userId);
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("JobApplication", "id", applicationId));

        // Ownership check
        if (!Objects.equals(application.getJobPosting().getCompany().getId(), company.getId())) {
            throw new UnauthorizedAccessException("UPDATE_APPLICATION", "You do not own the job posting for this application.");
        }

        application.setStatus(status);
        if (remarks != null && !remarks.trim().isEmpty()) {
            application.setRemarks(remarks.trim());
        }

        return jobApplicationRepository.save(application);
    }

    @Override
    public InterviewSchedule scheduleInterview(Long userId, InterviewScheduleDto dto) {
        CompanyProfile company = getCompanyProfileByUserId(userId);
        JobApplication application = jobApplicationRepository.findById(dto.getJobApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("JobApplication", "id", dto.getJobApplicationId()));

        // Ownership check
        if (!Objects.equals(application.getJobPosting().getCompany().getId(), company.getId())) {
            throw new UnauthorizedAccessException("SCHEDULE_INTERVIEW", "You do not own the job posting for this application.");
        }

        // Automatically advance application status to INTERVIEW_SCHEDULED
        application.setStatus(JobApplication.ApplicationStatus.INTERVIEW_SCHEDULED);
        jobApplicationRepository.save(application);

        InterviewSchedule interview = new InterviewSchedule();
        interview.setApplication(application);
        interview.setRoundName(dto.getRoundName().trim());
        interview.setScheduledDateTime(dto.getScheduledDateTime());
        interview.setVenueOrLink(dto.getVenueOrLink().trim());
        interview.setInterviewerName(dto.getInterviewerName() != null ? dto.getInterviewerName().trim() : null);
        interview.setFeedback(dto.getFeedback() != null ? dto.getFeedback().trim() : null);
        interview.setStatus(dto.getStatus() != null ? dto.getStatus() : InterviewSchedule.InterviewStatus.SCHEDULED);

        return interviewScheduleRepository.save(interview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewSchedule> getCompanyInterviews(Long userId) {
        CompanyProfile company = getCompanyProfileByUserId(userId);
        return interviewScheduleRepository.findByApplicationJobPostingCompanyIdOrderByScheduledDateTimeAsc(company.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public long getApplicantCount(Long userId) {
        CompanyProfile company = getCompanyProfileByUserId(userId);
        return jobApplicationRepository.countByJobPostingCompanyId(company.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public long getShortlistedCount(Long userId) {
        CompanyProfile company = getCompanyProfileByUserId(userId);
        return jobApplicationRepository.countByJobPostingCompanyIdAndStatus(
                company.getId(), JobApplication.ApplicationStatus.SHORTLISTED);
    }
}
