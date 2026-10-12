package com.placement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JobPosting - Encapsulates placement job openings published by verified companies.
 *
 * Demonstrates:
 * - OOP Encapsulation: Eligibility, deadline validity, and active state are evaluated via methods.
 * - Multi-entity Collaboration: isStudentEligible() coordinates requirements against StudentProfile.
 * - Relational Mapping: Many-to-one relationship with CompanyProfile.
 */
@Entity
@Table(name = "job_postings", indexes = {
        @Index(name = "idx_job_company", columnList = "company_profile_id"),
        @Index(name = "idx_job_active", columnList = "active")
})
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_profile_id", nullable = false)
    private CompanyProfile company;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "job_role", length = 100)
    private String jobRole;

    @Column(name = "job_type", length = 50)
    private String jobType = "Full-Time";

    @Column(length = 100)
    private String location;

    @Column(name = "salary_package")
    private Double salaryPackage; // in LPA (Lakhs Per Annum)

    @Column(name = "min_cgpa")
    private Double minCgpa;

    @Column(name = "eligible_branches", length = 200)
    private String eligibleBranches = "ALL";

    @Column(name = "eligible_graduation_year")
    private Integer eligibleGraduationYear;

    @Column(name = "required_skills", columnDefinition = "TEXT")
    private String requiredSkills;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "application_deadline")
    private LocalDate applicationDeadline;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public JobPosting() {
    }

    public JobPosting(CompanyProfile company, String title, String jobRole,
                      Double salaryPackage, Double minCgpa, LocalDate applicationDeadline) {
        this.company = company;
        this.title = title;
        this.jobRole = jobRole;
        this.salaryPackage = salaryPackage;
        this.minCgpa = minCgpa;
        this.applicationDeadline = applicationDeadline;
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business Methods
    public boolean isDeadlinePassed() {
        return this.applicationDeadline != null && LocalDate.now().isAfter(this.applicationDeadline);
    }

    public boolean isOpenForApplication() {
        boolean companyApproved = (this.company != null && this.company.isApproved());
        return this.active && companyApproved && !isDeadlinePassed();
    }

    public boolean isStudentEligible(StudentProfile student) {
        if (!isOpenForApplication() || student == null) {
            return false;
        }
        return student.isEligible(this.minCgpa, this.eligibleBranches, this.eligibleGraduationYear);
    }

    public String getFormattedSalary() {
        return this.salaryPackage != null ? String.format("%.1f LPA", this.salaryPackage) : "Disclosed on Selection";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CompanyProfile getCompany() {
        return company;
    }

    public void setCompany(CompanyProfile company) {
        this.company = company;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobPosting that = (JobPosting) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
