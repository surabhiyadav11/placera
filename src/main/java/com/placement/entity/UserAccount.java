package com.placement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * UserAccount - Core entity representing authenticated credentials in the system.
 *
 * Demonstrates OOP Encapsulation:
 * - All instance variables are private.
 * - State modification occurs through validated accessors or domain methods.
 * - Business methods expose high-level semantic queries (e.g., isStudent(), isAdmin()).
 */
@Entity
@Table(name = "user_accounts", indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true)
})
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor required by JPA
    public UserAccount() {
    }

    // Parameterized constructor demonstrating OOP constructor initialization
    public UserAccount(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = true;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Domain business methods
    public boolean isStudent() {
        return this.role == Role.ROLE_STUDENT;
    }

    public boolean isCompany() {
        return this.role == Role.ROLE_COMPANY;
    }

    public boolean isAdmin() {
        return this.role == Role.ROLE_ADMIN;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAccount that = (UserAccount) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", enabled=" + enabled +
                '}';
    }
}
