package com.placement.controller;

import com.placement.dto.AdminDashboardStats;
import com.placement.entity.CompanyProfile;
import com.placement.entity.JobApplication;
import com.placement.entity.JobPosting;
import com.placement.entity.PlacementDrive;
import com.placement.entity.StudentProfile;
import com.placement.service.AdminService;
import com.placement.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * AdminController - Handles institutional placement administration (/admin/**).
 *
 * Demonstrates:
 * - Genuine Server-Side Authorization: Access guarded by Spring Security ROLE_ADMIN.
 * - Institutional Oversight: Real-time placement analytics, company verification queue,
 *   student moderation, and placement campaign scheduling.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        AdminDashboardStats stats = adminService.getDashboardStatistics();
        List<CompanyProfile> pendingCompanies = adminService.getPendingCompanies();
        List<JobApplication> recentApplications = adminService.getAllApplications().stream().limit(8).toList();
        List<JobPosting> activeJobs = adminService.getAllJobPostings().stream().filter(JobPosting::isActive).limit(6).toList();

        model.addAttribute("stats", stats);
        model.addAttribute("pendingCompanies", pendingCompanies);
        model.addAttribute("recentApplications", recentApplications);
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("adminUser", userService.getCurrentAuthenticatedUser());

        return "admin/dashboard";
    }

    @GetMapping("/companies")
    public String manageCompanies(@RequestParam(value = "keyword", required = false) String keyword,
                                  Model model) {
        List<CompanyProfile> companies = adminService.getAllCompanies(keyword);
        model.addAttribute("companies", companies);
        model.addAttribute("pendingCount", adminService.getPendingCompanies().size());
        model.addAttribute("keyword", keyword);
        return "admin/companies";
    }

    @PostMapping("/companies/{id}/approve")
    public String approveCompany(@PathVariable("id") Long companyId,
                                 RedirectAttributes redirectAttributes) {
        adminService.approveCompany(companyId);
        redirectAttributes.addFlashAttribute("successMessage",
                "Company account approved successfully! They may now publish job openings.");
        return "redirect:/admin/companies";
    }

    @PostMapping("/companies/{id}/reject")
    public String rejectCompany(@PathVariable("id") Long companyId,
                                @RequestParam(value = "reason", required = false) String reason,
                                RedirectAttributes redirectAttributes) {
        adminService.rejectCompany(companyId, reason);
        redirectAttributes.addFlashAttribute("errorMessage",
                "Company account has been rejected.");
        return "redirect:/admin/companies";
    }

    @GetMapping("/students")
    public String manageStudents(@RequestParam(value = "keyword", required = false) String keyword,
                                 Model model) {
        List<StudentProfile> students = adminService.getAllStudents(keyword);
        model.addAttribute("students", students);
        model.addAttribute("keyword", keyword);
        return "admin/students";
    }

    @PostMapping("/students/{id}/toggle")
    public String toggleStudentAccount(@PathVariable("id") Long studentId,
                                       @RequestParam("enabled") boolean enabled,
                                       RedirectAttributes redirectAttributes) {
        StudentProfile student = adminService.getStudentById(studentId);
        adminService.toggleUserAccount(student.getUserAccount().getId(), enabled);
        String msg = enabled ? "Student account enabled." : "Student account disabled.";
        redirectAttributes.addFlashAttribute("successMessage", msg);
        return "redirect:/admin/students";
    }

    @GetMapping("/jobs")
    public String manageJobs(Model model) {
        List<JobPosting> jobs = adminService.getAllJobPostings();
        model.addAttribute("jobs", jobs);
        return "admin/jobs";
    }

    @GetMapping("/applications")
    public String manageApplications(Model model) {
        List<JobApplication> applications = adminService.getAllApplications();
        model.addAttribute("applications", applications);
        return "admin/applications";
    }

    @GetMapping("/drives")
    public String manageDrives(Model model) {
        List<PlacementDrive> drives = adminService.getAllPlacementDrives();
        model.addAttribute("drives", drives);
        model.addAttribute("newDrive", new PlacementDrive());
        return "admin/drives";
    }

    @PostMapping("/drives/new")
    public String createDrive(@ModelAttribute("newDrive") PlacementDrive drive,
                              RedirectAttributes redirectAttributes) {
        adminService.createPlacementDrive(drive);
        redirectAttributes.addFlashAttribute("successMessage", "Placement drive created successfully.");
        return "redirect:/admin/drives";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        AdminDashboardStats stats = adminService.getDashboardStatistics();
        List<JobApplication> applications = adminService.getAllApplications();
        List<StudentProfile> students = adminService.getAllStudents(null);
        List<JobPosting> jobs = adminService.getAllJobPostings();

        long selectedCount = applications.stream().filter(JobApplication::isSelected).count();
        long rejectedCount = applications.stream().filter(JobApplication::isRejected).count();
        long shortlistedCount = applications.stream().filter(JobApplication::isShortlisted).count();
        long interviewScheduledCount = applications.stream().filter(JobApplication::isInterviewScheduled).count();

        model.addAttribute("stats", stats);
        model.addAttribute("students", students);
        model.addAttribute("jobs", jobs);
        model.addAttribute("selectedCount", selectedCount);
        model.addAttribute("rejectedCount", rejectedCount);
        model.addAttribute("shortlistedCount", shortlistedCount);
        model.addAttribute("interviewScheduledCount", interviewScheduledCount);

        return "admin/reports";
    }
}
