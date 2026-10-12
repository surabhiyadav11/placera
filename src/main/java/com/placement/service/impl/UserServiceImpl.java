package com.placement.service.impl;

import com.placement.dto.CompanyRegisterDto;
import com.placement.dto.StudentRegisterDto;
import com.placement.entity.CompanyProfile;
import com.placement.entity.Role;
import com.placement.entity.StudentProfile;
import com.placement.entity.UserAccount;
import com.placement.exception.DuplicateResourceException;
import com.placement.exception.ResourceNotFoundException;
import com.placement.exception.UnauthorizedAccessException;
import com.placement.repository.CompanyProfileRepository;
import com.placement.repository.StudentProfileRepository;
import com.placement.repository.UserAccountRepository;
import com.placement.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserServiceImpl - Implements UserService interface contract.
 *
 * Demonstrates:
 * - OOP Constructor Injection: Injects all mandatory dependencies cleanly without field injection.
 * - Encapsulation of Registration Logic: Enforces transactional creation of credentials and profiles.
 * - Password Hashing: BCrypt encoder is applied before persisting credentials.
 * - Exception Handling: Throws domain-specific checked/unchecked exceptions.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserAccountRepository userAccountRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor Injection
    public UserServiceImpl(UserAccountRepository userAccountRepository,
                           StudentProfileRepository studentProfileRepository,
                           CompanyProfileRepository companyProfileRepository,
                           PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserAccount registerStudent(StudentRegisterDto dto) {
        if (userAccountRepository.existsByEmail(dto.getEmail().trim().toLowerCase())) {
            throw new DuplicateResourceException("Account", "email", dto.getEmail());
        }

        if (studentProfileRepository.existsByRollNumber(dto.getRollNumber().trim().toUpperCase())) {
            throw new DuplicateResourceException("StudentProfile", "rollNumber", dto.getRollNumber());
        }

        // 1. Create credential account with STUDENT role
        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(dto.getEmail().trim().toLowerCase());
        userAccount.setPassword(passwordEncoder.encode(dto.getPassword()));
        userAccount.setRole(Role.ROLE_STUDENT);
        userAccount.setEnabled(true);
        UserAccount savedAccount = userAccountRepository.save(userAccount);

        // 2. Create student profile
        StudentProfile profile = new StudentProfile();
        profile.setUserAccount(savedAccount);
        profile.setFullName(dto.getFullName().trim());
        profile.setRollNumber(dto.getRollNumber().trim().toUpperCase());
        profile.setBranch(dto.getBranch().trim());
        profile.setCgpa(dto.getCgpa());
        profile.setGraduationYear(dto.getGraduationYear());
        profile.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
        profile.setSkills(dto.getSkills() != null ? dto.getSkills().trim() : null);
        studentProfileRepository.save(profile);

        return savedAccount;
    }

    @Override
    public UserAccount registerCompany(CompanyRegisterDto dto) {
        if (userAccountRepository.existsByEmail(dto.getEmail().trim().toLowerCase())) {
            throw new DuplicateResourceException("Account", "email", dto.getEmail());
        }

        // 1. Create credential account with COMPANY role
        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(dto.getEmail().trim().toLowerCase());
        userAccount.setPassword(passwordEncoder.encode(dto.getPassword()));
        userAccount.setRole(Role.ROLE_COMPANY);
        userAccount.setEnabled(true);
        UserAccount savedAccount = userAccountRepository.save(userAccount);

        // 2. Create company profile in PENDING status
        CompanyProfile profile = new CompanyProfile();
        profile.setUserAccount(savedAccount);
        profile.setCompanyName(dto.getCompanyName().trim());
        profile.setContactPerson(dto.getContactPerson().trim());
        profile.setContactEmail(dto.getContactEmail() != null ? dto.getContactEmail().trim() : dto.getEmail().trim());
        profile.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
        profile.setWebsite(dto.getWebsite() != null ? dto.getWebsite().trim() : null);
        profile.setIndustry(dto.getIndustry().trim());
        profile.setLocation(dto.getLocation().trim());
        profile.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        profile.setApprovalStatus(CompanyProfile.ApprovalStatus.PENDING);
        companyProfileRepository.save(profile);

        return savedAccount;
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccount findByEmail(String email) {
        return userAccountRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("UserAccount", "email", email));
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccount getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userAccountRepository.findByEmail(auth.getName().trim().toLowerCase()).orElse(null);
    }

    @Override
    public void changePassword(String email, String currentPassword, String newPassword) {
        UserAccount user = findByEmail(email);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UnauthorizedAccessException("PASSWORD_UPDATE", "Current password does not match");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userAccountRepository.save(user);
    }

    @Override
    public void toggleAccountStatus(Long userId, boolean enabled) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserAccount", "id", userId));
        user.setEnabled(enabled);
        userAccountRepository.save(user);
    }
}
