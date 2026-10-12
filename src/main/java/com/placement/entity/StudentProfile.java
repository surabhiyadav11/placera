package com.placement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/**
 * StudentProfile - Encapsulates academic and personal credentials of a student candidate.
 *
 * Demonstrates:
 * - OOP Encapsulation: Strict data hiding with validated accessors.
 * - Business Domain Logic: isEligible() encapsulates eligibility criteria checking.
 * - Relational Entity Mapping: One-to-one relationship with UserAccount.
 */
@Entity
@Table(name = "student_profiles", indexes = {
        @Index(name = "idx_student_roll", columnList = "roll_number", unique = true)
})
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_account_id", nullable = false, unique = true)
    private UserAccount userAccount;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "roll_number", nullable = false, unique = true, length = 50)
    private String rollNumber;

    @Column(nullable = false, length = 80)
    private String branch;

    @Column(nullable = false)
    private Double cgpa;

    @Column(name = "graduation_year", nullable = false)
    private Integer graduationYear;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(name = "resume_path", length = 255)
    private String resumePath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public StudentProfile() {
    }

    public StudentProfile(UserAccount userAccount, String fullName, String rollNumber,
                          String branch, Double cgpa, Integer graduationYear) {
        this.userAccount = userAccount;
        this.fullName = fullName;
        this.rollNumber = rollNumber;
        this.branch = branch;
        this.cgpa = cgpa;
        this.graduationYear = graduationYear;
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

    /**
     * Domain method: Encapsulates academic qualification logic against a job posting's constraints.
     */
    public boolean isEligible(Double minCgpa, String eligibleBranches, Integer eligibleGradYear) {
        if (minCgpa != null && this.cgpa != null && this.cgpa < minCgpa) {
            return false;
        }

        if (eligibleGradYear != null && this.graduationYear != null &&
                !Objects.equals(this.graduationYear, eligibleGradYear)) {
            return false;
        }

        if (eligibleBranches != null && !eligibleBranches.trim().isEmpty() && this.branch != null) {
            String[] branches = eligibleBranches.split(",");
            boolean branchMatches = Arrays.stream(branches)
                    .map(String::trim)
                    .anyMatch(b -> b.equalsIgnoreCase("ALL") || b.equalsIgnoreCase(this.branch.trim()));
            if (!branchMatches) {
                return false;
            }
        }

        return true;
    }

    public String getFormattedCgpa() {
        return this.cgpa != null ? String.format("%.2f", this.cgpa) : "N/A";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getResumePath() {
        return resumePath;
    }

    public void setResumePath(String resumePath) {
        this.resumePath = resumePath;
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
        StudentProfile that = (StudentProfile) o;
        return Objects.equals(rollNumber, that.rollNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rollNumber);
    }
}
