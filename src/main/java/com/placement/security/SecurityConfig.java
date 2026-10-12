package com.placement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig - Core Spring Security configuration enforcing genuine server-side authorization.
 *
 * Demonstrates:
 * - Strict Role Separation:
 *   - Students cannot access /company/** or /admin/**.
 *   - Companies cannot access /student/** or /admin/**.
 *   - Admins cannot access /student/** or /company/**.
 * - Password Hashing: BCryptPasswordEncoder with strong salt rounds.
 * - Protection: CSRF token validation, session migration on authentication, and access denial routing.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    public SecurityConfig(CustomAuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Public resources and authentication endpoints
                        .requestMatchers(
                                "/",
                                "/home",
                                "/about",
                                "/login",
                                "/register",
                                "/register/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico",
                                "/access-denied",
                                "/error"
                        ).permitAll()

                        // Role-specific portal routes (Strict server-side isolation)
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .requestMatchers("/company/**").hasRole("COMPANY")
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // All other routes require authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(authenticationSuccessHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")
                )
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                );

        return http.build();
    }
}
