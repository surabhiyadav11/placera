package com.placement.controller;

import com.placement.entity.*;
import com.placement.exception.DuplicateResourceException;
import com.placement.exception.UnauthorizedAccessException;
import com.placement.service.StudentService;
import com.placement.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StudentController - Handles all operations for the Student Portal (/student/**).
 *
 * Demonstrates:
 * - Genuine Server-Side Authorization: Access is guarded by Spring Security ROLE_STUDENT.
 * - Horizontal Ownership Security: Automatically scopes all data access to the authenticated student.
 * - Dynamic Eligibility Verification: Assesses academic requirements before application submission.
 */
@Controller
@RequestMapping("/student")
public class StudentController {

    private final UserService userService;
    private final StudentService studentService;

    public StudentController(UserService userService, StudentService studentService) {
        this.userService = userService;
        this.studentService = studentService;
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
        StudentProfile profile = studentService.getProfileByUserId(user.getId());
        List<JobApplication> applications = studentService.getStudentApplications(user.getId());
        List<InterviewSchedule> interviews = studentService.getStudentInterviews(user.getId());
        List<JobPosting> availableJobs = studentService.getAvailableJobPostings(user.getId(), null);

        long appliedCount = applications.size();
        long shortlistedCount = applications.stream().filter(JobApplication::isShortlisted).count();
        long interviewCount = interviews.size();
        long selectedCount = applications.stream().filter(JobApplication::isSelected).count();

        model.addAttribute("profile", profile);
        model.addAttribute("user", user);
        model.addAttribute("appliedCount", appliedCount);
        model.addAttribute("shortlistedCount", shortlistedCount);
        model.addAttribute("interviewCount", interviewCount);
        model.addAttribute("selectedCount", selectedCount);
        model.addAttribute("recentApplications", applications.stream().limit(5).toList());
        model.addAttribute("upcomingInterviews", interviews.stream().filter(InterviewSchedule::isUpcoming).limit(5).toList());
        model.addAttribute("eligibleJobs", availableJobs.stream().filter(job -> job.isStudentEligible(profile)).limit(4).toList());

        return "student/dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        UserAccount user = getAuthenticatedUser();
        StudentProfile profile = studentService.getProfileByUserId(user.getId());
        model.addAttribute("profile", profile);
        model.addAttribute("user", user);
        return "student/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("profile") StudentProfile updatedProfile,
                                RedirectAttributes redirectAttributes) {
        UserAccount user = getAuthenticatedUser();
        studentService.updateProfile(user.getId(), updatedProfile);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/student/profile";
    }

    @PostMapping("/resume")
    public String uploadResume(@RequestParam("resume") MultipartFile file,
                               RedirectAttributes redirectAttributes) {
        UserAccount user = getAuthenticatedUser();
        try {
            studentService.storeResume(user.getId(), file);
            redirectAttributes.addFlashAttribute("successMessage", "Resume uploaded successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error uploading resume: " + ex.getMessage());
        }
        return "redirect:/student/profile";
    }

    @GetMapping("/jobs")
    public String browseJobs(@RequestParam(value = "keyword", required = false) String keyword,
                             Model model) {
        UserAccount user = getAuthenticatedUser();
        StudentProfile profile = studentService.getProfileByUserId(user.getId());
        List<JobPosting> jobs = studentService.getAvailableJobPostings(user.getId(), keyword);

        // Compute eligibility and applied status map for quick rendering
        Map<Long, Boolean> eligibilityMap = new HashMap<>();
        Map<Long, Boolean> appliedMap = new HashMap<>();

        for (JobPosting job : jobs) {
            eligibilityMap.put(job.getId(), job.isStudentEligible(profile));
            appliedMap.put(job.getId(), studentService.hasApplied(user.getId(), job.getId()));
        }

        model.addAttribute("profile", profile);
        model.addAttribute("jobs", jobs);
        model.addAttribute("eligibilityMap", eligibilityMap);
        model.addAttribute("appliedMap", appliedMap);
        model.addAttribute("keyword", keyword);

        return "student/jobs";
    }

    @GetMapping("/jobs/{id}")
    public String viewJobDetails(@PathVariable("id") Long jobId, Model model) {
        UserAccount user = getAuthenticatedUser();
        StudentProfile profile = studentService.getProfileByUserId(user.getId());
        JobPosting job = studentService.getJobPostingDetails(jobId);

        boolean eligible = job.isStudentEligible(profile);
        boolean alreadyApplied = studentService.hasApplied(user.getId(), jobId);

        model.addAttribute("profile", profile);
        model.addAttribute("job", job);
        model.addAttribute("eligible", eligible);
        model.addAttribute("alreadyApplied", alreadyApplied);

        return "student/job-details";
    }

    @PostMapping("/jobs/{id}/apply")
    public String applyForJob(@PathVariable("id") Long jobId,
                              @RequestParam(value = "coverNote", required = false) String coverNote,
                              RedirectAttributes redirectAttributes) {
        UserAccount user = getAuthenticatedUser();
        try {
            studentService.applyForJob(user.getId(), jobId, coverNote);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Application submitted successfully! You can track its status under 'My Applications'.");
            return "redirect:/student/applications";
        } catch (DuplicateResourceException ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Duplicate application prevented: You have already applied for this opening.");
            return "redirect:/student/jobs/" + jobId;
        } catch (UnauthorizedAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/student/jobs/" + jobId;
        }
    }

    @GetMapping("/applications")
    public String trackApplications(Model model) {
        UserAccount user = getAuthenticatedUser();
        StudentProfile profile = studentService.getProfileByUserId(user.getId());
        List<JobApplication> applications = studentService.getStudentApplications(user.getId());

        model.addAttribute("profile", profile);
        model.addAttribute("applications", applications);

        return "student/applications";
    }

    @GetMapping("/interviews")
    public String viewInterviews(Model model) {
        UserAccount user = getAuthenticatedUser();
        StudentProfile profile = studentService.getProfileByUserId(user.getId());
        List<InterviewSchedule> interviews = studentService.getStudentInterviews(user.getId());

        model.addAttribute("profile", profile);
        model.addAttribute("interviews", interviews);

        return "student/interviews";
    }
}
