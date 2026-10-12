package com.placement.controller;

import com.placement.dto.InterviewScheduleDto;
import com.placement.dto.JobPostingDto;
import com.placement.entity.*;
import com.placement.exception.UnauthorizedAccessException;
import com.placement.service.CompanyService;
import com.placement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * CompanyController - Handles all operations for the Company Portal (/company/**).
 *
 * Demonstrates:
 * - Genuine Server-Side Authorization: Access guarded by Spring Security ROLE_COMPANY.
 * - Horizontal Ownership Security: Companies can only view and manage their own postings and candidates.
 * - Administrative Policy Check: Unapproved companies cannot publish active openings.
 */
@Controller
@RequestMapping("/company")
public class CompanyController {

    private final UserService userService;
    private final CompanyService companyService;

    public CompanyController(UserService userService, CompanyService companyService) {
        this.userService = userService;
        this.companyService = companyService;
    }

    private UserAccount getAuthenticatedUser() {
        UserAccount user = userService.getCurrentAuthenticatedUser();
        if (user == null) {
            throw new UnauthorizedAccessException("AUTH", "You must be authenticated to access this resource.");
        }
        return user;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        UserAccount user = getAuthenticatedUser();
        CompanyProfile profile = companyService.getCompanyProfileByUserId(user.getId());
        List<JobPosting> jobs = companyService.getCompanyJobPostings(user.getId());
        List<JobApplication> applicants = companyService.getAllApplicantsForCompany(user.getId());
        List<InterviewSchedule> interviews = companyService.getCompanyInterviews(user.getId());

        long activeJobsCount = jobs.stream().filter(JobPosting::isActive).count();
        long totalApplicants = applicants.size();
        long shortlistedCount = applicants.stream().filter(JobApplication::isShortlisted).count();
        long scheduledInterviews = interviews.size();

        model.addAttribute("profile", profile);
        model.addAttribute("user", user);
        model.addAttribute("activeJobsCount", activeJobsCount);
        model.addAttribute("totalApplicants", totalApplicants);
        model.addAttribute("shortlistedCount", shortlistedCount);
        model.addAttribute("scheduledInterviews", scheduledInterviews);
        model.addAttribute("recentJobs", jobs.stream().limit(5).toList());
        model.addAttribute("recentApplicants", applicants.stream().limit(5).toList());
        model.addAttribute("upcomingInterviews", interviews.stream().filter(InterviewSchedule::isUpcoming).limit(5).toList());

        return "company/dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        UserAccount user = getAuthenticatedUser();
        CompanyProfile profile = companyService.getCompanyProfileByUserId(user.getId());
        model.addAttribute("profile", profile);
        model.addAttribute("user", user);
        return "company/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("profile") CompanyProfile updatedProfile,
                                RedirectAttributes redirectAttributes) {
        UserAccount user = getAuthenticatedUser();
        companyService.updateProfile(user.getId(), updatedProfile);
        redirectAttributes.addFlashAttribute("successMessage", "Company profile updated successfully.");
        return "redirect:/company/profile";
    }

    @GetMapping("/jobs")
    public String manageJobs(Model model) {
        UserAccount user = getAuthenticatedUser();
        CompanyProfile profile = companyService.getCompanyProfileByUserId(user.getId());
        List<JobPosting> jobs = companyService.getCompanyJobPostings(user.getId());

        model.addAttribute("profile", profile);
        model.addAttribute("jobs", jobs);
        return "company/jobs";
    }

    @GetMapping("/jobs/new")
    public String createJobForm(Model model) {
        UserAccount user = getAuthenticatedUser();
        CompanyProfile profile = companyService.getCompanyProfileByUserId(user.getId());

        if (!profile.isApproved()) {
            model.addAttribute("errorMessage",
                    "Your company registration is currently pending admin approval. You cannot publish jobs until approved.");
        }

        if (!model.containsAttribute("jobDto")) {
            model.addAttribute("jobDto", new JobPostingDto());
        }
        model.addAttribute("profile", profile);
        return "company/job-create";
    }

    @PostMapping("/jobs/new")
    public String processCreateJob(@Valid @ModelAttribute("jobDto") JobPostingDto dto,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        UserAccount user = getAuthenticatedUser();
        if (bindingResult.hasErrors()) {
            CompanyProfile profile = companyService.getCompanyProfileByUserId(user.getId());
            model.addAttribute("profile", profile);
            return "company/job-create";
        }

        try {
            companyService.createJobPosting(user.getId(), dto);
            redirectAttributes.addFlashAttribute("successMessage", "Job opening published successfully!");
            return "redirect:/company/jobs";
        } catch (UnauthorizedAccessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            CompanyProfile profile = companyService.getCompanyProfileByUserId(user.getId());
            model.addAttribute("profile", profile);
            return "company/job-create";
        }
    }

    @GetMapping("/jobs/{id}/edit")
    public String editJobForm(@PathVariable("id") Long jobId, Model model) {
        UserAccount user = getAuthenticatedUser();
        CompanyProfile profile = companyService.getCompanyProfileByUserId(user.getId());
        JobPosting job = companyService.getJobPostingById(user.getId(), jobId);

        JobPostingDto dto = new JobPostingDto();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setJobRole(job.getJobRole());
        dto.setJobType(job.getJobType());
        dto.setLocation(job.getLocation());
        dto.setSalaryPackage(job.getSalaryPackage());
        dto.setMinCgpa(job.getMinCgpa());
        dto.setEligibleBranches(job.getEligibleBranches());
        dto.setEligibleGraduationYear(job.getEligibleGraduationYear());
        dto.setRequiredSkills(job.getRequiredSkills());
        dto.setDescription(job.getDescription());
        dto.setApplicationDeadline(job.getApplicationDeadline());
        dto.setActive(job.isActive());

        model.addAttribute("jobDto", dto);
        model.addAttribute("profile", profile);
        model.addAttribute("jobId", jobId);
        return "company/job-edit";
    }

    @PostMapping("/jobs/{id}/edit")
    public String processUpdateJob(@PathVariable("id") Long jobId,
                                   @Valid @ModelAttribute("jobDto") JobPostingDto dto,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        UserAccount user = getAuthenticatedUser();
        if (bindingResult.hasErrors()) {
            CompanyProfile profile = companyService.getCompanyProfileByUserId(user.getId());
            model.addAttribute("profile", profile);
            model.addAttribute("jobId", jobId);
            return "company/job-edit";
        }

        companyService.updateJobPosting(user.getId(), jobId, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Job opening updated successfully.");
        return "redirect:/company/jobs";
    }

    @PostMapping("/jobs/{id}/toggle")
    public String toggleJobStatus(@PathVariable("id") Long jobId,
                                  @RequestParam("active") boolean active,
                                  RedirectAttributes redirectAttributes) {
        UserAccount user = getAuthenticatedUser();
        companyService.toggleJobStatus(user.getId(), jobId, active);
        String msg = active ? "Job opening reactivated successfully." : "Job opening closed successfully.";
        redirectAttributes.addFlashAttribute("successMessage", msg);
        return "redirect:/company/jobs";
    }

    @GetMapping("/jobs/{id}/applicants")
    public String viewApplicantsForJob(@PathVariable("id") Long jobId, Model model) {
        UserAccount user = getAuthenticatedUser();
        CompanyProfile profile = companyService.getCompanyProfileByUserId(user.getId());
        JobPosting job = companyService.getJobPostingById(user.getId(), jobId);
        List<JobApplication> applicants = companyService.getApplicantsForJob(user.getId(), jobId);

        model.addAttribute("profile", profile);
        model.addAttribute("job", job);
        model.addAttribute("applicants", applicants);
        return "company/applicants";
    }

    @GetMapping("/applicants")
    public String viewAllApplicants(Model model) {
        UserAccount user = getAuthenticatedUser();
        CompanyProfile profile = companyService.getCompanyProfileByUserId(user.getId());
        List<JobApplication> applicants = companyService.getAllApplicantsForCompany(user.getId());

        model.addAttribute("profile", profile);
        model.addAttribute("applicants", applicants);
        return "company/applicants";
    }

    @PostMapping("/applications/{id}/status")
    public String updateApplicantStatus(@PathVariable("id") Long applicationId,
                                        @RequestParam("status") JobApplication.ApplicationStatus status,
                                        @RequestParam(value = "remarks", required = false) String remarks,
                                        RedirectAttributes redirectAttributes) {
        UserAccount user = getAuthenticatedUser();
        companyService.updateApplicationStatus(user.getId(), applicationId, status, remarks);
        redirectAttributes.addFlashAttribute("successMessage", "Candidate application status updated to: " + status.getDisplayName());
        return "redirect:/company/applicants";
    }

    @GetMapping("/applications/{id}/schedule")
    public String scheduleInterviewForm(@PathVariable("id") Long applicationId, Model model) {
        UserAccount user = getAuthenticatedUser();
        CompanyProfile profile = companyService.getCompanyProfileByUserId(user.getId());

        InterviewScheduleDto dto = new InterviewScheduleDto();
        dto.setJobApplicationId(applicationId);

        model.addAttribute("profile", profile);
        model.addAttribute("scheduleDto", dto);
        return "company/interview-schedule";
    }

    @PostMapping("/applications/{id}/schedule")
    public String processScheduleInterview(@PathVariable("id") Long applicationId,
                                           @Valid @ModelAttribute("scheduleDto") InterviewScheduleDto dto,
                                           BindingResult bindingResult,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {
        UserAccount user = getAuthenticatedUser();
        if (bindingResult.hasErrors()) {
            CompanyProfile profile = companyService.getCompanyProfileByUserId(user.getId());
            model.addAttribute("profile", profile);
            return "company/interview-schedule";
        }

        dto.setJobApplicationId(applicationId);
        companyService.scheduleInterview(user.getId(), dto);
        redirectAttributes.addFlashAttribute("successMessage", "Interview round scheduled successfully!");
        return "redirect:/company/interviews";
    }

    @GetMapping("/interviews")
    public String viewInterviews(Model model) {
        UserAccount user = getAuthenticatedUser();
        CompanyProfile profile = companyService.getCompanyProfileByUserId(user.getId());
        List<InterviewSchedule> interviews = companyService.getCompanyInterviews(user.getId());

        model.addAttribute("profile", profile);
        model.addAttribute("interviews", interviews);
        return "company/interviews";
    }
}
