package com.placement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JobApplication - Encapsulates an application submitted by an eligible student for a job opening.
 *
 * Demonstrates:
 * - Database Constraint Enforcement: Unique composite constraint (student_profile_id, job_posting_id)
 *   strictly prevents duplicate applications at the database level.
 * - Application Lifecycle State Machine: ApplicationStatus transitions through defined stages.
 * - Encapsulation & Presentation Helpers: Supplies status badge styling and query methods.
 */
@Entity
@Table(name = "job_applications", uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_job_application", columnNames = {"student_profile_id", "job_posting_id"})
}, indexes = {
        @Index(name = "idx_application_student", columnList = "student_profile_id"),
        @Index(name = "idx_application_job", columnList = "job_posting_id"),
        @Index(name = "idx_application_status", columnList = "status")
})
public class JobApplication {

    public enum ApplicationStatus {
        APPLIED("Applied", "badge-primary"),
        SHORTLISTED("Shortlisted", "badge-warning"),
        INTERVIEW_SCHEDULED("Interview Scheduled", "badge-info"),
        SELECTED("Selected", "badge-success"),
        REJECTED("Rejected", "badge-danger");

        private final String displayName;
        private final String badgeClass;

        ApplicationStatus(String displayName, String badgeClass) {
            this.displayName = displayName;
            this.badgeClass = badgeClass;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getBadgeClass() {
            return badgeClass;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(name = "cover_note", columnDefinition = "TEXT")
    private String coverNote;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public JobApplication() {
    }

    public JobApplication(StudentProfile student, JobPosting jobPosting) {
        this.student = student;
        this.jobPosting = jobPosting;
        this.status = ApplicationStatus.APPLIED;
    }

    public JobApplication(StudentProfile student, JobPosting jobPosting, String coverNote) {
        this.student = student;
        this.jobPosting = jobPosting;
        this.coverNote = coverNote;
        this.status = ApplicationStatus.APPLIED;
    }

    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ApplicationStatus.APPLIED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Domain query methods
    public boolean isSelected() {
        return this.status == ApplicationStatus.SELECTED;
    }

    public boolean isRejected() {
        return this.status == ApplicationStatus.REJECTED;
    }

    public boolean isShortlisted() {
        return this.status == ApplicationStatus.SHORTLISTED;
    }

    public boolean isInterviewScheduled() {
        return this.status == ApplicationStatus.INTERVIEW_SCHEDULED;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StudentProfile getStudent() {
        return student;
    }

    public void setStudent(StudentProfile student) {
        this.student = student;
    }

    public JobPosting getJobPosting() {
        return jobPosting;
    }

    public void setJobPosting(JobPosting jobPosting) {
        this.jobPosting = jobPosting;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getCoverNote() {
        return coverNote;
    }

    public void setCoverNote(String coverNote) {
        this.coverNote = coverNote;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
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
        JobApplication that = (JobApplication) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
