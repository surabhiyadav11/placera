package com.placement.security;

import com.placement.entity.UserAccount;
import com.placement.repository.UserAccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * CustomUserDetailsService - Bridges JPA UserAccount persistence to Spring Security's authentication engine.
 *
 * Demonstrates:
 * - OOP Polymorphism & Interface Implementation: Implements UserDetailsService.
 * - Constructor Injection: Injects UserAccountRepository.
 * - Dynamic Authority Mapping: Assigns security roles (ROLE_STUDENT, ROLE_COMPANY, ROLE_ADMIN).
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public CustomUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccount user = userAccountRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("No registered account found with email: " + email));

        return new User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getAuthority()))
        );
    }
}
