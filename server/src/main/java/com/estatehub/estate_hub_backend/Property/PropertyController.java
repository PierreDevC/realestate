package com.estatehub.estate_hub_backend.Property;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

/**
 * Contrôleur REST pour la gestion des propriétés
 * Expose les endpoints pour les opérations CRUD et recherches sur les propriétés
 */
@RestController
@RequestMapping("/api/properties")
@Validated
@CrossOrigin(origins = "*")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private PropertyMapper propertyMapper;

    /**
     * Récupère toutes les propriétés avec pagination et filtres
     * 
     * @param page Numéro de page (défaut: 0)
     * @param size Taille de page (défaut: 10)
     * @param sortBy Champ de tri (défaut: id)
     * @param sortDir Direction de tri (défaut: asc)
     * @param minPrice Prix minimum
     * @param maxPrice Prix maximum
     * @param beds Nombre minimum de chambres
     * @param baths Nombre minimum de salles de bain
     * @param propertyType Type de propriété
     * @param city Ville
     * @param isAvailable Disponibilité
     * @return Page de PropertyDto
     */
    @GetMapping
    public ResponseEntity<Page<PropertyDto>> getProperties(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) @DecimalMin("0.0") Double minPrice,
            @RequestParam(required = false) @DecimalMin("0.0") Double maxPrice,
            @RequestParam(required = false) @Min(0) Integer beds,
            @RequestParam(required = false) @DecimalMin("0.0") Float baths,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean isAvailable) {

        // Configuration de la pagination et du tri
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // TODO: Implémenter les filtres dans le service
        // Pour l'instant, retourne toutes les propriétés
        Page<Property> propertyPage = propertyService.findAllProperties(pageable);
        Page<PropertyDto> propertyDtoPage = propertyMapper.toDtoPage(propertyPage);
        
        return ResponseEntity.ok(propertyDtoPage);
    }

    /**
     * Récupère une propriété par son ID
     * 
     * @param id ID de la propriété
     * @return PropertyDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<PropertyDto> getPropertyById(@PathVariable @Min(1) Long id) {
        Property property = propertyService.findPropertyById(id);
        PropertyDto propertyDto = propertyMapper.toDto(property);
        return ResponseEntity.ok(propertyDto);
    }

    /**
     * Crée une nouvelle propriété
     * 
     * @param propertyDto Données de la propriété à créer
     * @return PropertyDto de la propriété créée
     */
    @PostMapping
    public ResponseEntity<PropertyDto> createProperty(@Valid @RequestBody PropertyDto propertyDto) {
        Property property = propertyMapper.toNewEntity(propertyDto);
        Property savedProperty = propertyService.createProperty(property);
        PropertyDto savedPropertyDto = propertyMapper.toDto(savedProperty);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPropertyDto);
    }

    /**
     * Met à jour une propriété existante
     * 
     * @param id ID de la propriété à mettre à jour
     * @param propertyDto Nouvelles données de la propriété
     * @return PropertyDto de la propriété mise à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<PropertyDto> updateProperty(
            @PathVariable @Min(1) Long id, 
            @Valid @RequestBody PropertyDto propertyDto) {
        
        Property updatedProperty = propertyService.updateProperty(id, propertyMapper.toEntity(propertyDto));
        PropertyDto updatedPropertyDto = propertyMapper.toDto(updatedProperty);
        return ResponseEntity.ok(updatedPropertyDto);
    }

    /**
     * Supprime une propriété
     * 
     * @param id ID de la propriété à supprimer
     * @return Réponse vide
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable @Min(1) Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Recherche des propriétés dans un rayon géographique
     * 
     * @param lat Latitude
     * @param lng Longitude
     * @param radius Rayon en kilomètres
     * @return Liste des propriétés dans le rayon
     */
    @GetMapping("/search/location")
    public ResponseEntity<List<PropertyDto>> searchPropertiesByLocation(
            @RequestParam @DecimalMin("-90.0") @DecimalMin("90.0") Double lat,
            @RequestParam @DecimalMin("-180.0") @DecimalMin("180.0") Double lng,
            @RequestParam @DecimalMin("0.0") Double radius) {
        
        List<Property> properties = propertyService.searchByRadius(lat, lng, radius);
        List<PropertyDto> propertyDtos = propertyMapper.toDtoList(properties);
        return ResponseEntity.ok(propertyDtos);
    }

    // ===== MÉTHODES COMMENTÉES - ENTITÉS NON IMPLÉMENTÉES =====

    /**
     * Récupère les candidatures pour une propriété
     * TODO: Décommenter quand Application et ApplicationDto seront implémentés
     * 
     * @param id ID de la propriété
     * @return Liste des candidatures
     */
    /*
    @GetMapping("/{id}/applications")
    public ResponseEntity<List<ApplicationDto>> getPropertyApplications(@PathVariable @Min(1) Long id) {
        // TODO: Implémenter quand ApplicationService sera disponible
        // List<Application> applications = applicationService.findApplicationsByProperty(id);
        // List<ApplicationDto> applicationDtos = applicationMapper.toDtoList(applications);
        // return ResponseEntity.ok(applicationDtos);
        return ResponseEntity.ok(List.of());
    }
    */

    /**
     * Récupère les avis pour une propriété
     * TODO: Décommenter quand PropertyReview et ReviewDto seront implémentés
     * 
     * @param id ID de la propriété
     * @return Liste des avis
     */
    /*
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDto>> getPropertyReviews(@PathVariable @Min(1) Long id) {
        // TODO: Implémenter quand ReviewService sera disponible
        // List<PropertyReview> reviews = reviewService.findReviewsByProperty(id);
        // List<ReviewDto> reviewDtos = reviewMapper.toDtoList(reviews);
        // return ResponseEntity.ok(reviewDtos);
        return ResponseEntity.ok(List.of());
    }
    */

    /**
     * Récupère les demandes de maintenance pour une propriété
     * TODO: Décommenter quand MaintenanceRequest et MaintenanceRequestDto seront implémentés
     * 
     * @param id ID de la propriété
     * @return Liste des demandes de maintenance
     */
    /*
    @GetMapping("/{id}/maintenance-requests")
    public ResponseEntity<List<MaintenanceRequestDto>> getPropertyMaintenanceRequests(@PathVariable @Min(1) Long id) {
        // TODO: Implémenter quand MaintenanceService sera disponible
        // List<MaintenanceRequest> maintenanceRequests = maintenanceService.findRequestsByProperty(id);
        // List<MaintenanceRequestDto> maintenanceRequestDtos = maintenanceMapper.toDtoList(maintenanceRequests);
        // return ResponseEntity.ok(maintenanceRequestDtos);
        return ResponseEntity.ok(List.of());
    }
    */

    // ===== ENDPOINTS UTILITAIRES =====

    /**
     * Marque une propriété comme indisponible
     * 
     * @param id ID de la propriété
     * @return Réponse vide
     */
    @PatchMapping("/{id}/mark-unavailable")
    public ResponseEntity<Void> markPropertyAsUnavailable(@PathVariable @Min(1) Long id) {
        propertyService.markAsUnavailable(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Met à jour la note moyenne d'une propriété
     * TODO: Utiliser quand le système d'avis sera implémenté
     * 
     * @param id ID de la propriété
     * @return Réponse vide
     */
    @PatchMapping("/{id}/update-rating")
    public ResponseEntity<Void> updatePropertyAverageRating(@PathVariable @Min(1) Long id) {
        propertyService.updateAverageRating(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Récupère les propriétés d'un gestionnaire
     * 
     * @param managerClerkId ID Clerk du gestionnaire
     * @return Liste des propriétés du gestionnaire
     */
    @GetMapping("/manager/{managerClerkId}")
    public ResponseEntity<List<PropertyDto>> getPropertiesByManager(@PathVariable String managerClerkId) {
        List<Property> properties = propertyService.getPropertiesByManager(managerClerkId);
        List<PropertyDto> propertyDtos = propertyMapper.toDtoList(properties);
        return ResponseEntity.ok(propertyDtos);
    }
}