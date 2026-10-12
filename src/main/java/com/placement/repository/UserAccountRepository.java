package com.placement.repository;

import com.placement.entity.Role;
import com.placement.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserAccountRepository - Spring Data JPA repository for UserAccount persistence.
 *
 * Demonstrates OOP Abstraction & Interface Inheritance:
 * - Extends JpaRepository interface contract.
 * - Abstract query methods declare behavior without exposing low-level SQL/JDBC boilerplate.
 */
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    List<UserAccount> findByRole(Role role);

    List<UserAccount> findByRoleOrderByCreatedAtDesc(Role role);
}
