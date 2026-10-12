package com.placement.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * JobPostingDto - Data Transfer Object encapsulating job creation and editing forms.
 *
 * Demonstrates:
 * - OOP Encapsulation: Validates user input before transforming into the persistent JobPosting entity.
 */
public class JobPostingDto {

    private Long id;

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job role is required")
    private String jobRole;

    private String jobType = "Full-Time";

    @NotBlank(message = "Job location is required")
    private String location;

    @NotNull(message = "Salary package is required")
    @DecimalMin(value = "0.0", message = "Salary package cannot be negative")
    private Double salaryPackage;

    @NotNull(message = "Minimum CGPA criteria is required")
    @DecimalMin(value = "0.0", message = "Minimum CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "Minimum CGPA cannot exceed 10.0")
    private Double minCgpa;

    private String eligibleBranches = "ALL";

    private Integer eligibleGraduationYear;

    private String requiredSkills;

    private String description;

    @NotNull(message = "Application deadline is required")
    private LocalDate applicationDeadline;

    private boolean active = true;

    public JobPostingDto() {
    }

    public JobPostingDto(String title, String jobRole, String location, Double salaryPackage,
                         Double minCgpa, LocalDate applicationDeadline) {
        this.title = title;
        this.jobRole = jobRole;
        this.location = location;
        this.salaryPackage = salaryPackage;
        this.minCgpa = minCgpa;
        this.applicationDeadline = applicationDeadline;
        this.active = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getSalaryPackage() {
        return salaryPackage;
    }

    public void setSalaryPackage(Double salaryPackage) {
        this.salaryPackage = salaryPackage;
    }

    public Double getMinCgpa() {
        return minCgpa;
    }

    public void setMinCgpa(Double minCgpa) {
        this.minCgpa = minCgpa;
    }

    public String getEligibleBranches() {
        return eligibleBranches;
    }

    public void setEligibleBranches(String eligibleBranches) {
        this.eligibleBranches = eligibleBranches;
    }

    public Integer getEligibleGraduationYear() {
        return eligibleGraduationYear;
    }

    public void setEligibleGraduationYear(Integer eligibleGraduationYear) {
        this.eligibleGraduationYear = eligibleGraduationYear;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDate applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
