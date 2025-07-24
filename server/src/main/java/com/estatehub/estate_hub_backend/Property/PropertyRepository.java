package com.estatehub.estate_hub_backend.Property;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    /**
     * Find all properties with pagination
     */
    Page<Property> findAll(Pageable pageable);

    /**
     * Find property by ID
     */
    Optional<Property> findById(Long id);

    /**
     * Find properties by manager clerk ID
     */
    @Query("SELECT p FROM Property p WHERE p.manager.clerkId = :managerClerkId")
    List<Property> findByManagerClerkId(@Param("managerClerkId") String managerClerkId);

    /**
     * Find available properties only
     */
    List<Property> findByIsAvailableTrue();

    /**
     * Find properties by price range
     */
    List<Property> findByPricePerMonthBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Find properties by type
     */
    List<Property> findByPropertyType(String propertyType);

    /**
     * Find properties within radius using PostGIS
     */
    @Query(value = """
        SELECT p.* FROM properties p 
        JOIN locations l ON p.location_id = l.id 
        WHERE ST_DWithin(
            l.coordinates, 
            ST_MakePoint(:longitude, :latitude)::geography, 
            :radiusKm * 1000
        )
        """, nativeQuery = true)
    List<Property> findPropertiesWithinRadius(
        @Param("latitude") Double latitude, 
        @Param("longitude") Double longitude, 
        @Param("radiusKm") Double radiusKm
    );

    /**
     * Find properties by minimum beds and baths
     */
    List<Property> findByBedsGreaterThanEqualAndBathsGreaterThanEqual(Integer beds, Float baths);

    /**
     * Find properties by location (city and state)
     */
    @Query("SELECT p FROM Property p WHERE p.location.city = :city AND p.location.state = :state")
    List<Property> findByLocationCityAndLocationState(@Param("city") String city, @Param("state") String state);
}