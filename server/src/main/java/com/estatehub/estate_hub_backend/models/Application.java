package com.estatehub.estate_hub_backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import com.estatehub.estate_hub_backend.enums.ApplicationStatus;

@Entity
@Table(name = "applications")
public class Application {
    // Properties
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_date", nullable = false)
    @NotNull(message = "Application date is required")
    private LocalDateTime applicationDate;

    @Column(name = "status", nullable = false)
    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    // Relations
    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "applicant_name", nullable = false)
    @NotBlank(message = "Applicant name is required")
    @Size(min = 3, max = 100, message = "Applicant name must be between 3 and 100 characters")
    private String applicantName;

    @Column(name = "applicant_email", nullable = false)
    @NotBlank(message = "Applicant email is required")
    @Email(message = "Invalid email address")
    private String applicantEmail;

    @Column(name = "applicant_phone", nullable = false)
    @NotBlank(message = "Applicant phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
    private String applicantPhone;

    @Column(name = "message", nullable = false)
    @NotBlank(message = "Message is required")
    @Size(min = 3, max = 1000, message = "Message must be between 3 and 1000 characters")
    private String message;

    @Column(name = "lease_id", nullable = false)
    @NotBlank(message = "Lease ID is required")
    private String leaseId;

    // Audit fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Application() {}

    public Application(LocalDateTime applicationDate, ApplicationStatus status, Property property, 
                        Tenant tenant,  String applicantName, String applicantEmail, 
                        String applicantPhone, String message, String leaseId) {
        this.applicationDate = applicationDate;
        this.status = status;
        this.property = property;   
        this.tenant = tenant;
        this.applicantName = applicantName;
        this.applicantEmail = applicantEmail;
        this.applicantPhone = applicantPhone;
        this.message = message;
        this.leaseId = leaseId;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDateTime applicationDate) { this.applicationDate = applicationDate; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public String getApplicantEmail() { return applicantEmail; }
    public void setApplicantEmail(String applicantEmail) { this.applicantEmail = applicantEmail; }

    public String getApplicantPhone() { return applicantPhone; }
    public void setApplicantPhone(String applicantPhone) { this.applicantPhone = applicantPhone; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getLeaseId() { return leaseId; }
    public void setLeaseId(String leaseId) { this.leaseId = leaseId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
