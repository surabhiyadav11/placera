package com.placement.entity;

/**
 * Enumeration of security roles in the Placement Management System.
 *
 * Each role corresponds to a dedicated portal with strict server-side authorization:
 * - ROLE_STUDENT: Access only to the Student Portal
 * - ROLE_COMPANY: Access only to the Company Portal
 * - ROLE_ADMIN:   Access only to the Placement Admin Portal
 */
public enum Role {
    ROLE_STUDENT("STUDENT", "Student"),
    ROLE_COMPANY("COMPANY", "Company"),
    ROLE_ADMIN("ADMIN", "Placement Administrator");

    private final String roleName;
    private final String displayName;

    Role(String roleName, String displayName) {
        this.roleName = roleName;
        this.displayName = displayName;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the role authority string as expected by Spring Security (e.g., "ROLE_STUDENT").
     */
    public String getAuthority() {
        return name();
    }
}
