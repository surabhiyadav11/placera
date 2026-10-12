package com.placement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * PlacementDrive - Represents an institutional placement drive or recruitment campaign.
 *
 * Demonstrates:
 * - OOP Abstraction: Encapsulates campaign scheduling and timeline verification.
 * - Enums for domain-specific lifecycle management.
 */
@Entity
@Table(name = "placement_drives", indexes = {
        @Index(name = "idx_drive_status", columnList = "status"),
        @Index(name = "idx_drive_academic_year", columnList = "academic_year")
})
public class PlacementDrive {

    public enum DriveStatus {
        UPCOMING("Upcoming", "badge-warning"),
        ONGOING("Ongoing", "badge-success"),
        COMPLETED("Completed", "badge-secondary");

        private final String displayName;
        private final String badgeClass;

        DriveStatus(String displayName, String badgeClass) {
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

    @Column(name = "drive_name", nullable = false, length = 150)
    private String driveName;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(length = 100)
    private String coordinator;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DriveStatus status = DriveStatus.UPCOMING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PlacementDrive() {
    }

    public PlacementDrive(String driveName, String academicYear, LocalDate startDate, LocalDate endDate) {
        this.driveName = driveName;
        this.academicYear = academicYear;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = DriveStatus.UPCOMING;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = DriveStatus.UPCOMING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business domain methods
    public boolean isOngoing() {
        return this.status == DriveStatus.ONGOING;
    }

    public boolean isCompleted() {
        return this.status == DriveStatus.COMPLETED;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDriveName() {
        return driveName;
    }

    public void setDriveName(String driveName) {
        this.driveName = driveName;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(String coordinator) {
        this.coordinator = coordinator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DriveStatus getStatus() {
        return status;
    }

    public void setStatus(DriveStatus status) {
        this.status = status;
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
        PlacementDrive that = (PlacementDrive) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
