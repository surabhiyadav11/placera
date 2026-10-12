package com.placement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * CompanyRegisterDto - Data Transfer Object encapsulating company registration form data.
 *
 * Demonstrates:
 * - OOP Encapsulation: Prevents unauthorized client manipulation of approvalStatus or role.
 * - Jakarta Bean Validation for robust server-side input sanitation.
 */
public class CompanyRegisterDto {

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 150, message = "Company name must be between 2 and 150 characters")
    private String companyName;

    @NotBlank(message = "Login email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Contact person name is required")
    @Size(min = 2, max = 100, message = "Contact person name must be between 2 and 100 characters")
    private String contactPerson;

    @Email(message = "Contact email must be a valid email address")
    private String contactEmail;

    private String phone;

    private String website;

    @NotBlank(message = "Industry type is required")
    private String industry;

    @NotBlank(message = "Headquarters / location is required")
    private String location;

    private String description;

    public CompanyRegisterDto() {
    }

    public CompanyRegisterDto(String companyName, String email, String password,
                              String contactPerson, String industry, String location) {
        this.companyName = companyName;
        this.email = email;
        this.password = password;
        this.contactPerson = contactPerson;
        this.industry = industry;
        this.location = location;
    }

    // Getters and Setters
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
