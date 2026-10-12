package com.placement.security;

import com.placement.entity.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * CustomAuthenticationSuccessHandler - Routes authenticated users strictly to their designated portal.
 *
 * Demonstrates:
 * - OOP Polymorphism & Interface Implementation: Implements AuthenticationSuccessHandler.
 * - Strict Portal Demarcation:
 *   - ROLE_STUDENT -> /student/dashboard
 *   - ROLE_COMPANY -> /company/dashboard
 *   - ROLE_ADMIN   -> /admin/dashboard
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String roleName = authority.getAuthority();
            if (Role.ROLE_ADMIN.getAuthority().equals(roleName)) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                return;
            } else if (Role.ROLE_COMPANY.getAuthority().equals(roleName)) {
                response.sendRedirect(request.getContextPath() + "/company/dashboard");
                return;
            } else if (Role.ROLE_STUDENT.getAuthority().equals(roleName)) {
                response.sendRedirect(request.getContextPath() + "/student/dashboard");
                return;
            }
        }

        // Fallback default redirect if role is undefined
        response.sendRedirect(request.getContextPath() + "/");
    }
}
