package com.estatehub.estate_hub_backend.Property;
/* package com.estatehub.estate_hub_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PropertyDto {
    
    private Long id;
    
    @NotBlank(message = "Property name is required")
    @Size(max = 255, message = "Property name must not exceed 255 characters")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price per month is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @JsonProperty("pricePerMonth")
    private BigDecimal pricePerMonth;
    
    @DecimalMin(value = "0.0", message = "Security deposit must be non-negative")
    @JsonProperty("securityDeposit")
    private BigDecimal securityDeposit;
    
    @DecimalMin(value = "0.0", message = "Application fee must be non-negative")
    @JsonProperty("applicationFee")
    private BigDecimal applicationFee;
    
    @JsonProperty("photoUrls")
    private List<String> photoUrls;
    
    private List<String> amenities;
    
    private List<String> highlights;
    
    @JsonProperty("isPetsAllowed")
    private Boolean isPetsAllowed;
    
    @JsonProperty("isParkingIncluded")
    private Boolean isParkingIncluded;
    
    @Min(value = 0, message = "Number of beds must be non-negative")
    private Integer beds;
    
    @DecimalMin(value = "0.0", message = "Number of baths must be non-negative")
    private Float baths;
    
    @Min(value = 0, message = "Square feet must be non-negative")
    @JsonProperty("squareFeet")
    private Integer squareFeet;
    
    @NotBlank(message = "Property type is required")
    @JsonProperty("propertyType")
    private String propertyType;
    
    @JsonProperty("postedDate")
    private LocalDateTime postedDate;
    
    @JsonProperty("averageRating")
    private Float averageRating;
    
    @JsonProperty("numberOfReviews")
    private Integer numberOfReviews;
    
    @JsonProperty("isAvailable")
    private Boolean isAvailable;
    
    // Relations comme DTOs
    private LocationDto location;
    private ManagerDto manager;
    
    // Constructeurs
    public PropertyDto() {}
    
    public PropertyDto(String name, String description, BigDecimal pricePerMonth, 
                      Integer beds, Float baths, String propertyType, 
                      LocationDto location, ManagerDto manager) {
        this.name = name;
        this.description = description;
        this.pricePerMonth = pricePerMonth;
        this.beds = beds;
        this.baths = baths;
        this.propertyType = propertyType;
        this.location = location;
        this.manager = manager;
    }
    
    // Getters et Setters (identiques à l'entité)
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
    
    public LocationDto getLocation() { return location; }
    public void setLocation(LocationDto location) { this.location = location; }
    
    public ManagerDto getManager() { return manager; }
    public void setManager(ManagerDto manager) { this.manager = manager; }
}

*/