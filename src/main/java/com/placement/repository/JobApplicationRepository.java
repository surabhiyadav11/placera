package com.placement.repository;

import com.placement.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JobApplicationRepository - Spring Data JPA repository for JobApplication persistence.
 *
 * Demonstrates:
 * - OOP Abstraction: Interface contract managing application workflows.
 * - Duplicate verification: existsByStudentIdAndJobPostingId query check.
 * - Aggregate placement statistics: countDistinctPlacedStudents query.
 */
@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Optional<JobApplication> findByStudentIdAndJobPostingId(Long studentId, Long jobPostingId);

    boolean existsByStudentIdAndJobPostingId(Long studentId, Long jobPostingId);

    List<JobApplication> findByStudentIdOrderByAppliedAtDesc(Long studentId);

    List<JobApplication> findByJobPostingIdOrderByAppliedAtDesc(Long jobPostingId);

    List<JobApplication> findByJobPostingCompanyIdOrderByAppliedAtDesc(Long companyId);

    List<JobApplication> findByJobPostingCompanyIdAndStatusOrderByAppliedAtDesc(Long companyId, JobApplication.ApplicationStatus status);

    long countByStudentId(Long studentId);

    long countByJobPostingCompanyId(Long companyId);

    long countByJobPostingCompanyIdAndStatus(Long companyId, JobApplication.ApplicationStatus status);

    long countByStatus(JobApplication.ApplicationStatus status);

    @Query("SELECT COUNT(DISTINCT a.student.id) FROM JobApplication a WHERE a.status = com.placement.entity.JobApplication.ApplicationStatus.SELECTED")
    long countDistinctPlacedStudents();
}
