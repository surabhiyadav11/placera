package com.placement.controller;

import com.placement.dto.CompanyRegisterDto;
import com.placement.dto.StudentRegisterDto;
import com.placement.exception.DuplicateResourceException;
import com.placement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AuthController - Manages user authentication, registration forms, and 403 access denial.
 *
 * Demonstrates:
 * - OOP Constructor Injection: Injects UserService.
 * - Server-side Form Validation: Applies Jakarta Bean Validation to user registration payloads.
 * - Prevents public admin registration: Public endpoints only allow STUDENT or COMPANY onboarding.
 */
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(Authentication authentication,
                            @RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/";
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please verify your credentials.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }

        return "auth/login";
    }

    @GetMapping("/register")
    public String registerSelection() {
        return "auth/register-select";
    }

    @GetMapping("/register/student")
    public String studentRegisterForm(Model model) {
        if (!model.containsAttribute("studentDto")) {
            model.addAttribute("studentDto", new StudentRegisterDto());
        }
        return "auth/register-student";
    }

    @PostMapping("/register/student")
    public String processStudentRegistration(@Valid @ModelAttribute("studentDto") StudentRegisterDto dto,
                                             BindingResult bindingResult,
                                             Model model,
                                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/register-student";
        }

        try {
            userService.registerStudent(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Student registration successful! You can now log in with your credentials.");
            return "redirect:/login";
        } catch (DuplicateResourceException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/register-student";
        }
    }

    @GetMapping("/register/company")
    public String companyRegisterForm(Model model) {
        if (!model.containsAttribute("companyDto")) {
            model.addAttribute("companyDto", new CompanyRegisterDto());
        }
        return "auth/register-company";
    }

    @PostMapping("/register/company")
    public String processCompanyRegistration(@Valid @ModelAttribute("companyDto") CompanyRegisterDto dto,
                                             BindingResult bindingResult,
                                             Model model,
                                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/register-company";
        }

        try {
            userService.registerCompany(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Company registered successfully! Your profile is pending Placement Cell verification.");
            return "redirect:/login";
        } catch (DuplicateResourceException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/register-company";
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("statusCode", 403);
        model.addAttribute("errorTitle", "Access Denied");
        model.addAttribute("errorMessage", "You do not possess the required authorization to view this portal or resource.");
        return "error/403";
    }
}
