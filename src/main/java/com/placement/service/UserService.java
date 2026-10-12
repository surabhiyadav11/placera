package com.placement.service;

import com.placement.dto.CompanyRegisterDto;
import com.placement.dto.StudentRegisterDto;
import com.placement.entity.UserAccount;

/**
 * UserService - Abstraction contract for user identity, registration, and credential operations.
 *
 * Demonstrates:
 * - OOP Abstraction: Interface separating business logic contracts from implementation details.
 */
public interface UserService {

    UserAccount registerStudent(StudentRegisterDto dto);

    UserAccount registerCompany(CompanyRegisterDto dto);

    UserAccount findByEmail(String email);

    UserAccount getCurrentAuthenticatedUser();

    void changePassword(String email, String currentPassword, String newPassword);

    void toggleAccountStatus(Long userId, boolean enabled);
}
