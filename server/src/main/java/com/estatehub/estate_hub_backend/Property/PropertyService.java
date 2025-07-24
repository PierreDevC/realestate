package com.estatehub.estate_hub_backend.Property;

import com.estatehub.estate_hub_backend.Property.Property;
import com.estatehub.estate_hub_backend.Property.PropertyDto;
import com.estatehub.estate_hub_backend.Property.PropertyFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Property management operations
 * Defines contract for property-related business logic
 */
public interface PropertyService {

    /**
     * Find all properties with pagination and filtering
     * 
     * @param pageable pagination information
     * @param filters filter criteria for properties
     * @return paginated list of properties
     */
    Page<Property> findAllProperties(Pageable pageable, PropertyFilterDto filters);

    /**
     * Find property by ID
     * 
     * @param id property ID
     * @return property entity
     * @throws PropertyNotFoundException if property not found
     */
    Property findPropertyById(Long id);

    /**
     * Create a new property
     * 
     * @param property property entity to create
     * @return created property with generated ID
     * @throws ValidationException if property data is invalid
     */
    Property createProperty(Property property);

    /**
     * Update existing property
     * 
     * @param id property ID to update
     * @param updateDto updated property data
     * @param managerClerkId manager's clerk ID for ownership verification
     * @return updated property
     * @throws PropertyNotFoundException if property not found
     * @throws UnauthorizedException if manager doesn't own property
     * @throws ValidationException if update data is invalid
     */
    Property updateProperty(Long id, PropertyDto updateDto, String managerClerkId);

    /**
     * Delete property (soft delete - mark as inactive)
     * 
     * @param id property ID to delete
     * @param managerClerkId manager's clerk ID for ownership verification
     * @throws PropertyNotFoundException if property not found
     * @throws UnauthorizedException if manager doesn't own property
     * @throws BusinessException if property has active leases
     */
    void deleteProperty(Long id, String managerClerkId);

    /**
     * Search properties within a radius of given coordinates
     * 
     * @param latitude center latitude
     * @param longitude center longitude
     * @param radiusKm search radius in kilometers
     * @return list of properties within radius
     */
    List<Property> searchByRadius(Double latitude, Double longitude, Double radiusKm);

    /**
     * Update property's average rating based on reviews
     * 
     * @param propertyId property ID to update rating for
     */
    void updateAverageRating(Long propertyId);

    /**
     * Mark property as unavailable
     * 
     * @param propertyId property ID
     * @param managerClerkId manager's clerk ID for ownership verification
     * @throws PropertyNotFoundException if property not found
     * @throws UnauthorizedException if manager doesn't own property
     */
    void markAsUnavailable(Long propertyId, String managerClerkId);

    /**
     * Mark property as available
     * 
     * @param propertyId property ID
     * @param managerClerkId manager's clerk ID for ownership verification
     * @throws PropertyNotFoundException if property not found
     * @throws UnauthorizedException if manager doesn't own property
     */
    void markAsAvailable(Long propertyId, String managerClerkId);

    /**
     * Get all properties owned by a specific manager
     * 
     * @param managerClerkId manager's clerk ID
     * @return list of properties owned by manager
     */
    List<Property> getPropertiesByManager(String managerClerkId);

    /**
     * Find available properties with pagination
     * 
     * @param pageable pagination information
     * @return paginated list of available properties
     */
    Page<Property> findAvailableProperties(Pageable pageable);

    /**
     * Verify that a manager owns a specific property
     * 
     * @param propertyId property ID
     * @param managerClerkId manager's clerk ID
     * @throws PropertyNotFoundException if property not found
     * @throws UnauthorizedException if manager doesn't own property
     */
    void verifyManagerOwnership(Long propertyId, String managerClerkId);

    /**
     * Find properties by location (city and state)
     * 
     * @param city city name
     * @param state state name
     * @return list of properties in specified location
     */
    List<Property> findPropertiesByLocation(String city, String state);

    /**
     * Find properties by type
     * 
     * @param propertyType type of property (APARTMENT, HOUSE, etc.)
     * @return list of properties of specified type
     */
    List<Property> findPropertiesByType(String propertyType);

    /**
     * Find properties within price range
     * 
     * @param minPrice minimum price per month
     * @param maxPrice maximum price per month
     * @return list of properties within price range
     */
    List<Property> findPropertiesByPriceRange(Double minPrice, Double maxPrice);

    /**
     * Find properties with minimum bed/bath requirements
     * 
     * @param minBeds minimum number of bedrooms
     * @param minBaths minimum number of bathrooms
     * @return list of properties meeting requirements
     */
    List<Property> findPropertiesByBedroomsBathrooms(Integer minBeds, Float minBaths);

    /**
     * Get property statistics for a manager (dashboard data)
     * 
     * @param managerClerkId manager's clerk ID
     * @return property statistics summary
     */
    PropertyStatisticsDto getPropertyStatistics(String managerClerkId);

    /**
     * Update property photos
     * 
     * @param propertyId property ID
     * @param photoUrls list of new photo URLs
     * @param managerClerkId manager's clerk ID for ownership verification
     * @return updated property
     * @throws PropertyNotFoundException if property not found
     * @throws UnauthorizedException if manager doesn't own property
     */
    Property updatePropertyPhotos(Long propertyId, List<String> photoUrls, String managerClerkId);

    /**
     * Get recently posted properties
     * 
     * @param days number of days back to search
     * @param pageable pagination information
     * @return paginated list of recently posted properties
     */
    Page<Property> findRecentlyPostedProperties(Integer days, Pageable pageable);

    /**
     * Get properties with high ratings
     * 
     * @param minRating minimum rating threshold
     * @param pageable pagination information
     * @return paginated list of highly rated properties
     */
    Page<Property> findHighlyRatedProperties(Float minRating, Pageable pageable);

    /**
     * Check if property is available for new applications
     * 
     * @param propertyId property ID
     * @return true if property accepts applications, false otherwise
     */
    boolean isPropertyAvailableForApplications(Long propertyId);

    /**
     * Get total count of properties by manager
     * 
     * @param managerClerkId manager's clerk ID
     * @return count of properties owned by manager
     */
    Long getPropertyCountByManager(String managerClerkId);

    /**
     * Archive old inactive properties (cleanup operation)
     * 
     * @param daysInactive number of days property has been inactive
     * @return number of properties archived
     */
    Integer archiveInactiveProperties(Integer daysInactive);
}