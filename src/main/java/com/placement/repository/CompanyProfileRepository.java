package com.placement.repository;

import com.placement.entity.CompanyProfile;
import com.placement.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CompanyProfileRepository - Spring Data JPA repository for CompanyProfile persistence.
 *
 * Demonstrates:
 * - OOP Abstraction: Declares corporate data access and approval status filtering.
 */
@Repository
public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {

    Optional<CompanyProfile> findByUserAccount(UserAccount userAccount);

    Optional<CompanyProfile> findByUserAccountId(Long userAccountId);

    List<CompanyProfile> findByApprovalStatus(CompanyProfile.ApprovalStatus approvalStatus);

    List<CompanyProfile> findByApprovalStatusOrderByCreatedAtDesc(CompanyProfile.ApprovalStatus approvalStatus);

    long countByApprovalStatus(CompanyProfile.ApprovalStatus approvalStatus);

    boolean existsByCompanyName(String companyName);

    @Query("SELECT c FROM CompanyProfile c WHERE " +
           "LOWER(c.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.contactPerson) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.industry) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<CompanyProfile> searchCompanies(@Param("keyword") String keyword);
}
