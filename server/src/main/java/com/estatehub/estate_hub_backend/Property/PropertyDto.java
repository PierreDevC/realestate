package com.estatehub.estate_hub_backend.Property;

import com.estatehub.estate_hub_backend.Location.LocationDto;
import com.estatehub.estate_hub_backend.Manager.ManagerDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PropertyDto(
    Long id,
    
    @NotBlank(message = "Property name is required")
    @Size(max = 255, message = "Property name must not exceed 255 characters")
    String name,
    
    String description,
    
    @NotNull(message = "Price per month is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @JsonProperty("pricePerMonth")
    BigDecimal pricePerMonth,
    
    @DecimalMin(value = "0.0", message = "Security deposit must be non-negative")
    @JsonProperty("securityDeposit")
    BigDecimal securityDeposit,
    
    @DecimalMin(value = "0.0", message = "Application fee must be non-negative")
    @JsonProperty("applicationFee")
    BigDecimal applicationFee,
    
    @JsonProperty("photoUrls")
    List<String> photoUrls,
    
    List<String> amenities,
    
    List<String> highlights,
    
    @JsonProperty("isPetsAllowed")
    Boolean isPetsAllowed,
    
    @JsonProperty("isParkingIncluded")
    Boolean isParkingIncluded,
    
    @Min(value = 0, message = "Number of beds must be non-negative")
    Integer beds,
    
    @DecimalMin(value = "0.0", message = "Number of baths must be non-negative")
    Float baths,
    
    @Min(value = 0, message = "Square feet must be non-negative")
    @JsonProperty("squareFeet")
    Integer squareFeet,
    
    @NotBlank(message = "Property type is required")
    @JsonProperty("propertyType")
    String propertyType,
    
    @JsonProperty("postedDate")
    LocalDateTime postedDate,
    
    @JsonProperty("averageRating")
    Float averageRating,
    
    @JsonProperty("numberOfReviews")
    Integer numberOfReviews,
    
    @JsonProperty("isAvailable")
    Boolean isAvailable,
    
    @Valid
    LocationDto location,
    
    @Valid
    ManagerDto manager
) {
    // Constructeur personnalisé pour la création (optionnel)
    public PropertyDto(String name, String description, BigDecimal pricePerMonth, 
                      Integer beds, Float baths, String propertyType, 
                      LocationDto location, ManagerDto manager) {
        this(null, name, description, pricePerMonth, null, null, null, null, null,
             null, null, beds, baths, null, propertyType, null, null, null, 
             true, location, manager);
    }
}