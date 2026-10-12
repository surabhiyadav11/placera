package com.placement.repository;

import com.placement.entity.PlacementDrive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PlacementDriveRepository - Spring Data JPA repository for PlacementDrive persistence.
 *
 * Demonstrates:
 * - OOP Abstraction: Interface-based persistence for campus drive cycles and calendar timelines.
 */
@Repository
public interface PlacementDriveRepository extends JpaRepository<PlacementDrive, Long> {

    List<PlacementDrive> findByStatus(PlacementDrive.DriveStatus status);

    List<PlacementDrive> findByAcademicYear(String academicYear);

    List<PlacementDrive> findAllByOrderByStartDateDesc();

    long countByStatus(PlacementDrive.DriveStatus status);
}
