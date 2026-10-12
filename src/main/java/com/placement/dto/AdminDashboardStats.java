package com.placement.dto;

/**
 * AdminDashboardStats - Encapsulates institutional placement statistics and KPI metrics.
 *
 * Demonstrates:
 * - OOP Encapsulation: Groups related metrics into a single cohesive value object.
 * - Presentation helper methods for formatted viva and reporting outputs.
 */
public class AdminDashboardStats {

    private long totalStudents;
    private long placedStudents;
    private double placementPercentage;
    private long totalCompanies;
    private long pendingCompanies;
    private long activeJobOpenings;
    private long totalApplications;
    private long totalInterviewsScheduled;

    public AdminDashboardStats() {
    }

    public AdminDashboardStats(long totalStudents, long placedStudents, double placementPercentage,
                               long totalCompanies, long pendingCompanies, long activeJobOpenings,
                               long totalApplications, long totalInterviewsScheduled) {
        this.totalStudents = totalStudents;
        this.placedStudents = placedStudents;
        this.placementPercentage = placementPercentage;
        this.totalCompanies = totalCompanies;
        this.pendingCompanies = pendingCompanies;
        this.activeJobOpenings = activeJobOpenings;
        this.totalApplications = totalApplications;
        this.totalInterviewsScheduled = totalInterviewsScheduled;
    }

    public String getFormattedPlacementPercentage() {
        return String.format("%.1f%%", this.placementPercentage);
    }

    // Getters and Setters
    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getPlacedStudents() {
        return placedStudents;
    }

    public void setPlacedStudents(long placedStudents) {
        this.placedStudents = placedStudents;
    }

    public double getPlacementPercentage() {
        return placementPercentage;
    }

    public void setPlacementPercentage(double placementPercentage) {
        this.placementPercentage = placementPercentage;
    }

    public long getTotalCompanies() {
        return totalCompanies;
    }

    public void setTotalCompanies(long totalCompanies) {
        this.totalCompanies = totalCompanies;
    }

    public long getPendingCompanies() {
        return pendingCompanies;
    }

    public void setPendingCompanies(long pendingCompanies) {
        this.pendingCompanies = pendingCompanies;
    }

    public long getActiveJobOpenings() {
        return activeJobOpenings;
    }

    public void setActiveJobOpenings(long activeJobOpenings) {
        this.activeJobOpenings = activeJobOpenings;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public long getTotalInterviewsScheduled() {
        return totalInterviewsScheduled;
    }

    public void setTotalInterviewsScheduled(long totalInterviewsScheduled) {
        this.totalInterviewsScheduled = totalInterviewsScheduled;
    }
}
