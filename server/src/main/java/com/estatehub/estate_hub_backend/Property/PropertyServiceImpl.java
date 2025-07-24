package com.rentalapp.service.impl;

import com.rentalapp.dto.PropertyFilterDto;
import com.rentalapp.dto.PropertyStatisticsDto;
import com.rentalapp.dto.UpdatePropertyDto;
import com.rentalapp.entity.Property;
import com.rentalapp.entity.PropertyReview;
import com.rentalapp.exception.BusinessException;
import com.rentalapp.exception.PropertyNotFoundException;
import com.rentalapp.exception.UnauthorizedException;
import com.rentalapp.exception.ValidationException;
import com.rentalapp.repository.PropertyRepository;
import com.rentalapp.repository.ApplicationRepository;
import com.rentalapp.repository.LeaseRepository;
import com.rentalapp.service.PropertyService;
import com.rentalapp.service.ReviewService;
import com.rentalapp.service.LocationService;
import com.rentalapp.service.FileStorageService;
import com.rentalapp.mapper.PropertyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final ApplicationRepository applicationRepository;
    private final LeaseRepository leaseRepository;
    
    private final ReviewService reviewService;
    private final LocationService locationService;
    private final FileStorageService fileStorageService;
    
    private final PropertyMapper propertyMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<Property> findAllProperties(Pageable pageable, PropertyFilterDto filters) {
        log.debug("Finding all properties with filters: {}", filters);
        
        Specification<Property> spec = buildPropertySpecification(filters);
        return propertyRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Property findPropertyById(Long id) {
        log.debug("Finding property by ID: {}", id);
        
        return propertyRepository.findById(id)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with ID: " + id));
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
        property.setCreatedAt(LocalDateTime.now());
        property.setUpdatedAt(LocalDateTime.now());
        
        // Geocode address if location coordinates are missing
        if (property.getLocation() != null && property.getLocation().getCoordinates() == null) {
            try {
                var coordinates = locationService.geocodeAddress(
                    property.getLocation().getAddress() + ", " + 
                    property.getLocation().getCity() + ", " + 
                    property.getLocation().getState()
                );
                property.getLocation().setCoordinates(coordinates);
            } catch (Exception e) {
                log.warn("Failed to geocode address for property: {}", property.getName(), e);
            }
        }
        
        Property savedProperty = propertyRepository.save(property);
        log.info("Successfully created property with ID: {}", savedProperty.getId());
        
        return savedProperty;
    }

    @Override
    public Property updateProperty(Long id, UpdatePropertyDto updateDto, String managerClerkId) {
        log.info("Updating property {} by manager: {}", id, managerClerkId);
        
        Property existingProperty = findPropertyById(id);
        verifyManagerOwnership(id, managerClerkId);
        
        // Update fields from DTO
        updatePropertyFields(existingProperty, updateDto);
        existingProperty.setUpdatedAt(LocalDateTime.now());
        
        // Re-geocode if address changed
        if (updateDto.getLocation() != null) {
            try {
                var coordinates = locationService.geocodeAddress(
                    updateDto.getLocation().getAddress() + ", " + 
                    updateDto.getLocation().getCity() + ", " + 
                    updateDto.getLocation().getState()
                );
                existingProperty.getLocation().setCoordinates(coordinates);
            } catch (Exception e) {
                log.warn("Failed to re-geocode address for property: {}", id, e);
            }
        }
        
        Property updatedProperty = propertyRepository.save(existingProperty);
        log.info("Successfully updated property: {}", id);
        
        return updatedProperty;
    }

    @Override
    public void deleteProperty(Long id, String managerClerkId) {
        log.info("Deleting property {} by manager: {}", id, managerClerkId);
        
        Property property = findPropertyById(id);
        verifyManagerOwnership(id, managerClerkId);
        
        // Check for active leases
        Long activeLeaseCount = leaseRepository.countActiveLeasesByPropertyId(id);
        if (activeLeaseCount > 0) {
            throw new BusinessException("Cannot delete property with active leases. Found " + activeLeaseCount + " active lease(s).");
        }
        
        // Soft delete - mark as unavailable and inactive
        property.setIsAvailable(false);
        property.setUpdatedAt(LocalDateTime.now());
        propertyRepository.save(property);
        
        // Clean up associated files
        if (property.getPhotoUrls() != null) {
            for (String photoUrl : property.getPhotoUrls()) {
                try {
                    fileStorageService.deleteFile(extractS3KeyFromUrl(photoUrl));
                } catch (Exception e) {
                    log.warn("Failed to delete photo for property {}: {}", id, photoUrl, e);
                }
            }
        }
        
        log.info("Successfully soft-deleted property: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Property> searchByRadius(Double latitude, Double longitude, Double radiusKm) {
        log.debug("Searching properties within {}km of lat: {}, lng: {}", radiusKm, latitude, longitude);
        
        if (latitude == null || longitude == null || radiusKm == null) {
            throw new ValidationException("Latitude, longitude, and radius are required for location search");
        }
        
        return propertyRepository.findPropertiesWithinRadius(latitude, longitude, radiusKm);
    }

    @Override
    public void updateAverageRating(Long propertyId) {
        log.debug("Updating average rating for property: {}", propertyId);
        
        Property property = findPropertyById(propertyId);
        
        List<PropertyReview> reviews = reviewService.findReviewsByProperty(propertyId);
        
        if (reviews.isEmpty()) {
            property.setAverageRating(0.0f);
            property.setNumberOfReviews(0);
        } else {
            double averageRating = reviews.stream()
                    .filter(PropertyReview::getIsVerified) // Only count verified reviews
                    .mapToInt(PropertyReview::getRating)
                    .average()
                    .orElse(0.0);
            
            property.setAverageRating((float) averageRating);
            property.setNumberOfReviews(reviews.size());
        }
        
        property.setUpdatedAt(LocalDateTime.now());
        propertyRepository.save(property);
        
        log.debug("Updated rating for property {}: {} stars from {} reviews", 
                 propertyId, property.getAverageRating(), property.getNumberOfReviews());
    }

    @Override
    public void markAsUnavailable(Long propertyId, String managerClerkId) {
        log.info("Marking property {} as unavailable by manager: {}", propertyId, managerClerkId);
        
        Property property = findPropertyById(propertyId);
        verifyManagerOwnership(propertyId, managerClerkId);
        
        property.setIsAvailable(false);
        property.setUpdatedAt(LocalDateTime.now());
        propertyRepository.save(property);
        
        log.info("Successfully marked property {} as unavailable", propertyId);
    }

    @Override
    public void markAsAvailable(Long propertyId, String managerClerkId) {
        log.info("Marking property {} as available by manager: {}", propertyId, managerClerkId);
        
        Property property = findPropertyById(propertyId);
        verifyManagerOwnership(propertyId, managerClerkId);
        
        property.setIsAvailable(true);
        property.setUpdatedAt(LocalDateTime.now());
        propertyRepository.save(property);
        
        log.info("Successfully marked property {} as available", propertyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Property> getPropertiesByManager(String managerClerkId) {
        log.debug("Finding properties for manager: {}", managerClerkId);
        
        return propertyRepository.findByManagerClerkId(managerClerkId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Property> findAvailableProperties(Pageable pageable) {
        log.debug("Finding available properties");
        
        return propertyRepository.findByIsAvailableTrue(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public void verifyManagerOwnership(Long propertyId, String managerClerkId) {
        log.debug("Verifying manager {} owns property {}", managerClerkId, propertyId);
        
        Property property = findPropertyById(propertyId);
        
        if (!property.getManagerClerkId().equals(managerClerkId)) {
            throw new UnauthorizedException("Manager does not own this property");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Property> findPropertiesByLocation(String city, String state) {
        log.debug("Finding properties in {}, {}", city, state);
        
        return propertyRepository.findByLocationCityAndLocationState(city, state);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Property> findPropertiesByType(String propertyType) {
        log.debug("Finding properties of type: {}", propertyType);
        
        return propertyRepository.findByPropertyType(propertyType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Property> findPropertiesByPriceRange(Double minPrice, Double maxPrice) {
        log.debug("Finding properties in price range: {} - {}", minPrice, maxPrice);
        
        BigDecimal min = minPrice != null ? BigDecimal.valueOf(minPrice) : BigDecimal.ZERO;
        BigDecimal max = maxPrice != null ? BigDecimal.valueOf(maxPrice) : BigDecimal.valueOf(Double.MAX_VALUE);
        
        return propertyRepository.findByPricePerMonthBetween(min, max);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Property> findPropertiesByBedroomsBathrooms(Integer minBeds, Float minBaths) {
        log.debug("Finding properties with min beds: {}, min baths: {}", minBeds, minBaths);
        
        int beds = minBeds != null ? minBeds : 0;
        float baths = minBaths != null ? minBaths : 0.0f;
        
        return propertyRepository.findByBedsGreaterThanEqualAndBathsGreaterThanEqual(beds, baths);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyStatisticsDto getPropertyStatistics(String managerClerkId) {
        log.debug("Getting property statistics for manager: {}", managerClerkId);
        
        List<Property> properties = getPropertiesByManager(managerClerkId);
        
        long totalProperties = properties.size();
        long availableProperties = properties.stream()
                .mapToLong(p -> p.getIsAvailable() ? 1 : 0)
                .sum();
        long occupiedProperties = totalProperties - availableProperties;
        
        double averageRent = properties.stream()
                .filter(Property::getIsAvailable)
                .mapToDouble(p -> p.getPricePerMonth().doubleValue())
                .average()
                .orElse(0.0);
        
        double averageRating = properties.stream()
                .mapToDouble(Property::getAverageRating)
                .average()
                .orElse(0.0);
        
        // Get pending applications count
        long pendingApplications = properties.stream()
                .mapToLong(p -> applicationRepository.countByPropertyIdAndStatus(p.getId(), "PENDING"))
                .sum();
        
        return PropertyStatisticsDto.builder()
                .totalProperties((int) totalProperties)
                .availableProperties((int) availableProperties)
                .occupiedProperties((int) occupiedProperties)
                .averageRent(BigDecimal.valueOf(averageRent))
                .averageRating((float) averageRating)
                .pendingApplications((int) pendingApplications)
                .build();
    }

    @Override
    public Property updatePropertyPhotos(Long propertyId, List<String> photoUrls, String managerClerkId) {
        log.info("Updating photos for property {} by manager: {}", propertyId, managerClerkId);
        
        Property property = findPropertyById(propertyId);
        verifyManagerOwnership(propertyId, managerClerkId);
        
        // Delete old photos
        if (property.getPhotoUrls() != null) {
            for (String oldPhotoUrl : property.getPhotoUrls()) {
                try {
                    fileStorageService.deleteFile(extractS3KeyFromUrl(oldPhotoUrl));
                } catch (Exception e) {
                    log.warn("Failed to delete old photo: {}", oldPhotoUrl, e);
                }
            }
        }
        
        // Set new photos
        property.setPhotoUrls(photoUrls.toArray(new String[0]));
        property.setUpdatedAt(LocalDateTime.now());
        
        Property updatedProperty = propertyRepository.save(property);
        log.info("Successfully updated photos for property: {}", propertyId);
        
        return updatedProperty;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Property> findRecentlyPostedProperties(Integer days, Pageable pageable) {
        log.debug("Finding properties posted in last {} days", days);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days != null ? days : 7);
        return propertyRepository.findByPostedDateAfter(cutoffDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Property> findHighlyRatedProperties(Float minRating, Pageable pageable) {
        log.debug("Finding properties with rating >= {}", minRating);
        
        float rating = minRating != null ? minRating : 4.0f;
        return propertyRepository.findByAverageRatingGreaterThanEqual(rating, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPropertyAvailableForApplications(Long propertyId) {
        log.debug("Checking if property {} is available for applications", propertyId);
        
        Property property = findPropertyById(propertyId);
        return property.getIsAvailable() && 
               !leaseRepository.existsActiveLeaseByPropertyId(propertyId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getPropertyCountByManager(String managerClerkId) {
        log.debug("Getting property count for manager: {}", managerClerkId);
        
        return propertyRepository.countByManagerClerkId(managerClerkId);
    }

    @Override
    public Integer archiveInactiveProperties(Integer daysInactive) {
        log.info("Archiving properties inactive for {} days", daysInactive);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysInactive != null ? daysInactive : 90);
        List<Property> inactiveProperties = propertyRepository.findInactiveProperties(cutoffDate);
        
        int archivedCount = 0;
        for (Property property : inactiveProperties) {
            // Only archive if no active leases
            if (!leaseRepository.existsActiveLeaseByPropertyId(property.getId())) {
                property.setIsAvailable(false);
                property.setUpdatedAt(LocalDateTime.now());
                propertyRepository.save(property);
                archivedCount++;
            }
        }
        
        log.info("Archived {} inactive properties", archivedCount);
        return archivedCount;
    }

    // Private helper methods
    
    private Specification<Property> buildPropertySpecification(PropertyFilterDto filters) {
        return (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();
            
            if (StringUtils.hasText(filters.getCity())) {
                predicates = criteriaBuilder.and(predicates,
                    criteriaBuilder.equal(root.get("location").get("city"), filters.getCity()));
            }
            
            if (StringUtils.hasText(filters.getState())) {
                predicates = criteriaBuilder.and(predicates,
                    criteriaBuilder.equal(root.get("location").get("state"), filters.getState()));
            }
            
            if (StringUtils.hasText(filters.getPropertyType())) {
                predicates = criteriaBuilder.and(predicates,
                    criteriaBuilder.equal(root.get("propertyType"), filters.getPropertyType()));
            }
            
            if (filters.getMinPrice() != null) {
                predicates = criteriaBuilder.and(predicates,
                    criteriaBuilder.greaterThanOrEqualTo(root.get("pricePerMonth"), 
                        BigDecimal.valueOf(filters.getMinPrice())));
            }
            
            if (filters.getMaxPrice() != null) {
                predicates = criteriaBuilder.and(predicates,
                    criteriaBuilder.lessThanOrEqualTo(root.get("pricePerMonth"), 
                        BigDecimal.valueOf(filters.getMaxPrice())));
            }
            
            if (filters.getMinBeds() != null) {
                predicates = criteriaBuilder.and(predicates,
                    criteriaBuilder.greaterThanOrEqualTo(root.get("beds"), filters.getMinBeds()));
            }
            
            if (filters.getMaxBeds() != null) {
                predicates = criteriaBuilder.and(predicates,
                    criteriaBuilder.lessThanOrEqualTo(root.get("beds"), filters.getMaxBeds()));
            }
            
            if (filters.getPetsAllowed() != null && filters.getPetsAllowed()) {
                predicates = criteriaBuilder.and(predicates,
                    criteriaBuilder.isTrue(root.get("isPetsAllowed")));
            }
            
            if (filters.getParkingIncluded() != null && filters.getParkingIncluded()) {
                predicates = criteriaBuilder.and(predicates,
                    criteriaBuilder.isTrue(root.get("isParkingIncluded")));
            }
            
            return predicates;
        };
    }
    
    private void validatePropertyData(Property property) {
        if (!StringUtils.hasText(property.getName())) {
            throw new ValidationException("Property name is required");
        }
        
        if (property.getPricePerMonth() == null || property.getPricePerMonth().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price per month must be greater than 0");
        }
        
        if (property.getBeds() == null || property.getBeds() < 0) {
            throw new ValidationException("Number of bedrooms must be 0 or greater");
        }
        
        if (property.getBaths() == null || property.getBaths() < 0) {
            throw new ValidationException("Number of bathrooms must be 0 or greater");
        }
        
        if (property.getLocation() == null) {
            throw new ValidationException("Property location is required");
        }
    }
    
    private void updatePropertyFields(Property property, UpdatePropertyDto updateDto) {
        if (StringUtils.hasText(updateDto.getName())) {
            property.setName(updateDto.getName());
        }
        
        if (StringUtils.hasText(updateDto.getDescription())) {
            property.setDescription(updateDto.getDescription());
        }
        
        if (updateDto.getPricePerMonth() != null) {
            property.setPricePerMonth(updateDto.getPricePerMonth());
        }
        
        if (updateDto.getSecurityDeposit() != null) {
            property.setSecurityDeposit(updateDto.getSecurityDeposit());
        }
        
        if (updateDto.getApplicationFee() != null) {
            property.setApplicationFee(updateDto.getApplicationFee());
        }
        
        if (updateDto.getAmenities() != null) {
            property.setAmenities(updateDto.getAmenities());
        }
        
        if (updateDto.getHighlights() != null) {
            property.setHighlights(updateDto.getHighlights());
        }
        
        if (updateDto.getIsPetsAllowed() != null) {
            property.setIsPetsAllowed(updateDto.getIsPetsAllowed());
        }
        
        if (updateDto.getIsParkingIncluded() != null) {
            property.setIsParkingIncluded(updateDto.getIsParkingIncluded());
        }
        
        if (updateDto.getBeds() != null) {
            property.setBeds(updateDto.getBeds());
        }
        
        if (updateDto.getBaths() != null) {
            property.setBaths(updateDto.getBaths());
        }
        
        if (updateDto.getSquareFeet() != null) {
            property.setSquareFeet(updateDto.getSquareFeet());
        }
        
        if (updateDto.getLocation() != null) {
            propertyMapper.updateLocationFromDto(property.getLocation(), updateDto.getLocation());
        }
    }
    
    private String extractS3KeyFromUrl(String s3Url) {
        // Extract S3 key from CloudFront or S3 URL
        // Implementation depends on your URL structure
        if (s3Url.contains("/")) {
            return s3Url.substring(s3Url.lastIndexOf("/") + 1);
        }
        return s3Url;
    }
}