package com.placement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * InterviewSchedule - Models interview sessions organized by companies for shortlisted applicants.
 *
 * Demonstrates:
 * - OOP Encapsulation: Status progression and date-time queries are evaluated via methods.
 * - Relational Entity Mapping: Many-to-one relationship with JobApplication.
 */
@Entity
@Table(name = "interview_schedules", indexes = {
        @Index(name = "idx_interview_application", columnList = "job_application_id"),
        @Index(name = "idx_interview_status", columnList = "status")
})
public class InterviewSchedule {

    public enum InterviewStatus {
        SCHEDULED("Scheduled", "badge-info"),
        COMPLETED("Completed", "badge-success"),
        CANCELLED("Cancelled", "badge-danger"),
        RESCHEDULED("Rescheduled", "badge-warning");

        private final String displayName;
        private final String badgeClass;

        InterviewStatus(String displayName, String badgeClass) {
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
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication application;

    @Column(name = "round_name", nullable = false, length = 100)
    private String roundName;

    @Column(name = "scheduled_date_time", nullable = false)
    private LocalDateTime scheduledDateTime;

    @Column(name = "venue_or_link", nullable = false, length = 255)
    private String venueOrLink;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InterviewStatus status = InterviewStatus.SCHEDULED;

    @Column(name = "interviewer_name", length = 100)
    private String interviewerName;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public InterviewSchedule() {
    }

    public InterviewSchedule(JobApplication application, String roundName,
                             LocalDateTime scheduledDateTime, String venueOrLink) {
        this.application = application;
        this.roundName = roundName;
        this.scheduledDateTime = scheduledDateTime;
        this.venueOrLink = venueOrLink;
        this.status = InterviewStatus.SCHEDULED;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = InterviewStatus.SCHEDULED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Domain query methods
    public boolean isUpcoming() {
        return this.status == InterviewStatus.SCHEDULED
                && this.scheduledDateTime != null
                && this.scheduledDateTime.isAfter(LocalDateTime.now());
    }

    public boolean isCompleted() {
        return this.status == InterviewStatus.COMPLETED;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JobApplication getApplication() {
        return application;
    }

    public void setApplication(JobApplication application) {
        this.application = application;
    }

    public String getRoundName() {
        return roundName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public String getVenueOrLink() {
        return venueOrLink;
    }

    public void setVenueOrLink(String venueOrLink) {
        this.venueOrLink = venueOrLink;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
    }

    public String getInterviewerName() {
        return interviewerName;
    }

    public void setInterviewerName(String interviewerName) {
        this.interviewerName = interviewerName;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
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
        InterviewSchedule that = (InterviewSchedule) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
