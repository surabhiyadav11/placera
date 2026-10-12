package com.placement.repository;

import com.placement.entity.InterviewSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * InterviewScheduleRepository - Spring Data JPA repository for InterviewSchedule persistence.
 *
 * Demonstrates:
 * - OOP Abstraction: Interface contract for scheduling, candidate round tracking, and timeline sorting.
 */
@Repository
public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule, Long> {

    List<InterviewSchedule> findByApplicationStudentIdOrderByScheduledDateTimeAsc(Long studentId);

    List<InterviewSchedule> findByApplicationJobPostingCompanyIdOrderByScheduledDateTimeAsc(Long companyId);

    List<InterviewSchedule> findByApplicationIdOrderByScheduledDateTimeDesc(Long applicationId);

    long countByApplicationJobPostingCompanyId(Long companyId);

    long countByStatus(InterviewSchedule.InterviewStatus status);

    List<InterviewSchedule> findTop5ByApplicationStudentIdAndStatusOrderByScheduledDateTimeAsc(
            Long studentId, InterviewSchedule.InterviewStatus status);
}
