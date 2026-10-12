package com.placement.controller;

import com.placement.entity.CompanyProfile;
import com.placement.entity.Role;
import com.placement.repository.CompanyProfileRepository;
import com.placement.repository.JobPostingRepository;
import com.placement.service.AdminService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * HomeController - Handles public landing, campus placement highlights, and about pages.
 *
 * Demonstrates:
 * - OOP Constructor Injection: Injects collaborating services cleanly.
 * - Dynamic Portal Redirection: Automatically forwards authenticated users to their assigned dashboard.
 */
@Controller
public class HomeController {

    private final AdminService adminService;
    private final JobPostingRepository jobPostingRepository;
    private final CompanyProfileRepository companyProfileRepository;

    public HomeController(AdminService adminService,
                          JobPostingRepository jobPostingRepository,
                          CompanyProfileRepository companyProfileRepository) {
        this.adminService = adminService;
        this.jobPostingRepository = jobPostingRepository;
        this.companyProfileRepository = companyProfileRepository;
    }

    @GetMapping({"/", "/home"})
    public String index(Authentication authentication, Model model) {
        // If user is already authenticated, forward directly to their respective portal
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                String role = authority.getAuthority();
                if (Role.ROLE_ADMIN.getAuthority().equals(role)) {
                    return "redirect:/admin/dashboard";
                } else if (Role.ROLE_COMPANY.getAuthority().equals(role)) {
                    return "redirect:/company/dashboard";
                } else if (Role.ROLE_STUDENT.getAuthority().equals(role)) {
                    return "redirect:/student/dashboard";
                }
            }
        }

        // Supply public landing stats and recruiter showcase
        model.addAttribute("stats", adminService.getDashboardStatistics());
        model.addAttribute("featuredJobs", jobPostingRepository.findByActiveTrueAndCompany_ApprovalStatusOrderByCreatedAtDesc(
                CompanyProfile.ApprovalStatus.APPROVED));
        model.addAttribute("approvedCompanies", companyProfileRepository.findByApprovalStatus(
                CompanyProfile.ApprovalStatus.APPROVED));

        return "home/index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("stats", adminService.getDashboardStatistics());
        return "home/about";
    }
}
