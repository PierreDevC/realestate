package com.estatehub.estate_hub_backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "properties")
public class Property {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Property name is required")
    @Size(max = 255, message = "Property name must not exceed 255 characters")
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Price per month is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(name = "price_per_month", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerMonth;
    
    @DecimalMin(value = "0.0", message = "Security deposit must be non-negative")
    @Column(name = "security_deposit", precision = 10, scale = 2)
    private BigDecimal securityDeposit;
    
    @DecimalMin(value = "0.0", message = "Application fee must be non-negative")
    @Column(name = "application_fee", precision = 10, scale = 2)
    private BigDecimal applicationFee;
    
    @ElementCollection
    @CollectionTable(name = "property_photos", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls;
    
    @ElementCollection
    @CollectionTable(name = "property_amenities", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "amenity")
    private List<String> amenities;
    
    @ElementCollection
    @CollectionTable(name = "property_highlights", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "highlight")
    private List<String> highlights;
    
    @Column(name = "is_pets_allowed")
    private Boolean isPetsAllowed = false;
    
    @Column(name = "is_parking_included")
    private Boolean isParkingIncluded = false;
    
    @Min(value = 0, message = "Number of beds must be non-negative")
    @Column(nullable = false)
    private Integer beds;
    
    @DecimalMin(value = "0.0", message = "Number of baths must be non-negative")
    @Column(nullable = false)
    private Float baths;
    
    @Min(value = 0, message = "Square feet must be non-negative")
    @Column(name = "square_feet")
    private Integer squareFeet;
    
    @NotBlank(message = "Property type is required")
    @Column(name = "property_type", nullable = false)
    private String propertyType; // APARTMENT, HOUSE, CONDO, TOWNHOUSE, etc.
    
    @CreationTimestamp
    @Column(name = "posted_date", nullable = false, updatable = false)
    private LocalDateTime postedDate;
    
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    @Column(name = "average_rating", precision = 3, scale = 2)
    private Float averageRating;
    
    @Min(value = 0)
    @Column(name = "number_of_reviews")
    private Integer numberOfReviews = 0;
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Manager manager;
    
    // Audit fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructeurs
    public Property() {}
    
    public Property(String name, String description, BigDecimal pricePerMonth, 
                   Integer beds, Float baths, String propertyType, 
                   Location location, Manager manager) {
        this.name = name;
        this.description = description;
        this.pricePerMonth = pricePerMonth;
        this.beds = beds;
        this.baths = baths;
        this.propertyType = propertyType;
        this.location = location;
        this.manager = manager;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPricePerMonth() { return pricePerMonth; }
    public void setPricePerMonth(BigDecimal pricePerMonth) { this.pricePerMonth = pricePerMonth; }
    
    public BigDecimal getSecurityDeposit() { return securityDeposit; }
    public void setSecurityDeposit(BigDecimal securityDeposit) { this.securityDeposit = securityDeposit; }
    
    public BigDecimal getApplicationFee() { return applicationFee; }
    public void setApplicationFee(BigDecimal applicationFee) { this.applicationFee = applicationFee; }
    
    public List<String> getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; }
    
    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }
    
    public List<String> getHighlights() { return highlights; }
    public void setHighlights(List<String> highlights) { this.highlights = highlights; }
    
    public Boolean getIsPetsAllowed() { return isPetsAllowed; }
    public void setIsPetsAllowed(Boolean isPetsAllowed) { this.isPetsAllowed = isPetsAllowed; }
    
    public Boolean getIsParkingIncluded() { return isParkingIncluded; }
    public void setIsParkingIncluded(Boolean isParkingIncluded) { this.isParkingIncluded = isParkingIncluded; }
    
    public Integer getBeds() { return beds; }
    public void setBeds(Integer beds) { this.beds = beds; }
    
    public Float getBaths() { return baths; }
    public void setBaths(Float baths) { this.baths = baths; }
    
    public Integer getSquareFeet() { return squareFeet; }
    public void setSquareFeet(Integer squareFeet) { this.squareFeet = squareFeet; }
    
    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }
    
    public LocalDateTime getPostedDate() { return postedDate; }
    public void setPostedDate(LocalDateTime postedDate) { this.postedDate = postedDate; }
    
    public Float getAverageRating() { return averageRating; }
    public void setAverageRating(Float averageRating) { this.averageRating = averageRating; }
    
    public Integer getNumberOfReviews() { return numberOfReviews; }
    public void setNumberOfReviews(Integer numberOfReviews) { this.numberOfReviews = numberOfReviews; }
    
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
    
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    
    public Manager getManager() { return manager; }
    public void setManager(Manager manager) { this.manager = manager; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

