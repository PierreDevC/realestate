package com.estatehub.estate_hub_backend.Property;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class PropertyServiceImpl implements PropertyService {

    private static final Logger log = LoggerFactory.getLogger(PropertyServiceImpl.class);
    
    private final PropertyRepository propertyRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Property> findAllProperties(Pageable pageable) {
        log.debug("Finding all properties with pagination");
        return propertyRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Property findPropertyById(Long id) {
        log.debug("Finding property by ID: {}", id);
        
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + id));
    }

    @Override
    public Property createProperty(Property property) {
        log.info("Creating new property: {}", property.getName());
        
        validatePropertyData(property);
        
        // Set initial values
        property.setPostedDate(LocalDateTime.now());
        property.setIsAvailable(true);
        property.setAverageRating(0.0f);
        property.setNumberOfReviews(0);
        
        Property savedProperty = propertyRepository.save(property);
        log.info("Successfully created property with ID: {}", savedProperty.getId());
        
        return savedProperty;
    }

    @Override
    public Property updateProperty(Long id, Property updatedProperty) {
        log.info("Updating property with ID: {}", id);
        
        Property existingProperty = findPropertyById(id);
        
        // Update fields
        updatePropertyFields(existingProperty, updatedProperty);
        
        Property saved = propertyRepository.save(existingProperty);
        log.info("Successfully updated property: {}", id);
        
        return saved;
    }

    @Override
    public void deleteProperty(Long id) {
        log.info("Deleting property with ID: {}", id);
        
        Property property = findPropertyById(id);
        
        // Soft delete - mark as unavailable
        property.setIsAvailable(false);
        propertyRepository.save(property);
        
        log.info("Successfully deleted property: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Property> searchByRadius(Double latitude, Double longitude, Double radiusKm) {
        log.debug("Searching properties within {}km of lat: {}, lng: {}", radiusKm, latitude, longitude);
        
        if (latitude == null || longitude == null || radiusKm == null) {
            throw new IllegalArgumentException("Latitude, longitude, and radius are required for location search");
        }
        
        return propertyRepository.findPropertiesWithinRadius(latitude, longitude, radiusKm);
    }

    @Override
    public void updateAverageRating(Long propertyId) {
        log.debug("Updating average rating for property: {}", propertyId);
        
        Property property = findPropertyById(propertyId);
        
        // TODO: Calculate actual rating from reviews
        // For now, we'll just log the action
        log.debug("Rating updated for property: {}", propertyId);
        
        propertyRepository.save(property);
    }

    @Override
    public void markAsUnavailable(Long propertyId) {
        log.info("Marking property {} as unavailable", propertyId);
        
        Property property = findPropertyById(propertyId);
        property.setIsAvailable(false);
        propertyRepository.save(property);
        
        log.info("Successfully marked property {} as unavailable", propertyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Property> getPropertiesByManager(String managerClerkId) {
        log.debug("Finding properties for manager: {}", managerClerkId);
        
        return propertyRepository.findByManagerClerkId(managerClerkId);
    }

    // Private helper methods
    
    private void validatePropertyData(Property property) {
        if (!StringUtils.hasText(property.getName())) {
            throw new IllegalArgumentException("Property name is required");
        }
        
        if (property.getPricePerMonth() == null || property.getPricePerMonth().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price per month must be greater than 0");
        }
        
        if (property.getBeds() == null || property.getBeds() < 0) {
            throw new IllegalArgumentException("Number of bedrooms must be 0 or greater");
        }
        
        if (property.getBaths() == null || property.getBaths() < 0) {
            throw new IllegalArgumentException("Number of bathrooms must be 0 or greater");
        }
        
        if (property.getLocation() == null) {
            throw new IllegalArgumentException("Property location is required");
        }
        
        if (property.getManager() == null) {
            throw new IllegalArgumentException("Property manager is required");
        }
    }
    
    private void updatePropertyFields(Property existing, Property updated) {
        if (StringUtils.hasText(updated.getName())) {
            existing.setName(updated.getName());
        }
        
        if (StringUtils.hasText(updated.getDescription())) {
            existing.setDescription(updated.getDescription());
        }
        
        if (updated.getPricePerMonth() != null) {
            existing.setPricePerMonth(updated.getPricePerMonth());
        }
        
        if (updated.getSecurityDeposit() != null) {
            existing.setSecurityDeposit(updated.getSecurityDeposit());
        }
        
        if (updated.getApplicationFee() != null) {
            existing.setApplicationFee(updated.getApplicationFee());
        }
        
        if (updated.getAmenities() != null) {
            existing.setAmenities(updated.getAmenities());
        }
        
        if (updated.getHighlights() != null) {
            existing.setHighlights(updated.getHighlights());
        }
        
        if (updated.getIsPetsAllowed() != null) {
            existing.setIsPetsAllowed(updated.getIsPetsAllowed());
        }
        
        if (updated.getIsParkingIncluded() != null) {
            existing.setIsParkingIncluded(updated.getIsParkingIncluded());
        }
        
        if (updated.getBeds() != null) {
            existing.setBeds(updated.getBeds());
        }
        
        if (updated.getBaths() != null) {
            existing.setBaths(updated.getBaths());
        }
        
        if (updated.getSquareFeet() != null) {
            existing.setSquareFeet(updated.getSquareFeet());
        }
        
        if (updated.getPropertyType() != null) {
            existing.setPropertyType(updated.getPropertyType());
        }
        
        if (updated.getPhotoUrls() != null) {
            existing.setPhotoUrls(updated.getPhotoUrls());
        }
        
        // Location and Manager updates would need separate handling
        if (updated.getLocation() != null) {
            existing.setLocation(updated.getLocation());
        }
        
        if (updated.getManager() != null) {
            existing.setManager(updated.getManager());
        }
    }
}