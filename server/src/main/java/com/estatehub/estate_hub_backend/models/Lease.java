package com.estatehub.estate_hub_backend.models;

import com.estatehub.estate_hub_backend.enums.LeaseStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leases")
public class Lease {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @NotNull(message = "Monthly rent is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly rent must be greater than 0")
    @Column(name = "monthly_rent", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyRent;
    
    @DecimalMin(value = "0.0", message = "Security deposit must be non-negative")
    @Column(name = "security_deposit", precision = 10, scale = 2)
    private BigDecimal securityDeposit;
    
    @NotNull(message = "Lease status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "lease_status", nullable = false)
    private LeaseStatus leaseStatus = LeaseStatus.ACTIVE;
    
    @Size(max = 500, message = "Lease document URL must not exceed 500 characters")
    @Column(name = "lease_document_url")
    private String leaseDocumentUrl;
    
    // Relations
    @NotNull(message = "Property is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
    
    @NotNull(message = "Tenant is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", unique = true)
    private Application application;
    
    // Audit fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructeurs
    public Lease() {}
    
    public Lease(LocalDate startDate, LocalDate endDate, BigDecimal monthlyRent, 
                BigDecimal securityDeposit, Property property, Tenant tenant) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.monthlyRent = monthlyRent;
        this.securityDeposit = securityDeposit;
        this.property = property;
        this.tenant = tenant;
        this.leaseStatus = LeaseStatus.ACTIVE;
    }
    
    public Lease(LocalDate startDate, LocalDate endDate, BigDecimal monthlyRent, 
                BigDecimal securityDeposit, Property property, Tenant tenant, 
                Application application) {
        this(startDate, endDate, monthlyRent, securityDeposit, property, tenant);
        this.application = application;
    }
    
    // Méthodes utilitaires
    public boolean isActive() {
        return LeaseStatus.ACTIVE.equals(this.leaseStatus);
    }
    
    public boolean isExpired() {
        return LeaseStatus.EXPIRED.equals(this.leaseStatus) || 
               (LocalDate.now().isAfter(this.endDate) && isActive());
    }
    
    public boolean isTerminated() {
        return LeaseStatus.TERMINATED.equals(this.leaseStatus);
    }
    
    public long getDurationInDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    public long getDurationInMonths() {
        return java.time.temporal.ChronoUnit.MONTHS.between(startDate, endDate);
    }
    
    public BigDecimal getTotalRentAmount() {
        return monthlyRent.multiply(BigDecimal.valueOf(getDurationInMonths()));
    }
    
    public boolean isRenewalEligible() {
        return isActive() && LocalDate.now().isAfter(endDate.minusMonths(2));
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }
    
    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }
    
    public BigDecimal getSecurityDeposit() {
        return securityDeposit;
    }
    
    public void setSecurityDeposit(BigDecimal securityDeposit) {
        this.securityDeposit = securityDeposit;
    }
    
    public LeaseStatus getLeaseStatus() {
        return leaseStatus;
    }
    
    public void setLeaseStatus(LeaseStatus leaseStatus) {
        this.leaseStatus = leaseStatus;
    }
    
    public String getLeaseDocumentUrl() {
        return leaseDocumentUrl;
    }
    
    public void setLeaseDocumentUrl(String leaseDocumentUrl) {
        this.leaseDocumentUrl = leaseDocumentUrl;
    }
    
    public Property getProperty() {
        return property;
    }
    
    public void setProperty(Property property) {
        this.property = property;
    }
    
    public Tenant getTenant() {
        return tenant;
    }
    
    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
    
    public Application getApplication() {
        return application;
    }
    
    public void setApplication(Application application) {
        this.application = application;
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
    
    // toString pour debugging
    @Override
    public String toString() {
        return "Lease{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", monthlyRent=" + monthlyRent +
                ", leaseStatus=" + leaseStatus +
                ", propertyId=" + (property != null ? property.getId() : null) +
                ", tenantId=" + (tenant != null ? tenant.getId() : null) +
                '}';
    }
    
    // equals et hashCode basés sur l'ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lease)) return false;
        Lease lease = (Lease) o;
        return id != null && id.equals(lease.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}