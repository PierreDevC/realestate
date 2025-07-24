package com.estatehub.estate_hub_backend.Property;

import com.estatehub.estate_hub_backend.enums.PropertyType;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import com.estatehub.estate_hub_backend.Location.Location;
import com.estatehub.estate_hub_backend.Manager.Manager;
import com.estatehub.estate_hub_backend.Location.LocationDto;
import com.estatehub.estate_hub_backend.Manager.ManagerDto;

@Mapper(componentModel = "spring")
public interface PropertyMapper {

    // ===== CONVERSIONS PRINCIPALES =====
    
    /**
     * Convertit une entité Property en PropertyDto
     */
    @Mapping(target = "propertyType", source = "propertyType", qualifiedByName = "enumToString")
    @Mapping(target = "location", source = "location", qualifiedByName = "locationToDto")
    @Mapping(target = "manager", source = "manager", qualifiedByName = "managerToDto")
    PropertyDto toDto(Property property);

    /**
     * Convertit un PropertyDto en entité Property
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postedDate", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "numberOfReviews", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "propertyType", source = "propertyType", qualifiedByName = "stringToEnum")
    Property toEntity(PropertyDto dto);

    // ===== CONVERSIONS DE COLLECTIONS =====
    
    /**
     * Convertit une liste d'entités Property en liste de PropertyDto
     */
    List<PropertyDto> toDtoList(List<Property> properties);

    /**
     * Convertit une Page d'entités Property en Page de PropertyDto
     */
    default Page<PropertyDto> toDtoPage(Page<Property> propertyPage) {
        List<PropertyDto> dtos = toDtoList(propertyPage.getContent());
        return new PageImpl<>(dtos, propertyPage.getPageable(), propertyPage.getTotalElements());
    }

    // ===== MÉTHODES DE MAPPING PERSONNALISÉES =====

    /**
     * Convertit PropertyType enum vers String
     */
    @Named("enumToString")
    default String propertyTypeToString(PropertyType propertyType) {
        return propertyType != null ? propertyType.name() : null;
    }

    /**
     * Convertit String vers PropertyType enum
     */
    @Named("stringToEnum")
    default PropertyType stringToPropertyType(String propertyType) {
        if (propertyType == null || propertyType.isBlank()) {
            return null;
        }
        try {
            return PropertyType.valueOf(propertyType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Convertit Location vers LocationDto
     */
    @Named("locationToDto")
    default LocationDto locationToDto(Location location) {
        if (location == null) return null;
        
        Double lat = null, lng = null;
        if (location.getCoordinates() != null) {
            lat = location.getCoordinates().getY();
            lng = location.getCoordinates().getX();
        }
        
        return new LocationDto(
            location.getId(),
            location.getAddress(),
            location.getCity(),
            location.getState(),
            location.getCountry(),
            location.getPostalCode(),
            lat,
            lng
        );
    }

    /**
     * Convertit Manager vers ManagerDto
     */
    @Named("managerToDto")
    default ManagerDto managerToDto(Manager manager) {
        if (manager == null) return null;
        
        return new ManagerDto(
            manager.getId(),
            manager.getName(),
            manager.getEmail(),
            manager.getPhoneNumber()
        );
    }

    // ===== MÉTHODES DE MISE À JOUR =====

    /**
     * Met à jour une entité Property existante avec les données du DTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postedDate", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "numberOfReviews", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "propertyType", source = "propertyType", qualifiedByName = "stringToEnum")
    void updateEntityFromDto(PropertyDto dto, @MappingTarget Property property);

    /**
     * Mapping pour la création (sans ID ni timestamps)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postedDate", ignore = true)
    @Mapping(target = "averageRating", constant = "0.0f")
    @Mapping(target = "numberOfReviews", constant = "0")
    @Mapping(target = "isAvailable", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "propertyType", source = "propertyType", qualifiedByName = "stringToEnum")
    Property toNewEntity(PropertyDto dto);
}