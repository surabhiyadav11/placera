package com.placement.repository;

import com.placement.entity.CompanyProfile;
import com.placement.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JobPostingRepository - Spring Data JPA repository for JobPosting persistence.
 *
 * Demonstrates:
 * - OOP Abstraction: Encapsulates query logic to enforce that students only view
 *   active job postings published by APPROVED companies.
 */
@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    List<JobPosting> findByCompanyId(Long companyId);

    List<JobPosting> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    List<JobPosting> findByActiveTrueAndCompany_ApprovalStatusOrderByCreatedAtDesc(CompanyProfile.ApprovalStatus status);

    long countByActiveTrue();

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndActiveTrue(Long companyId);

    @Query("SELECT j FROM JobPosting j WHERE j.active = true AND j.company.approvalStatus = 'APPROVED' AND (" +
           "LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.jobRole) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.company.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.requiredSkills) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<JobPosting> searchActiveJobs(@Param("keyword") String keyword);
}
