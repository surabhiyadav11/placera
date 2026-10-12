package com.placement.repository;

import com.placement.entity.StudentProfile;
import com.placement.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * StudentProfileRepository - Data access interface for student profile management.
 *
 * Demonstrates:
 * - OOP Abstraction: Interface-based persistence contract.
 * - Dynamic criteria searching via JPQL.
 */
@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    Optional<StudentProfile> findByUserAccount(UserAccount userAccount);

    Optional<StudentProfile> findByUserAccountId(Long userAccountId);

    Optional<StudentProfile> findByRollNumber(String rollNumber);

    boolean existsByRollNumber(String rollNumber);

    List<StudentProfile> findByBranch(String branch);

    List<StudentProfile> findByGraduationYear(Integer graduationYear);

    @Query("SELECT s FROM StudentProfile s JOIN s.userAccount u WHERE " +
           "LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.rollNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.branch) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<StudentProfile> searchStudents(@Param("keyword") String keyword);
}
