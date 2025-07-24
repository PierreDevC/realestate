package com.estatehub.estate_hub_backend.Property;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for Property management operations
 * Defines contract for property-related business logic
 */
public interface PropertyService {

    /**
     * Find all properties with pagination
     */
    Page<Property> findAllProperties(Pageable pageable);

    /**
     * Find property by ID
     */
    Property findPropertyById(Long id);

    /**
     * Create a new property
     */
    Property createProperty(Property property);

    /**
     * Update existing property
     */
    Property updateProperty(Long id, Property property);

    /**
     * Delete property
     */
    void deleteProperty(Long id);

    /**
     * Search properties within radius
     */
    List<Property> searchByRadius(Double latitude, Double longitude, Double radiusKm);

    /**
     * Update property's average rating
     */
    void updateAverageRating(Long propertyId);

    /**
     * Mark property as unavailable
     */
    void markAsUnavailable(Long propertyId);

    /**
     * Get properties by manager
     */
    List<Property> getPropertiesByManager(String managerClerkId);
}